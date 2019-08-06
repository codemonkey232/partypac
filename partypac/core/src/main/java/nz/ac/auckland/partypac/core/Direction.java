package nz.ac.auckland.partypac.core;

public class Direction {

  public static final int STOPPED = 0;
  public static final int UP = 1;
  public static final int RIGHT = 2;
  public static final int DOWN = 3;
  public static final int LEFT = 4;

  public static final Direction[] D = {
    new Direction("Stopped", 16, 0, 0, 0),
    new Direction("Up",   1, 3, 0, -1),
    new Direction("Right",   2, 4, 1, 0),
    new Direction("Down",   4, 1, 0, 1),
    new Direction("Left",   8, 2, -1, 0)};

  public final String name;
  public final int bit;   //1,2,4,8 - storage bit for reading hex files
  public final int opposite;  //0,1,2,3 - opposite direction
  public final int x;    //-1 is left, 1 is right
  public final int y;    //-1 is up, 1 is down

  public Direction (String name, int bit, int opposite, int x, int y) {
    this.name = name;
    this.bit = bit;
    this.opposite = opposite;
    this.x = x;
    this.y = y;
  }
}
