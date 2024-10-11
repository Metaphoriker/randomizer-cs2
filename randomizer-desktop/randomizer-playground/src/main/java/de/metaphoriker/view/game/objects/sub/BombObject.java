package de.metaphoriker.view.game.objects.sub;

import de.metaphoriker.util.ImageUtil;
import de.metaphoriker.view.game.Position;
import de.metaphoriker.view.game.objects.sup.AbstractStaticObject;

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
