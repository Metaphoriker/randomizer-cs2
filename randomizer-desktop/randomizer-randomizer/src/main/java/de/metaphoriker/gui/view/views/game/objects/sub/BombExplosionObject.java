package de.metaphoriker.gui.view.views.game.objects.sub;

import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.gui.view.views.game.Position;
import de.metaphoriker.gui.view.views.game.objects.sup.AbstractLivingGameObject;
import de.metaphoriker.gui.view.views.game.objects.sup.AbstractStaticObject;

public class BombExplosionObject extends AbstractStaticObject {

  private static final int STAY_TICKS = 100;

  private int ticksAlive = 0;

  public BombExplosionObject(Position position) {
    super(position, 150, 150);

    setFill(ImageUtil.getRawImagePattern("images/game/bomb_boom_icon.png"));
  }

  @Override
  public void update() {

    ticksAlive++;
    getPosition().getGameField().getEntities().stream()
        .filter(AbstractLivingGameObject.class::isInstance)
        .map(AbstractLivingGameObject.class::cast)
        .filter(entity -> entity.getBoundsInParent().intersects(getBoundsInParent()))
        .forEach(entity -> entity.damage(24));

    if (ticksAlive >= STAY_TICKS) damage(1);
  }
}
