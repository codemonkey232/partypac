package nz.ac.auckland.partypac.core;

import playn.core.Image;
import playn.core.Platform;
import playn.core.Sound;
import playn.core.Surface;
import playn.core.Key;
import pythagoras.i.Point;

public class Game {

  // ********** Constants **********
  public static final int
    NORMAL = 0, WAIT_TO_GO = 1, WAIT_TO_DIE = 2, GAME_OVER = 3, PAUSED = 4,
    GO_TIME = 50, DIE_TIME = 50, GOODIE_TIME = 400;

  public static final int MAX_PACMEN = 4;
  public static boolean MUSIC_ON = true;

  public static final int[] LIVES = new int[] {5, 3, 2, 1};

  public static final Point LIFE_POINT = new Point(0, Layout.MAZE_HEIGHT_PX + 25);
  public static final Point TO_WIN_POINT = new Point(624, Layout.MAZE_HEIGHT_PX + 25);

  // ********** Instance Variables **********
  private Maze maze;
  public Sound song;
  private PacMan[] pacmen;
  private Ghost[] ghosts;
  private Menu menu;
  private Point[] goodiePoints;
  private int level, status, lives, winsToGo,
        numPlayers, numGhosts, numGoodies,
        waitUntil, goodieUntil, spot;

  // ********** Constructor **********
  public Game (Menu menu) {
    this.menu = menu;

    numPlayers = 0;
    status = GAME_OVER;
    winsToGo = -1;
    lives = -1;
  }

  // ********** New Game Method **********
  public void newGame(int numPlayers) {
    this.numPlayers = numPlayers;
    this.lives = LIVES[numPlayers - 1];
    
    for (int i = 0; i < MAX_PACMEN; i++) {
      pacmen[i].resetScore();
      pacmen[i].setStatus(i < numPlayers ? PacMan.INVINCIBLE : PacMan.NOT_PLAYING);
    }
    level = 0;
    getReady();
  }

  public PacMan[] getPacmen() {return pacmen;}
  public int getNumPlayers() {return numPlayers;}
  public int status() { return status; }

  public void draw(Surface s) {
    if (!PartyPac.isLoaded() || maze == null || pacmen == null) {
      return;
    }

    Background.draw(s);
    maze.draw(s);
    maze.drawPickUps(s);
    for (int i = 0; i < numPlayers; i++) {
      pacmen[i].draw(s);
    }
    for (int i = 0; i < numGhosts; i++) {
      ghosts[i].draw(s);
    }
    Risers.draw(s);
    s.setFillColor(0xff000000);
    s.fillRect(0, Layout.MAZE_HEIGHT_PX, Layout.MAZE_WIDTH_PX, Layout.HUD_HEIGHT_PX);
    for (int i = 0; i < numPlayers; i++) {
      pacmen[i].maybeDrawAngels(s);
      pacmen[i].drawScore(s);
    }
    if (status != GAME_OVER) {
      for (int n = 0; n < lives; n++) {
        s.draw(Images.life.texture(), LIFE_POINT.x + 16 * n, LIFE_POINT.y);
      }
      for (int n = 0; n < winsToGo; n++) {
        s.draw(Images.lifeSaver.texture(), TO_WIN_POINT.x - 16 * n, TO_WIN_POINT.y);
      }
    }
  }

  public void type(Key key) {
    for (Control control : Control.CONTROLS) {
      if (key == control.key) {
        int player = Math.min(control.player, numPlayers - 1);
        pacmen[player].requestDirection(control.direction);
        return;
      }
    }
    if (key == Key.F7 && status == NORMAL && (level + 1) < Levels.NUM_LEVELS) {
      song.stop();
      level++;
      getReady();
    }
    if (isPause(key) && status == NORMAL) {
      status = PAUSED;
      menu.statusUpdated();
    } else if (isPause(key) && status == PAUSED) {
      status = NORMAL;
      menu.statusUpdated();
    }
  }
  
  private static boolean isPause(Key key) {
    return key == Key.PAUSE || key == Key.P;
  }

