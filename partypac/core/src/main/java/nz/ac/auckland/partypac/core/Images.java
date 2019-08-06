package nz.ac.auckland.partypac.core;

import playn.core.Image;
import playn.core.Texture;
import playn.core.Platform;
import java.util.HashMap;
import java.util.Map;

public class Images {

  public static final Map<String, Image> IMAGES = new HashMap<String, Image>();

  public static Image ppIcon, loading, partyPac, keys, paused, congrats, gameOver;
  public static Image menu1Player, menu2Player, menu3Player, menu4Player;
  public static Image tileGreen, tileWharf, tileStone, tileMatrix;
  public static Image pacYellow, pacOrange, pacGreen, pacRed, pacAngel;
  public static Image gRed, gPink, gWhite, gCyan, gJade, gPurple, gBrown;
  public static Image ghostBlue, ghostMatrix, ghostMBlue, ghostEyes;
  public static Image life, lifeSaver, dots, fruit;

  static boolean isLoaded = false;

  static void loadImages(Platform plat) {
    ppIcon = getImage(plat, "ppicon.gif");
    loading = getImage(plat, "loading.gif");
    partyPac = getImage(plat, "partypac.gif");
    keys = getImage(plat, "keys.gif");
    paused = getImage(plat, "paused.gif");
    congrats = getImage(plat, "congratulations.gif");
    gameOver = getImage(plat, "gameover.gif");

    menu1Player = getImage(plat, "1player.gif");
    menu2Player = getImage(plat, "2player.gif");
    menu3Player = getImage(plat, "3player.gif");
    menu4Player = getImage(plat, "4player.gif");

    tileGreen = getImage(plat, "tilegreen.gif");
    tileWharf = getImage(plat, "tilewharf.gif");
    tileStone = getImage(plat, "tilestone.gif");
    tileMatrix = getImage(plat, "tilematrix.gif");

    pacYellow = getImage(plat, "pacyellow.gif");
    pacOrange = getImage(plat, "pacorange.gif");
    pacGreen = getImage(plat, "pacgreen.gif");
    pacRed = getImage(plat, "pacred.gif");
    pacAngel = getImage(plat, "pacangel.gif");

    gRed = getImage(plat, "ghostred.gif");
    gPink = getImage(plat, "ghostpink.gif");
    gWhite = getImage(plat, "ghostwhite.gif");
    gCyan = getImage(plat, "ghostcyan.gif");
    gJade = getImage(plat, "ghostjade.gif");
    gPurple = getImage(plat, "ghostpurple.gif");
    gBrown = getImage(plat, "ghostbrown.gif");

    ghostBlue = getImage(plat, "ghostblue.gif");
    ghostMatrix = getImage(plat, "ghostmatrix.gif");
    ghostMBlue = getImage(plat, "ghostmblue.gif");
    ghostEyes = getImage(plat, "ghosteyes.gif");

    life = getImage(plat, "life.gif");
    lifeSaver = getImage(plat, "lifesaver.gif");
    dots = getImage(plat, "dots.gif");
    fruit = getImage(plat, "fruit.gif");
  }

  static Image getImage(Platform plat, String name) {
    Image image = plat.assets().getImage("images/" + name);
    IMAGES.put(name, image);
    return image;
  }

  static boolean isLoaded() {
    if (isLoaded) {
      return true;
    }
    for (Image image : IMAGES.values()) {
      if (!image.isLoaded()) {
        return false;
      }
    }
    for (Image image : IMAGES.values()) {
      image.texture().reference();
    }
    isLoaded = true;
    return true;
  }
}
