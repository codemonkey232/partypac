package nz.ac.auckland.partypac.core;

import playn.core.Platform;
import playn.core.Sound;
import react.RFuture;

import java.util.HashMap;
import java.util.Map;

public class Levels {

  public static final Map<String, RFuture<String>> TEXTS = new HashMap<String, RFuture<String>>();
  public static final Map<String, Sound> MUSICS = new HashMap<String, Sound>();

  public static final int NUM_LEVELS = 7, DEMO_LEVELS = 7;

  public static final String[] LEVEL_FILE_NAMES = new String[] {
    "green1.data",
    "wharf1.data",
    "wharf2.data",
    "stone1.data",
    "stone2.data",
    "matrix1.data",
    "matrix2.data"
  };

  public static final String[] MAZE_FILE_NAMES = new String[] {
    "green1.maze",
    "wharf1.maze",
    "wharf2.maze",
    "stone1.maze",
    "stone2.maze",
    "matrix1.maze",
    "matrix2.maze"
  };

  public static final String[] MUSIC_FILE_NAMES = new String[] {
    "KoD_Black_forest",
    "KoD_Demonwarriors",
    "KoD_Force_of_darkness",
    "KoD_Kingdom_of_ice",
    "KoD_Dragons_revenge",
    "KoD_Legion_of_fear",
    "KoD_Last_battle"
  };

  static boolean isLoaded = false;

  static void load(Platform plat) {
    for (String l : LEVEL_FILE_NAMES) {
      getText(plat, l);
    }
    for (String m : MAZE_FILE_NAMES) {
      getText(plat, m);
    }
    for (String m : MUSIC_FILE_NAMES) {
      getMusic(plat, m);
    }
  }

  static RFuture<String> getText(Platform plat, String name) {
    RFuture<String> text = plat.assets().getText("levels/" + name);
    TEXTS.put(name, text);
    return text;
  }

  static Sound getMusic(Platform plat, String name) {
    Sound music = plat.assets().getMusic("music/" + name);
    music.setLooping(true);
    MUSICS.put(name, music);
    return music;
  }

  static boolean isLoaded() {
    if (isLoaded) {
      return true;
    }
    for (RFuture<String> text : TEXTS.values()) {
      if (!text.isCompleteNow()) {
        return false;
      }
    }
    for (Sound music : MUSICS.values()) {
      if (!music.isLoaded()) {
        return false;
      }
    }
    isLoaded = true;
    return true;
  }
}

