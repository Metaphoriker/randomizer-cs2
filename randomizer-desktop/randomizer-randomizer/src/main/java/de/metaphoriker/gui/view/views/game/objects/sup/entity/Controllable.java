package de.metaphoriker.gui.view.views.game.objects.sup.entity;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface Controllable {

  void onPressKey(KeyEvent keyEvent);

  void onReleaseKey(KeyEvent keyEvent);

  void onMouseMove(MouseEvent event);

  void onMouseClick(MouseEvent event);
}