  public void tick() {
    if (!PartyPac.isLoaded()) {
      return;
    }

    if (maze == null) {
      pacmen = new PacMan[] {
        new PacMan("pacyellow.gif", 0xffffff00, 0),
        new PacMan("pacorange.gif", 0xffffaa00, 1),
        new PacMan("pacgreen.gif", 0xff77ff00, 2),
        new PacMan("pacred.gif", 0xffff5500, 3)
      };

      level = (int)(Levels.DEMO_LEVELS * Math.random());
      loadLevel(Levels.LEVEL_FILE_NAMES[level]);
      menu.statusUpdated();
    }

    if (status == PAUSED) {
      return;
    }

    waitUntil--;
    if (waitUntil == 0)
      if (status == WAIT_TO_DIE) {
        Sounds.lifeLost.play();
        lives--;
        if (lives == 0) {
          song.stop();
          status = GAME_OVER;
          menu.setStatus(Menu.GAME_OVER);
          for (int n = 0; n < numPlayers; n++)
            pacmen[n].setStatus(PacMan.GAME_OVER);
        } else {
          status = NORMAL;
          for (int n = 0; n < numPlayers; n++) 
            pacmen[n].setStatus(PacMan.WAITING);
        }
      } else if (status == WAIT_TO_GO) {
        status = NORMAL;
        Sounds.levelStart.play();
      }

    Background.tick();
    Risers.tick();
    int playersDead = 0;
    if (status != WAIT_TO_GO) {
      for (int n = 0; n < numPlayers; n++) {
        pacmen[n].tick(maze, ghosts);
        if (pacmen[n].getStatus() == PacMan.DEAD) {
          playersDead ++;
        }
      }
      if (playersDead == numPlayers && status == NORMAL) {
        status = WAIT_TO_DIE;
        waitUntil = DIE_TIME;
      } else if (playersDead < numPlayers && status == WAIT_TO_DIE) {
        status = NORMAL;
      }

      for (int n = 0; n < numGhosts; n++) {
        ghosts[n].tick(maze, numPlayers, pacmen);
      }
      Ghost.SLOW_UNTIL--;
    }

    if (maze.getDotsLeft() == 0 && status == NORMAL) {
      winsToGo--;
      if (winsToGo == 0) {
        level++;
        if (song != null) {
          song.stop();
        }
        if (level < Levels.NUM_LEVELS) {
          lives++;
          Sounds.levelWon.play();
          getReady();
        } else {
          Sounds.gameWon.play();
          menu.setStatus(Menu.GAME_WON);
          status = GAME_OVER;
          for (int n = 0; n < numPlayers; n++)
            pacmen[n].setStatus(PacMan.GAME_WON);
        }
      } else {
        Sounds.dotsEaten.play();
        maze.reDot();
      }
    }

    goodieUntil--;
    if (goodieUntil == 0)
      if (spot == -1) {
        if (status == NORMAL) {
          Sounds.pickupSpawn.play();
          spot = (int)(numGoodies * Math.random());
          maze.setPickup(goodiePoints[spot], (int)(5 * Math.random()) + 3);
          goodieUntil = GOODIE_TIME;
        }
      } else {
        maze.setPickup(goodiePoints[spot], 0);
        spot = -1;
        goodieUntil = (int)(500 * Math.random() + 500);
      }

  }

  // ********** Private Methods **********

  private void getReady() {
    spot = -1;
    goodieUntil = (int)(500 * Math.random() + 500);
    status = WAIT_TO_GO;
    waitUntil = GO_TIME;
    loadLevel(Levels.LEVEL_FILE_NAMES[level]);
    if (status != GAME_OVER && MUSIC_ON && song != null) {
      song.play();
    }
    winsToGo = Math.max(numPlayers - 1, 1);
  }

  // ********** Loads a level **********
  private void loadLevel(String levelFileName) {
    if (song != null) {
      song.stop();
    }

    DataSet d = new DataSet(levelFileName);
    maze = new Maze(d.readString());
    song = Levels.MUSICS.get(d.readString());
    Background.setColour(d.readBoolean(), d.readBoolean(), d.readColour(), d.readColour(), d.readColour());
    for (int n = 0; n < MAX_PACMEN; n++)
      pacmen[n].spawnAt(d.readLocation());
    numGhosts = d.readInt();
    ghosts = new Ghost[numGhosts];
    for (int i = 0; i < numGhosts; i++) {
      ghosts[i] = new Ghost(d.readString(), d.readString(), d.readLocation());
    }
    numGoodies = d.readInt();
    goodiePoints = new Point[numGoodies];
    for (int n = 0; n < numGoodies; n++)
      goodiePoints[n] = d.readLocation();
  }

  public float width() {
    return Layout.VIEW_WIDTH_PX;
  }

  public float height() {
    return Layout.VIEW_WIDTH_PX;
  }
}
