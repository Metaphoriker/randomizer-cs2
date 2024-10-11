package de.metaphoriker.view.game.objects.sub;

import de.metaphoriker.util.ImageUtil;
import de.metaphoriker.view.game.Position;
import de.metaphoriker.view.game.objects.sup.AbstractStaticObject;
import java.util.concurrent.ThreadLocalRandom;

public class FireObject extends AbstractStaticObject {

  private static final int speed = 1;

  public FireObject(Position position) {
    super(
        position,
        ThreadLocalRandom.current().nextInt(50, 100),
        ThreadLocalRandom.current().nextInt(20, 50));

    setTranslateX(position.getLocation().getX());
    setTranslateY(position.getLocation().getY());

    setFill(ImageUtil.getImagePattern("images/game/fire_icon.gif"));
  }

  @Override
  public void update() {
    super.update();

    setWidth(getWidth() - speed);
    setHeight(getHeight() - speed);

    getPosition().getGameField().getEntities().stream()
        .filter(EnemyObject.class::isInstance)
        .forEach(
            entity -> {
              EnemyObject livingEntity = (EnemyObject) entity;

              if (livingEntity.getBoundsInParent().intersects(getBoundsInParent()))
                livingEntity.damage(3);
            });

    if (getWidth() <= 0 || getHeight() <= 0) damage(1);
  }
}
