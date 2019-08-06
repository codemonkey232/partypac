package nz.ac.auckland.partypac.core;

import playn.scene.Pointer;
import playn.core.*;
import playn.scene.*;
import pythagoras.f.Point;

public class Menu extends GroupLayer {

  public static final int SPLASH_SCREEN = 0, SELECT_GAME = 1, GAME_ON = 2, GAME_OVER = 3, GAME_WON = 4;

  ImageLayer partyPac = new ImageLayer(Images.partyPac);
  ImageLayer loading = new ImageLayer(Images.loading);
  ImageLayer keys = new ImageLayer(Images.keys);
  ImageLayer paused = new ImageLayer(Images.paused);
  ImageLayer congrats = new ImageLayer(Images.congrats);
  ImageLayer gameOver = new ImageLayer(Images.gameOver);
  ImageLayer[] selectButtons = new ImageLayer[] {
      new ImageLayer(Images.menu1Player),
      new ImageLayer(Images.menu2Player),
      new ImageLayer(Images.menu3Player),
      new ImageLayer(Images.menu4Player),
  };

  int status = SPLASH_SCREEN;

  Game game = new Game(this);

  public Menu() {
    add(partyPac);
    add(loading);
    add(keys);
    add(paused);
    add(congrats);
    add(gameOver);
    for (ImageLayer selectButton : selectButtons) {
      add(selectButton);
    }

    this.events().connect(new Pointer.Listener() {
      @Override public void onStart (Pointer.Interaction iact) {
        if (status == GAME_OVER || status == GAME_WON) {
          status = SPLASH_SCREEN;
        } else if (status == SPLASH_SCREEN && PartyPac.isLoaded()) {
          status = SELECT_GAME;
        } else if (status == SELECT_GAME) {
          for (int i = 0; i < selectButtons.length; i++) {
            ImageLayer selectButton = selectButtons[i];
            float x = iact.x() - selectButton.tx();
            float y = iact.y() - selectButton.ty();
            if (x > 0 && x < selectButton.width()
                && y > 0 && y < selectButton.height()) {
              status = GAME_ON;
              game.newGame(i + 1);
            }
          } 
        }
        statusUpdated();
      }
    });

    statusUpdated();

  }

  public void setStatus(int status) {
    this.status = status;
    statusUpdated();
  }

  public void statusUpdated() {
    loading.setVisible(!PartyPac.isLoaded());
    partyPac.setVisible(status == SPLASH_SCREEN);
    keys.setVisible(status == SPLASH_SCREEN || status == SELECT_GAME);
    paused.setVisible((status == GAME_ON) && (game.status() == Game.PAUSED));
    congrats.setVisible(status == GAME_WON);
    gameOver.setVisible(status == GAME_OVER);
    for (ImageLayer selectButton : selectButtons) {
      selectButton.setVisible(status == SELECT_GAME);
    }
  }

  public void tick() {
    Layout.centreImage(partyPac);
    Layout.centreImage(loading);
    loading.setTy(Layout.MAZE_HEIGHT_PX - 200);
    Layout.centreImage(paused);
    Layout.centreImage(keys);
    keys.setTy(Layout.MAZE_HEIGHT_PX - 50);
    Layout.centreImage(congrats);
    Layout.centreImage(gameOver);
    int i = 0;
    for (ImageLayer selectButton : selectButtons) {
      Layout.centreImage(selectButton);
      selectButton.setTy(Layout.MAZE_HEIGHT_PX * (i++) / 5 + 64);
    }
    game.tick();
  }

  protected void paintClipped (Surface surf) {
    if (PartyPac.isLoaded()) {
      game.draw(surf);
    }
    super.paintClipped(surf);
  }

  public float width() {
    return Layout.VIEW_WIDTH_PX;
  }

  public float height() {
    return Layout.VIEW_WIDTH_PX;
  }

}
