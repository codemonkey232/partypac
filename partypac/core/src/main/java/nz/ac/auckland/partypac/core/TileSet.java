package nz.ac.auckland.partypac.core;

import playn.core.Canvas;
import playn.core.Graphics;
import playn.core.GL20;
import playn.core.Image;
import playn.core.Texture;
import playn.core.Tile;

public class TileSet {

  static int WHOLE = Layout.TILE_SIZE_PX;
  static int HALF = Layout.TILE_SIZE_PX / 2;

  // **************************************
  // ********** Specific Methods **********
  // **************************************


  // ********** Maze Methods **********
  public static Image.Region[] loadImageTiles(String fileName) {
    return divideImage(Images.IMAGES.get(fileName), Layout.TILE_SIZE_PX);
  }

  public static Texture drawMazeImage (int[][][] mazeTiles, int[][][] transforms, Image.Region[] imageTiles, int minZ, int maxZ) {
    Canvas canvas = createCanvas(Layout.MAZE_WIDTH_PX, Layout.MAZE_HEIGHT_PX);

    for (int z = minZ; z <= maxZ; z++) {
      for (int x = 0; x < Maze.WIDTH; x++) {
        for (int y = 0; y < Maze.HEIGHT; y++) {
          int tileID = mazeTiles[x][y][z];
          int transform = transforms[x][y][z];
          if (tileID != 0) {
            canvas.save();
            canvas.translate(x * WHOLE, y * WHOLE);
            applyTransform(canvas, transform);
            pixelate();
            canvas.draw(imageTiles[tileID - 1], 0, 0);
            pixelate();
            canvas.restore();
          }
        }
      }
    }
    System.out.println(canvas.image.pixelWidth() + " " + canvas.image.pixelHeight() + " " + Layout.MAZE_WIDTH_PX + " " + Layout.MAZE_HEIGHT_PX);
    return canvas.toTexture();
  }
  
  public static void pixelate() {
    GL20 gl20 = PartyPac.plat.graphics().gl;
    gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
    gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
  }

  public static Image.Region[] divideImage(Image image, int size) {
    int columns = image.pixelWidth() / size;
    int rows = image.pixelHeight() / size;

    Image.Region[] tiles = new Image.Region[rows * columns];
    for (int y = 0; y < rows; y++)        //y is outer loop, so it loads acrosswards - the same way a page is read
      for (int x = 0; x < columns; x++)
        tiles[y * columns + x] = image.region(x * size, y * size, size, size);
    return tiles;
  }

  public static Tile[] divideImageTiles(Image image, int size) {
    Image.Region[] regions = divideImage(image, size);
    Tile[] tiles = new Tile[regions.length];
    for (int i = 0; i < regions.length; i++) {
      tiles[i] = regions[i].tile();
    }
    tiles[0].texture().reference();
    return tiles;
  }

  private static void applyTransform(Canvas canvas, int transform) {
    if (transform == 0) return;

    canvas.translate(HALF, HALF);
    if (transform >= 4) {
      transform -= 4;
      canvas.transform(-1, 0, 0, 1, 0, 0);
    }
    while (transform > 0) {
      transform--;
      canvas.transform(0, 1, -1, 0, 0, 0);
    }
    canvas.translate(-HALF, -HALF);
  }
  
  public interface CanvasCreator {
    public Canvas create(int pixelWidth, int pixelHeight);
  }
  
  public static CanvasCreator canvasCreator = new CanvasCreator() {
    public Canvas create(int pixelWidth, int pixelHeight) {
      return PartyPac.plat.graphics().createCanvas(pixelWidth, pixelHeight);
    }
  };
  
  public static Canvas createCanvas(int pixelWidth, int pixelHeight) {
    return canvasCreator.create(pixelWidth, pixelHeight);
  }
}
