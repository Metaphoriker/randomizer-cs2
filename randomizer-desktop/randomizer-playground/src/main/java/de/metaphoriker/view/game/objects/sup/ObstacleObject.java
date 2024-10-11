package de.metaphoriker.view.game.objects.sup;

import de.metaphoriker.view.game.Position;

/** More or less for identification purposes */
public abstract class ObstacleObject extends AbstractStaticObject {

  protected ObstacleObject(Position position, int width, int height) {
    super(position, width, height);
  }
}
