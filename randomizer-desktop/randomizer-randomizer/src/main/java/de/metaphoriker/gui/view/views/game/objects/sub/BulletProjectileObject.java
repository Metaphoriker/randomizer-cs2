package de.metaphoriker.gui.view.views.game.objects.sub;

import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.gui.view.views.game.Position;
import de.metaphoriker.gui.view.views.game.objects.sup.ProjectileObject;
import javafx.geometry.Point2D;

public class BulletProjectileObject extends ProjectileObject {

  public BulletProjectileObject(Position position, Point2D velocity) {
    super(position, velocity, 25, 25);
    setFill(ImageUtil.getImagePattern("images/game/bullet_icon.png"));
  }

  @Override
  public void update() {
    super.update();

    getPosition().getGameField().getEntities().stream()
        .filter(EnemyObject.class::isInstance)
        .forEach(
            entity -> {
              EnemyObject livingEntity = (EnemyObject) entity;

              if (livingEntity.getBoundsInParent().intersects(getBoundsInParent())) {
                livingEntity.damage(5);
                damage(1);
              }
            });
  }
}
