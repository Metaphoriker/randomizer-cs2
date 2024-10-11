package de.metaphoriker.view.game.objects.sub;

import de.metaphoriker.util.ImageUtil;
import de.metaphoriker.view.game.Position;
import de.metaphoriker.view.game.objects.sup.AbstractLivingGameObject;
import de.metaphoriker.view.game.objects.sup.ObstacleObject;
import de.metaphoriker.view.game.objects.sup.entity.Aggressive;
import de.metaphoriker.view.game.objects.sup.entity.Facing;
import de.metaphoriker.view.game.objects.sup.entity.LivingEntity;
import de.metaphoriker.view.game.objects.sup.entity.Moveable;
import de.metaphoriker.view.game.objects.sup.entity.Vector;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class EnemyObject extends AbstractLivingGameObject
    implements LivingEntity, Moveable, Aggressive {

  private static final int DEFAULT_ENEMY_HEALTH = 10;

  private LivingEntity target;
  private int range = 10;
  private int damage = 2;

  public EnemyObject(Position position) {
    super(position, 30, 30);

    healthProperty.set(DEFAULT_ENEMY_HEALTH);
    setFill(
        ImageUtil.getImagePattern("images/game/enemy_icon.png", ImageUtil.ImageResolution.LARGE));
  }

  @Override
  public void update() {

    if (target != null && !target.isDead()) {

      aStarToTarget();

      if (getPosition().getLocation().distance(target.getPosition().getLocation()) <= range)
        attack(target);
    }
  }

  @Override
  public void attack(LivingEntity entity) {
    entity.damage(damage);
  }

  @Override
  public void setTarget(LivingEntity target) {
    this.target = target;
  }

  @Override
  public void setRange(int range) {
    this.range = range;
  }

  @Override
  public void setDamage(int damage) {
    this.damage = damage;
  }

  @Override
  public int getDamage() {
    return damage;
  }

  @Override
  public int getRange() {
    return range;
  }

  @Override
  public LivingEntity getTarget() {
    return target;
  }

  /**
   * @deprecated Use {@link #aStarToTarget()} instead. This ignores obstacles.
   */
  public void stepTowardsTarget() {

    Point2D targetPoint = target.getPosition().getLocation();
    Point2D currentPosition = getPosition().getLocation();

    if (targetPoint.getX() > currentPosition.getX()) moveRight();
    else if (targetPoint.getX() < currentPosition.getX()) moveLeft();

    if (targetPoint.getY() > currentPosition.getY()) moveDown();
    else if (targetPoint.getY() < currentPosition.getY()) moveUp();
  }

  /**
   * @deprecated Use {@link #aStarToTarget()} instead. This ignores obstacles.
   */
  @Deprecated
  public void followTarget() {

    Point2D playerPosition = target.getPosition().getLocation();
    Point2D enemyPosition = getPosition().getLocation();

    double distance = playerPosition.distance(enemyPosition);

    double x = (playerPosition.getX() - enemyPosition.getX()) / distance;
    double y = (playerPosition.getY() - enemyPosition.getY()) / distance;

    setTranslateX(getTranslateX() + x);
    setTranslateY(getTranslateY() + y);
  }

  public void aStarToTarget() {

    // TODO: Get this to work right

    Vector playerPosition =
        new Vector(
            target.getPosition().getLocation().getX(), target.getPosition().getLocation().getY());
    Vector enemyPosition =
        new Vector(getPosition().getLocation().getX(), getPosition().getLocation().getY());

    Facing closestInDistance = null;
    for (Facing facing : Facing.values()) {

      if (closestInDistance == null) {
        closestInDistance = facing;
        continue;
      }

      if (facing.getVector().multiply(movingSpeed()).add(enemyPosition).distance(playerPosition)
          < closestInDistance
              .getVector()
              .multiply(movingSpeed())
              .add(enemyPosition)
              .distance(playerPosition)) closestInDistance = facing;
    }

    if (closestInDistance == null) { // Bugged entity
      damage(healthProperty.get());
      return;
    }

    if (isObstacle(closestInDistance)) {
      for (Facing facing : Facing.values()) {

        if (facing == closestInDistance) continue;

        if (!isObstacle(facing)) {
          closestInDistance = facing;
          break;
        }
      }
    }

    moveTowardsFacing(closestInDistance);
  }

  @Override
  protected int movingSpeed() {
    return 1;
  }

  private boolean isObstacle(Facing facing) {

    Rectangle2D rectangle =
        new Rectangle2D(
            getPosition().getLocation().getX() + facing.getVector().getX(),
            getPosition().getLocation().getY() + facing.getVector().getY(),
            getWidth(),
            getHeight());

    return getPosition().getGameField().getEntities().stream()
        .filter(ObstacleObject.class::isInstance)
        .map(ObstacleObject.class::cast)
        .anyMatch(obstacleObject -> obstacleObject.getHitBox().intersects(rectangle));
  }
}
