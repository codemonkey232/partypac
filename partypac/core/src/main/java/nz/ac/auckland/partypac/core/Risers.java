package nz.ac.auckland.partypac.core;

import pythagoras.i.Point;
import playn.core.TextFormat;
import playn.core.Texture;
import playn.core.Surface;
import playn.core.Font;
import playn.core.Canvas;
import java.util.Map;
import java.util.HashMap;

public class Risers {

  public static final int NUM_RISERS = 10, OFFSET_X = 15, OFFSET_Y = 30, LIFE_TIME = 50;
  public static final TextFormat TEXT_FORMAT = new TextFormat(new Font("Courier", Font.Style.BOLD, 16), true);

  private static Point[] spawnedAt = new Point[NUM_RISERS];
  private static String[] text = new String[NUM_RISERS];
  private static int[] colour = new int[NUM_RISERS];
  private static int[] spawnTime = new int[NUM_RISERS];
  private static int t = 0;

  private static Map<String, Texture> map = new HashMap<String, Texture>();

  public static void tick() {
    t++;
  }

  public static void riser(Point p, String texty, int c) {
    for (int n = 1; n < NUM_RISERS; n++) {
      spawnedAt[n - 1] = spawnedAt[n];
      text[n - 1] = text[n];
      colour[n - 1] = colour[n];
      spawnTime[n - 1] = spawnTime[n];
    }
    spawnedAt[NUM_RISERS - 1] = new Point(p.x, p.y);
    text[NUM_RISERS - 1] = texty;
    colour[NUM_RISERS - 1] = c;
    spawnTime[NUM_RISERS - 1] = t;
  }

  public static void draw(Surface s) {
    for (int n = 0; n < NUM_RISERS; n++) {
      int aliveTime = t - spawnTime[n];
      if (aliveTime <= LIFE_TIME && spawnTime[n] != 0) {
        s.draw(getTexture(text[n], colour[n]), spawnedAt[n].x - OFFSET_X, spawnedAt[n].y - aliveTime - OFFSET_Y);
      }
    }
  }

  public static Texture getTexture(String texty, int colour) {
    String key = texty + "|" + colour;
    Texture ret = map.get(key);
    if (ret == null) {
      ret = createTexture(texty, colour);
      map.put(key, ret);
    }
    return ret;
  }

  public static Texture createTexture(String texty, int colour) {
    Canvas canvas = PartyPac.plat.graphics().createCanvas(Layout.MAZE_WIDTH_PX / 4, Layout.HUD_HEIGHT_PX);
    canvas.clear();
    canvas.setFillColor(colour);
    canvas.fillText(PartyPac.plat.graphics().layoutText(texty, TEXT_FORMAT), 0, 5);
    Texture ret = canvas.toTexture();
    ret.reference();
    return ret;
  }

}
