package nz.ac.auckland.partypac.core;

import playn.core.Sound;
import playn.core.Platform;
import java.util.HashMap;
import java.util.Map;

public class Sounds {

  public static final Map<String, Sound> SOUNDS = new HashMap<String, Sound>();

  public static Sound levelStart, dotsEaten, lifeLost, pickupSpawn, levelWon, gameWon;
  public static Sound pacSpawn, pacDeath, pacDizzy, pacFast, pillEaten;
  public static Sound ghostSpawn, ghostEaten, ghostSlow;

  static boolean isLoaded = false;

  static void load(Platform plat) {
    levelStart = getSound(plat, "levelstart");
    dotsEaten = getSound(plat, "dotseaten");
    lifeLost = getSound(plat, "lifelost");
    pickupSpawn = getSound(plat, "pickupspawn");
    levelWon = getSound(plat, "levelwon");
    gameWon = getSound(plat, "gamewon");

    pacSpawn = getSound(plat, "pacspawn");
    pacDeath = getSound(plat, "pacdeath");
    pacDizzy = getSound(plat, "pacdizzy");
    pacFast = getSound(plat, "pacfast");
    pillEaten = getSound(plat, "pilleaten");

    ghostSpawn = getSound(plat, "ghostspawn");
    ghostEaten = getSound(plat, "ghosteaten");
    ghostSlow = getSound(plat, "ghostslow");
  }

  static Sound getSound(Platform plat, String name) {
    Sound sound = plat.assets().getSound("sounds/" + name);
    SOUNDS.put(name, sound);
    return sound;
  }

  static boolean isLoaded() {
    if (isLoaded) {
      return true;
    }
    for (Sound sound : SOUNDS.values()) {
      if (!sound.isLoaded()) {
        return false;
      }
    }
    isLoaded = true;
    return true;
  }
}
