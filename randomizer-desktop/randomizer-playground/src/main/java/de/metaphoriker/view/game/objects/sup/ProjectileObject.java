package de.metaphoriker.view.game.objects.sup;

import de.metaphoriker.view.game.Position;
import de.metaphoriker.view.game.objects.sup.entity.Projectile;
import javafx.geometry.Point2D;

public class ProjectileObject extends GameObject implements Projectile { // TODO: Make this abstract

  private final Point2D velocity;

  private int health = 1;

  public ProjectileObject(Position position, Point2D velocity, double width, double height) {
    super(position, width, height);

    this.velocity = velocity;
  }

  @Override
  public void update() {

    setTranslateX(getTranslateX() + velocity.getX());
    setTranslateY(getTranslateY() + velocity.getY());

    getPosition().getGameField().getEntities().stream()
        .filter(ObstacleObject.class::isInstance)
        .map(ObstacleObject.class::cast)
        .filter(entity -> entity.getBoundsInParent().intersects(getBoundsInParent()))
        .forEach(entity -> damage(1));
  }

  @Override
  public Point2D getVelocity() {
    return velocity;
  }

  @Override
  public void setHealth(int health) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void damage(int amount) {
    health -= amount;
  }

  @Override
  public int getHealth() {
    return health;
  }

  @Override
  public int getMaxHealth() {
    return -1;
  }
}
