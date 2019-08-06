package nz.ac.auckland.partypac.core;

import pythagoras.i.Point;
import playn.core.Texture;
import playn.core.Tile;
import playn.core.Surface;

public class Ghost {

  // ********** Constants **********
  public static final int
    ALIVE = 0, SLOW = 1, BLUE = 2, DEAD = 3, WAITING = 4,
    SPAWN_TIME = 50, BLUE_TIME = 350, SLOW_TIME = 500,
    FLASH_TIME = 50, FLASH_PERIOD = 5,
    SPAWN_PREPLAY = 5;


  public static final int SIZE = 42;
  public static final int INITIAL_SPEED = 4;
  public static int SLOW_UNTIL = 0;

  // ********** Instance Variables **********
  private Point position;
  private int status, direction, speed, waitUntil;
  private Texture normGhost, blueGhost;
  private Tile[] eyeTiles;

  // ********** Constructor **********
  public Ghost(String normFileName, String blueFileName, Point spawnPoint) {
    this.normGhost = Images.IMAGES.get(normFileName).texture();
    this.blueGhost = Images.IMAGES.get(blueFileName).texture();
    this.normGhost.reference();
    this.blueGhost.reference();
    position = Maze.getPosition(spawnPoint);
    status = ALIVE;
    speed = INITIAL_SPEED;
    direction = Direction.STOPPED;

    if (eyeTiles == null) {
      eyeTiles = TileSet.divideImageTiles(Images.ghostEyes, SIZE);
      eyeTiles[0].texture().reference();
    }
  }

  public boolean setBlue() {
    if (status != DEAD && status != WAITING)
      waitUntil = BLUE_TIME;
    if (status != ALIVE && status != SLOW)
      return false;
    status = BLUE;
    return true;
  }

  public void setPosition(Point position) {
    this.position = position;
  }

  public Point getPosition() {
    return position;
  }

  public int getDirection() {
    return direction;
  }

  public void draw(Surface s) {
    if (status == ALIVE || status == SLOW) {
      s.draw(normGhost, position.x - SIZE/2, position.y - SIZE/2);
    }
    if (status == BLUE) {
      if (waitUntil < FLASH_TIME && (waitUntil / FLASH_PERIOD) % 2 == 0) {
        s.draw(normGhost, position.x - SIZE/2, position.y - SIZE/2);
      } else {
        s.draw(blueGhost, position.x - SIZE/2, position.y - SIZE/2);
      }
    }

    if (status == BLUE || status == WAITING) {
      s.draw(eyeTiles[0], position.x - SIZE/2, position.y - SIZE/2);
    } else {
      s.draw(eyeTiles[direction], position.x - SIZE/2, position.y - SIZE/2);
    }
  }

  public void tick(Maze maze, int numPlayers, PacMan[] pacmen) {
    waitUntil--;
    if (waitUntil == SPAWN_PREPLAY && status == WAITING)
      Sounds.ghostSpawn.play();
    if (waitUntil == 0) {
      if (status == WAITING)
        Background.ring(position);
      if (status != DEAD)
        status = ALIVE;
    }
    if (status == ALIVE && SLOW_UNTIL > 0)
      status = SLOW;
    else if (status == SLOW && SLOW_UNTIL <= 0)
      status = ALIVE;
    pickDirection(maze);
    position.x = DataSet.loopChange(position.x, Direction.D[direction].x * speed, Layout.MIN_X, Layout.MAX_X);
    position.y = DataSet.loopChange(position.y, Direction.D[direction].y * speed, Layout.MIN_Y, Layout.MAX_Y);
    for (int n = 0; n < numPlayers; n++) {
      collide(pacmen[n]);
    }
  }

  public static void setSlow() {
    Sounds.ghostSlow.play();
    SLOW_UNTIL = SLOW_TIME;
  }

  // ********** Private methods **********
  private void pickDirection(Maze maze) {
    Point node = Maze.getNode(position);
    if (node != null) {
      setSpeed();
      if (status == DEAD) {
        pickRandomDirection(maze.getGhostHome(node));
        if (direction == 0) {
          status = WAITING;
          waitUntil = SPAWN_TIME;
        }
      } else if (status != WAITING)
        pickRandomDirection(maze.getPath(node));
    }
  }

  // Notthreadsafe.
  static int[] possibleDirections = new int[3];  

  private void pickRandomDirection(int path) {
    int doubleBack = Direction.D[direction].opposite, numberPossible = 0;
    for (int dir = 1; dir <= 4; dir++)
      if (dir != doubleBack && Maze.bitIsOn(path, Direction.D[dir].bit))  {
        possibleDirections[numberPossible] = dir;      //On a path, not back the way you came
        numberPossible++;
      }
    if (numberPossible == 0)
      if (status != DEAD || Maze.bitIsOn(path, Direction.D[doubleBack].bit))
        direction = doubleBack;    //No new directions - go back the way you came
      else {
        direction = Direction.STOPPED;  //Ghost is stuck. Respawns
        status = WAITING;
        waitUntil = SPAWN_TIME;
      }
    else if (numberPossible == 1)
      direction = possibleDirections[0];    //Go the only new direction
    else
      direction = possibleDirections[(int)(numberPossible * Math.random())];  //Pick a random new direction
  }

  private void setSpeed() {
    if (status == ALIVE || status == DEAD)
      speed = INITIAL_SPEED;
    else
      speed = INITIAL_SPEED / 2;
  }

  private void collide(PacMan pacman) {
    if (pacman.getStatus() <= 2)
      if (status == ALIVE || status == SLOW || status == BLUE) {
        int dx = pacman.getPosition().x - position.x;
        int dy = pacman.getPosition().y - position.y;
        int sumR = (PacMan.SIZE + Ghost.SIZE) / 2;
        if (dx*dx + dy*dy < sumR * sumR)
          if (status == BLUE) {
            status = DEAD;
            Background.ring(position);
            pacman.eatGhost();
          } else if (pacman.getStatus() != PacMan.INVINCIBLE) {
            pacman.setStatus(PacMan.DEAD);
            Background.ring(pacman.getPosition());
          }
      }
  }

  public float width() {
    return SIZE;
  }

  public float height() {
    return SIZE;
  }
}
