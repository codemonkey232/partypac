package nz.ac.auckland.partypac.core;

import playn.core.Image;
import playn.core.Tile;
import playn.core.Surface;
import playn.core.Canvas;
import playn.core.Texture;
import playn.core.TextFormat;
import playn.core.Font;
import pythagoras.i.Point;

public class PacMan {

  // ********** Constants **********
  public static final int
    INVINCIBLE = -1, ALIVE = 0, FAST = 1, DIZZY = 2, DEAD = 3, WAITING = 4, GAME_OVER = 5, GAME_WON = 6, NOT_PLAYING = 7,
    SPAWN_GRACE = 100, FLASH_PERIOD = 5, PICKUP_TIME = 500, DEAD_TIME = 500, WAIT_TIME = 100,
    ANGEL_SPEED = 5, ANGEL_A = 10, ANGLE_W = 2,
    SPAWN_PREPLAY = 20;

  public static final int SIZE = 42;
  public static final int INITIAL_SPEED = 4;

  public static final TextFormat TEXT_FORMAT = new TextFormat(new Font("Courier", Font.Style.BOLD, 18), true);

  private Point position, respawnPoint;
  private int actualDirection = 0, requestedDirection = 0,
        speed = INITIAL_SPEED, status = -1, waitUntil, player,
        score, ghostScore, dotScore, deaths;
  private int colour;
  private Tile[] pacTiles;
  private Canvas canvas;
  private Texture scoreTexture;

  public PacMan(String imageFile, int colour, int player) {
    this.pacTiles = TileSet.divideImageTiles(Images.IMAGES.get(imageFile), SIZE);
    this.status = NOT_PLAYING;
    this.colour = colour;
    this.player = player;
    spawnAt(new Point(32, 32));
    canvas = PartyPac.plat.graphics().createCanvas(Layout.MAZE_WIDTH_PX / 4, Layout.HUD_HEIGHT_PX);
    updateScore();
  }

  public void spawnAt(Point spawnPoint) {
    respawnPoint = Maze.getPosition(spawnPoint);
    respawn();
  }

  public void respawn() {
    if (status == NOT_PLAYING) {
      return;
    }
    status = INVINCIBLE;
    waitUntil = SPAWN_GRACE;
    speed = INITIAL_SPEED;
    position = new Point(respawnPoint.x, respawnPoint.y);
    actualDirection = Direction.STOPPED;
    requestedDirection = Direction.STOPPED;
  }

