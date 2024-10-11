package de.metaphoriker.view.game.objects.sup.entity;

import de.metaphoriker.view.game.Position;
import javafx.geometry.Rectangle2D;

public interface Entity {

  void update();

  Facing getFacing();

  Rectangle2D getHitBox();

  Position getPosition();
}
