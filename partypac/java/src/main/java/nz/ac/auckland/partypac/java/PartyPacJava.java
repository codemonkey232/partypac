package nz.ac.auckland.partypac.java;

import playn.java.LWJGLPlatform;

import nz.ac.auckland.partypac.core.PartyPac;
import nz.ac.auckland.partypac.core.Layout;

public class PartyPacJava {

  public static void main (String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    config.appName = "Party Pac - (C) aols010 2004";
    config.width = Layout.VIEW_WIDTH_PX;
    config.height = Layout.VIEW_HEIGHT_PX;
    LWJGLPlatform plat = new LWJGLPlatform(config);
    new PartyPac(plat);
    plat.start();
  }
}
