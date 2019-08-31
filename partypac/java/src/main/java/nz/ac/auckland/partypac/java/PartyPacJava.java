package nz.ac.auckland.partypac.java;

import java.awt.image.BufferedImage;

import playn.java.LWJGLPlatform;
import playn.core.Graphics;
import playn.core.Canvas;
import playn.core.Scale;

import nz.ac.auckland.partypac.core.PartyPac;
import nz.ac.auckland.partypac.core.Layout;
import nz.ac.auckland.partypac.core.TileSet;

import java.lang.reflect.Field;


public class PartyPacJava {

  public static void main (String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    config.appName = "Party Pac - (C) aols010 2004";
    config.width = Layout.VIEW_WIDTH_PX;
    config.height = Layout.VIEW_HEIGHT_PX;
    LWJGLPlatform plat = new LWJGLPlatform(config);
    TileSet.canvasCreator = new JavaCanvasCreator(plat.graphics());
    new PartyPac(plat);
    plat.start();
  }
  
  static class JavaCanvasCreator implements TileSet.CanvasCreator {
    private final Graphics graphics; 
    
    JavaCanvasCreator(Graphics graphics) {
      this.graphics = graphics;
    }

    public Canvas create(int pixelWidth, int pixelHeight) {
      try {
        Field f = Graphics.class.getDeclaredField("scale");
        f.setAccessible(true);
        Scale temp = (Scale) f.get(graphics);
        f.set(graphics, Scale.ONE);
        Canvas canvas = graphics.createCanvas(pixelWidth, pixelHeight);
        f.set(graphics, temp);
        return canvas;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
