package nz.ac.auckland.partypac.core;

import playn.core.*;
import playn.scene.ImageLayer;
import playn.scene.SceneGame;
import playn.scene.Mouse;
import playn.scene.Pointer;
import react.Slot;

public class PartyPac extends SceneGame {

  Menu menu;
  static Platform plat;

  public PartyPac (Platform plat) {
    super(plat, 25); // 25ms per frame
    PartyPac.plat = plat;

    Images.loadImages(plat);
    Levels.load(plat);
    Sounds.load(plat);

    // create and add background image layer
    menu = new Menu();
    rootLayer.add(menu);

    update.connect(new Slot<Clock>() {
      public void onEmit (Clock clock) { tick(); }
    });

    Pointer pointer = new Pointer(plat, rootLayer, false);
    plat.input().mouseEvents.connect(new Mouse.Dispatcher(rootLayer, false));


    plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
      public void onEmit (Keyboard.KeyEvent ev) {
        if (!ev.down) {
          return;
        }
        menu.game.type(ev.key);
      }
    });
  }

  public void tick() {
    menu.tick();
  }

  static boolean isLoaded = false;

  public static boolean isLoaded() {
    if (isLoaded) {
      return true;
    }
    isLoaded = Images.isLoaded() && Levels.isLoaded() && Sounds.isLoaded();
    return isLoaded;
  }

}
