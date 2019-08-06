package nz.ac.auckland.partypac.core;

import pythagoras.i.Point;
import playn.core.Texture;
import playn.core.Image;
import playn.core.Tile;
import playn.core.Surface;

public class Maze {

  public static int WIDTH = Layout.MAZE_WIDTH_TILES;
  public static int HEIGHT = Layout.MAZE_HEIGHT_TILES;
  public static int SIZE = Layout.TILE_SIZE_PX;

  public static boolean PATHS_OFFSET = true, DOTS_AS_IMAGES = true;

  private int[][] paths = new int[WIDTH][HEIGHT];
  private int[][] pickUps = new int[WIDTH][HEIGHT];
  private int[][] ghostHome = new int[WIDTH][HEIGHT];
  private int[][][] mazeTiles;
  private int[][][] transforms;
  private Texture mazeImage;
  private String tileFileName;
  private int depth, dotsLeft;

  private static Tile[] dotTiles;
  private static Tile[] fruitTiles;

  public Maze(String mazeFileName) {
    if (dotTiles == null || fruitTiles == null) {
      dotTiles = TileSet.divideImageTiles(Images.dots, SIZE);
      dotTiles[0].texture().reference();

      fruitTiles = TileSet.divideImageTiles(Images.fruit, SIZE);
      fruitTiles[0].texture().reference();
    }

    load(mazeFileName);
    updateImage();
    reDot();
  }

  public int getDepth() {
    return depth;
  }

  public void draw(Surface s) {
    s.draw(mazeImage, 0, 0);
  }

  public void drawPickUps(Surface s) {
    s.setFillColor(0xffffff00);
    for (int x = 0; x < Maze.WIDTH; x++) {
      for (int y = 0; y < Maze.HEIGHT; y++) {
        Point pos = getPosition(new Point(x, y));
        int pickUp = pickUps[x][y];
        if (pickUp == 1) {
          if (DOTS_AS_IMAGES)
            s.draw(dotTiles[pickUp - 1], pos.x - SIZE/2, pos.y - SIZE/2);
          else
            s.fillRect(pos.x - 4, pos.y - 4, 8, 8);
        } else if (pickUp == 2) {
          s.draw(dotTiles[1], pos.x - SIZE/2, pos.y - SIZE/2);
        } else if (pickUp == 3) {
          s.draw(dotTiles[2], pos.x - SIZE/2, pos.y - SIZE/2);
        } else if (pickUp >= 4) {
          s.draw(fruitTiles[pickUp - 4], pos.x - SIZE/2, pos.y - SIZE/2);
        }
      }
    }
  }

  public void updateImage() {
    mazeImage = TileSet.drawMazeImage(mazeTiles, transforms, TileSet.loadImageTiles(tileFileName), 0, mazeTiles[0][0].length - 1);
    mazeImage.reference();
  }

  public static Point getNode(Point position) {
    if (PATHS_OFFSET)
      position = new Point(position.x - SIZE/2, position.y - SIZE/2);
    if (position.x % SIZE != SIZE/2 || position.y % SIZE != SIZE/2)
      return null;
    Point node = new Point(position.x / SIZE, position.y / SIZE);
    if (node.x < 0 || node.y < 0 || node.x >= WIDTH || node.y >= HEIGHT)
      return null;
    return node;
  }

  public static int getNodeDist(Point position) {
    if (!PATHS_OFFSET)
      position = new Point(position.x - SIZE/2, position.y - SIZE/2);
    return Math.max(position.x % SIZE, position.y % SIZE);
  }

  public static Point getPosition(Point node) {
    int addend;
    if (PATHS_OFFSET) addend = 2; else addend = 1;
    return new Point((2*node.x + addend) * SIZE/2, (2*node.y + addend) * SIZE/2);
  }

  public int getPath(Point node) {
    return paths[node.x][node.y];
  }

  public int getGhostHome(Point node) {
    return ghostHome[node.x][node.y];
  }

  public int getDotsLeft() {
    return dotsLeft;
  }

  public int eatPickUp(Point node) {
    int pickUp = pickUps[node.x][node.y];
    if (pickUp <= 0)
      return 0;
    if (pickUp <= 2) {
      dotsLeft--;
      pickUps[node.x][node.y] = -pickUp;
    } else
      pickUps[node.x][node.y] = 0;
    return pickUp;
  }

  public void reDot() {
    dotsLeft = 0;
    for (int x = 0; x < WIDTH; x++)
      for (int y = 0; y < HEIGHT; y++) {
        pickUps[x][y] = Math.abs(pickUps[x][y]);
        if (pickUps[x][y] == 1 || pickUps[x][y] == 2)
          dotsLeft++;
      }
  }

  public static boolean bitIsOn(int integer, int bit) {
    return (integer & bit) != 0;
  }

  public void setPickup(Point p, int pickup) {
    pickUps[p.x][p.y] = pickup;
  }

  public void load(String mazeFileName) {
    DataSet d = new DataSet(mazeFileName);

    tileFileName = d.readString();
    depth = d.readInt();

    paths = d.readArray(WIDTH, HEIGHT);
    pickUps = d.readArray(WIDTH, HEIGHT);
    ghostHome = d.readArray(WIDTH, HEIGHT);
    mazeTiles = d.readArray(WIDTH, HEIGHT, depth);
    transforms = d.readArray(WIDTH, HEIGHT, depth);
  }
}


