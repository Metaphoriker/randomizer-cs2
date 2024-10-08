package de.metaphoriker.gui.view.views.game.objects.sub;

import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.gui.view.views.game.Position;
import de.metaphoriker.gui.view.views.game.objects.sup.AbstractStaticObject;

public class BombObject extends AbstractStaticObject {

  private static final int TICKS = 100;

  private int ticksAlive = 0;

  public BombObject(Position position) {
    super(position, 50, 50);

    setFill(ImageUtil.getRawImagePattern("images/game/bomb_icon.png"));
  }

  @Override
  public void update() {

    ticksAlive++;

    if (ticksAlive >= TICKS) {
      damage(1);
      getPosition().getGameField().getEntities().add(new BombExplosionObject(getPosition()));
    }
  }
}
