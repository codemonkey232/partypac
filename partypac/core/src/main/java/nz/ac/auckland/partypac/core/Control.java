package nz.ac.auckland.partypac.core;

import playn.core.Key;

public class Control {

  public final Key key;
  public final int player;
  public final int direction;

  public Control (Key key, int player, int direction) {
    this.key = key;
    this.player = player;
    this.direction = direction;
  }

  public static Control[] CONTROLS = new Control[] {
    new Control(Key.UP, 0, Direction.UP),
    new Control(Key.RIGHT, 0, Direction.RIGHT),
    new Control(Key.DOWN, 0, Direction.DOWN),
    new Control(Key.LEFT, 0, Direction.LEFT),

    new Control(Key.W, 1, Direction.UP),
    new Control(Key.D, 1, Direction.RIGHT),
    new Control(Key.S, 1, Direction.DOWN),
    new Control(Key.A, 1, Direction.LEFT),

    new Control(Key.NP8, 2, Direction.UP),
    new Control(Key.NP6, 2, Direction.RIGHT),
    new Control(Key.NP5, 2, Direction.DOWN),
    new Control(Key.NP2, 2, Direction.DOWN),
    new Control(Key.NP4, 2, Direction.LEFT),

    new Control(Key.I, 3, Direction.UP),
    new Control(Key.L, 3, Direction.RIGHT),
    new Control(Key.K, 3, Direction.DOWN),
    new Control(Key.J, 3, Direction.LEFT),
  };
}

