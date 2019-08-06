package nz.ac.auckland.partypac.core;

import playn.core.Color;
import playn.core.Surface;
import playn.core.Canvas;
import playn.core.Texture;
import pythagoras.i.Point;

public class Background {

  public static final int NUM_RINGS = 5;
  public static boolean BACKGROUND_FX = true;

  // ********** Static Variables **********
  private static int darkColour, lightColour, ringColour;
  private static int status, t;
  private static boolean glow, ring;
  private static Point[] rings = new Point[NUM_RINGS];
  private static int[] spawnTime = new int[NUM_RINGS];

  private static int backColour;
  private static Canvas canvas;
  private static Texture texture;

  public static void setColour(boolean glowy, boolean ringy, int dColour, int lColour, int rColour) {
    glow = glowy;
    ring = ringy;
    darkColour = dColour;
    lightColour = lColour;
    ringColour = rColour;
  }

  public static void tick() {
    t++;

    if (glow && BACKGROUND_FX) {
      int redC = (r(lightColour) + r(darkColour)) / 2;
      int greenC = (g(lightColour) + g(darkColour)) / 2;
      int blueC = (b(lightColour) + b(darkColour)) / 2;
      int redA = (r(lightColour) - r(darkColour)) / 2;
      int greenA = (g(lightColour) - g(darkColour)) / 2;
      int blueA = (b(lightColour) - b(darkColour)) / 2;

      double brightness = Math.sin(t / 10.0);
      redA = (int)(redA * brightness + redC);
      greenA = (int)(greenA * brightness + greenC);
      blueA = (int)(blueA * brightness + blueC);
      backColour = Color.rgb(redA, greenA, blueA);
    } else {
      backColour = darkColour;
    }

    if (ring && BACKGROUND_FX) {
      if (canvas == null) {
        canvas = PartyPac.plat.graphics().createCanvas(Layout.MAZE_WIDTH_PX, Layout.MAZE_WIDTH_PX);
      }
      for (int n = 0; n < NUM_RINGS; n++) {
        if (spawnTime[n] != 0) {
          int radius = (t - spawnTime[n]) * 5;
          if (radius < (Layout.MAZE_WIDTH_PX + Layout.MAZE_HEIGHT_PX)) {
            canvas.setFillColor(ringColour);
            canvas.fillCircle(rings[n].x, rings[n].y, radius + 10);
            canvas.setFillColor(backColour);
            canvas.fillCircle(rings[n].x, rings[n].y, radius);
          }
        }
      }
      texture = canvas.image.texture();
      texture.update(canvas.image);
    }
  }

  public static void ring(Point p) {
    for (int n = 1; n < NUM_RINGS; n++) {
      rings[n - 1] = rings[n];
      spawnTime[n - 1] = spawnTime[n];
    }
    rings[NUM_RINGS - 1] = new Point(p.x, p.y);
    spawnTime[NUM_RINGS - 1] = t;
  }

  public static void draw(Surface s) {
    s.setFillColor(backColour);
    s.fillRect(0, 0, Layout.MAZE_WIDTH_PX, Layout.MAZE_HEIGHT_PX);
    if (ring && BACKGROUND_FX && texture != null) {
      s.draw(texture, 0, 0);
    }
  }

  static int r(int rgb) {
    return (rgb >> 16) & 0xff;
  }

  static int g(int rgb) {
    return (rgb >> 8) & 0xff;
  }

  static int b(int rgb) {
    return (rgb) & 0xff;
  }

}

