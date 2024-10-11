package de.metaphoriker.view.game.objects.sup;

import de.metaphoriker.view.game.HealthBar;
import de.metaphoriker.view.game.Position;
import de.metaphoriker.view.game.objects.sup.entity.Facing;
import de.metaphoriker.view.game.objects.sup.entity.LivingEntity;
import de.metaphoriker.view.game.objects.sup.entity.Moveable;
import de.metaphoriker.view.game.objects.sup.entity.Vector;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class AbstractLivingGameObject extends GameObject
    implements LivingEntity, Moveable {

  protected final IntegerProperty healthProperty = new SimpleIntegerProperty(10);

  private int maxHealth;

  protected AbstractLivingGameObject(Position position, double width, double height) {
    super(position, width, height);

    HealthBar healthBar = new HealthBar(getHealth());
    healthBar.translateXProperty().bind(translateXProperty());
    healthBar.translateYProperty().bind(translateYProperty().subtract(10));

    healthProperty.addListener(
        (observable, oldValue, newValue) -> {
          healthBar.updateHealth(newValue.intValue());
          maxHealth = healthBar.getMaxHealth();
        });
    position.getGameField().getEntities().add(healthBar);
  }

  @Override
  public void setHealth(int health) {
    healthProperty.set(health);
  }

  @Override
  public int getHealth() {
    return healthProperty.get();
  }

  @Override
  public int getMaxHealth() {
    return maxHealth;
  }

  @Override
  public void damage(int amount) {
    healthProperty.set(getHealth() - amount);
  }

  // TODO: Should this exist?
  public void moveTowardsFacing(Facing facing) {

    Vector vector = facing.getVector().multiply(movingSpeed());

    setTranslateX(getTranslateX() + vector.getX());
    setTranslateY(getTranslateY() + vector.getY());
  }

  @Override
  public void moveRight() {
    setTranslateX(getTranslateX() + movingSpeed());
  }

  @Override
  public void moveLeft() {
    setTranslateX(getTranslateX() - movingSpeed());
  }

  @Override
  public void moveUp() {
    setTranslateY(getTranslateY() - movingSpeed());
  }

  @Override
  public void moveDown() {
    setTranslateY(getTranslateY() + movingSpeed());
  }

  @Override
  public void rotateLeft() {
    setRotate(getRotate() - movingSpeed());
  }

  @Override
  public void rotateRight() {
    setRotate(getRotate() + movingSpeed());
  }

  protected abstract int movingSpeed();
}
