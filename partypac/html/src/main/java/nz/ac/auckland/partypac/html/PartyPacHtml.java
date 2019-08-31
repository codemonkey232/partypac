package nz.ac.auckland.partypac.html;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import playn.html.HtmlGraphics;
import playn.html.HtmlCanvas;
import playn.html.HtmlImage;
import playn.core.Graphics;
import playn.core.Canvas;
import playn.core.Scale;

import nz.ac.auckland.partypac.core.PartyPac;
import nz.ac.auckland.partypac.core.Layout;
import nz.ac.auckland.partypac.core.TileSet;

public class PartyPacHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    config.experimentalFullscreen = true;
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("partypac/");
    plat.graphics().setSize(Layout.VIEW_WIDTH_PX, Layout.VIEW_HEIGHT_PX);
    TileSet.canvasCreator = new HtmlCanvasCreator(plat.graphics());
    new PartyPac(plat);
    plat.start();
  }
  
  static class HtmlCanvasCreator implements TileSet.CanvasCreator {
    private final Graphics graphics;

    HtmlCanvasCreator(Graphics graphics) {
      this.graphics = graphics;
    }

    public Canvas create(int pixelWidth, int pixelHeight) {
      CanvasElement elem = Document.get().createCanvasElement();
      elem.setWidth(pixelWidth);
      elem.setHeight(pixelHeight);
      return new HtmlCanvas(graphics, new HtmlImage(graphics, Scale.ONE, elem, "<canvas>"));
    }
  }
}
