package nz.ac.auckland.partypac.core;

import playn.core.Image;
import playn.core.Platform;
import playn.scene.ImageLayer;

public class Layout {

  public static final int TILE_SIZE_PX = 32;
  public static final int MAZE_WIDTH_TILES = 20;
  public static final int MAZE_HEIGHT_TILES = 16;
  public static final int HUD_HEIGHT_PX = 50;

  public static final int MAZE_WIDTH_PX = MAZE_WIDTH_TILES * TILE_SIZE_PX;
  public static final int MAZE_HEIGHT_PX = MAZE_HEIGHT_TILES * TILE_SIZE_PX;

  public static final int VIEW_WIDTH_PX = MAZE_WIDTH_PX;
  public static final int VIEW_HEIGHT_PX = MAZE_HEIGHT_PX + HUD_HEIGHT_PX;

  // Game bounds:
  public static final int
    MIN_X = -TILE_SIZE_PX / 2,
    MAX_X = MAZE_WIDTH_PX + Layout.TILE_SIZE_PX / 2,
    MIN_Y = -TILE_SIZE_PX / 2,
    MAX_Y = MAZE_HEIGHT_PX + Layout.TILE_SIZE_PX / 2;

  public static void centreImage(ImageLayer imageLayer) {
    imageLayer.setTranslation(MAZE_WIDTH_PX / 2 - imageLayer.width() / 2,
                              MAZE_HEIGHT_PX / 2 - imageLayer.height() / 2);
  }
}

