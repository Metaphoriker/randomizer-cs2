package de.metaphoriker.gui.view.views.game.objects.sup.entity;

import de.metaphoriker.gui.view.views.game.Position;
import javafx.geometry.Rectangle2D;

public interface Entity {

  void update();

  Facing getFacing();

  Rectangle2D getHitBox();

  Position getPosition();
}
