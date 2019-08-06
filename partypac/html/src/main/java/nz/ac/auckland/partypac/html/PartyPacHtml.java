package nz.ac.auckland.partypac.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import nz.ac.auckland.partypac.core.PartyPac;

public class PartyPacHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    config.experimentalFullscreen = true;
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("partypac/");
    new PartyPac(plat);
    plat.start();
  }
}