  public void setStatus(int status) {
    if (status == DEAD) {
      waitUntil = DEAD_TIME;
      Sounds.pacDeath.play();
      Risers.riser(position, "-500", colour);
      score -= 500;
      updateScore();
      deaths++;
    } else if (status == WAITING)
      waitUntil = WAIT_TIME;
    else if (status == GAME_OVER || status == GAME_WON)
      waitUntil = -1;
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public void setPosition(Point position) {
    this.position = position;
  }

  public Point getPosition() {
    return position;
  }

  public int getActualDirection() {
    return actualDirection;
  }

  public void requestDirection(int direction) {
    if (status == DIZZY)
      requestedDirection = Direction.D[direction].opposite;
    else
      requestedDirection = direction;
  }

  public int getRequestedDirection() {
    return requestedDirection;
  }

  public void resetScore() {
    score = 0;
    dotScore = 0;
    ghostScore = 0;
    deaths = 0;
    updateScore();
  }

  public int getScore() {return score;}
  public int getGhostScore() {return ghostScore;}
  public int getDotScore() {return dotScore;}
  public int getDeaths() {return deaths;}
  public int getColour() {return colour;}
  public Tile getFace() {return pacTiles[6];}

  public void draw(Surface s) {
    if (status == NOT_PLAYING) {
      return;
    }

    boolean flashOn = (waitUntil / FLASH_PERIOD) % 2 == 0;
    Tile tile = null;
    if (status == ALIVE || status == FAST || status == DIZZY
      || (status == INVINCIBLE && flashOn)) {
      int mouthType = 4 * Maze.getNodeDist(position) / Maze.SIZE;
      mouthType = Math.abs(2 - mouthType);

      int faceDirection = actualDirection;
      if (faceDirection == Direction.STOPPED) {
        faceDirection = requestedDirection;
      }
      if (faceDirection == Direction.STOPPED || faceDirection == Direction.UP) {
        mouthType = 0;
      }
      int faceType = faceDirection == Direction.STOPPED ? 2 : faceDirection - 1;

      tile = pacTiles[faceType * 3 + mouthType];
    } else if (status == DEAD || status == WAITING || status == GAME_OVER) {
      tile = pacTiles[2];
    }

    if (tile != null) {
      s.draw(tile, position.x - SIZE/2, position.y - SIZE/2);
    }
  }

  public void maybeDrawAngels(Surface s) {
    if (status == DEAD || status == WAITING) {
      drawAngel(s, position.x, position.y - ANGEL_SPEED * (DEAD_TIME - waitUntil));
      drawAngel(s, respawnPoint.x, respawnPoint.y - ANGEL_SPEED * waitUntil);
    }
    if (status == GAME_WON) {
      drawAngel(s, position.x, position.y + ANGEL_SPEED * (waitUntil));
    }
  }

  public void drawScore(Surface s) {
    s.draw(canvas.image.texture(), player * Layout.MAZE_WIDTH_PX / 4, Layout.MAZE_HEIGHT_PX);
  }

  private void updateScore() {
    canvas.clear();
    canvas.setFillColor(colour);
    canvas.fillText(PartyPac.plat.graphics().layoutText(stringOf(score), TEXT_FORMAT), 0, 5);
    scoreTexture = canvas.image.texture();
    scoreTexture.update(canvas.image);
  }

  public void eatGhost() {
    Sounds.ghostEaten.play();
    Risers.riser(position, "500", colour);
    ghostScore++;
    score += 500;
    updateScore();
  }

  public void tick(Maze maze, Ghost[] ghosts) {
    if (status == NOT_PLAYING) {
      return;
    }

    waitUntil--;
    if (waitUntil == SPAWN_PREPLAY && status >= 3) {
      Sounds.pacSpawn.play();
    }
    if (waitUntil == 0) {
      if (status == INVINCIBLE || status == FAST || status == DIZZY) status = ALIVE;
      else if (status == DEAD || status == WAITING) respawn();
    }
    if (status <= 2) {
      checkDirection(maze, ghosts);
      position.x = DataSet.loopChange(position.x, Direction.D[actualDirection].x * speed, Layout.MIN_X, Layout.MAX_X);
      position.y = DataSet.loopChange(position.y, Direction.D[actualDirection].y * speed, Layout.MIN_Y, Layout.MAX_Y);
    }
  }

  // ********** Private methods **********
  private void checkDirection(Maze maze, Ghost[] ghosts) {
    Point node = Maze.getNode(position);
    if (node == null) {
      if (requestedDirection == Direction.D[actualDirection].opposite) //180's allowed anywhere
          actualDirection = requestedDirection;
    } else {
      eatPickUp(maze, node, ghosts);
      setSpeed();
      int path = maze.getPath(node);
      if (Maze.bitIsOn(path, Direction.D[requestedDirection].bit)) //Can go requested direction
        actualDirection = requestedDirection;
      else if (!Maze.bitIsOn(path, Direction.D[actualDirection].bit)) //Cannot go requested nor actual direction
        actualDirection = Direction.STOPPED;
    }
  }

  private void setSpeed() {
    if (status == FAST)
      speed = INITIAL_SPEED * 2;
    else
      speed = INITIAL_SPEED;
  }

  private void eatPickUp(Maze maze, Point node, Ghost[] ghosts) {
    int pickUp = maze.eatPickUp(node);
    int reward = 0;
    if (pickUp == 1) {
      dotScore++;
      reward = 10;
    } else if (pickUp == 2) {
      Sounds.pillEaten.play();
      reward = 100;
      dotScore++;
      for (int n = 0; n < ghosts.length; n++)
        if (ghosts[n].setBlue())
          reward += 100;
    } else if (pickUp == 3) {
      int type = (int)(3 * Math.random());
      reward = 1000;
      if (type == 0) {
        status = FAST;
        Sounds.pacFast.play();
        waitUntil = PICKUP_TIME;
      } else if (type == 1) {
        status = DIZZY;
        Sounds.pacDizzy.play();
        waitUntil = PICKUP_TIME;
      } else {
        Ghost.setSlow();
      }
    } else if (pickUp >= 4) {
      Sounds.pillEaten.play();
      if (pickUp - 4 == player) reward = 2000;
      else reward = 1000;
    }
    if (pickUp >= 2) {
      Risers.riser(position, "" + reward, colour);
    }
    score += reward;
    updateScore();
  }

  private void drawAngel(Surface s, int x, int y) {
    if (y >= Layout.MIN_Y) {
      x += (int)(ANGEL_A * Math.sin(1.0 * waitUntil / ANGLE_W));
      s.draw(Images.pacAngel.texture(), x - SIZE/2, y - SIZE/2);
    }
  }

  private String stringOf(int score) {
    String s = "" + score;
    return "            ".substring(s.length()) + score;
  }
}







