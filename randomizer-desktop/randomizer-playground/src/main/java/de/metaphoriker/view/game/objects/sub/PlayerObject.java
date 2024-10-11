package de.metaphoriker.view.game.objects.sub;

import de.metaphoriker.util.ImageUtil;
import de.metaphoriker.view.game.Position;
import de.metaphoriker.view.game.objects.WeaponImpl;
import de.metaphoriker.view.game.objects.sup.AbstractLivingGameObject;
import de.metaphoriker.view.game.objects.sup.ItemObject;
import de.metaphoriker.view.game.objects.sup.ObstacleObject;
import de.metaphoriker.view.game.objects.sup.entity.Facing;
import de.metaphoriker.view.game.objects.sup.entity.Item;
import de.metaphoriker.view.game.objects.sup.entity.Player;
import de.metaphoriker.view.game.objects.sup.entity.Vector;
import de.metaphoriker.view.game.objects.sup.inventory.Weapon;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PlayerObject extends AbstractLivingGameObject implements Player {

  private static final int DEFAULT_HEALTH = 100;

  private final EnumSet<KeyCode> pressedKeys = EnumSet.allOf(KeyCode.class);
  private final ObservableList<Item> items = FXCollections.observableList(new ArrayList<>());

  private Weapon weapon;

  public PlayerObject(Position position) {
    super(position, 50, 50);

    healthProperty.set(DEFAULT_HEALTH);

    setFill(
        ImageUtil.getImagePattern("images/game/figure_icon.png", ImageUtil.ImageResolution.LARGE));

    pressedKeys
        .clear(); // Fix so it won't move right away when pressed a button elsewhere or whatever,
    // idk
  }

  @Override
  public void update() {
    pressedKeys.forEach(
        key -> {
          Facing facing = null;
          Runnable methodCall = null;

          switch (key) {
            case W:
              facing = Facing.NORTH;
              methodCall = this::moveUp;
              break;
            case S:
              facing = Facing.SOUTH;
              methodCall = this::moveDown;
              break;
            case A:
              facing = Facing.WEST;
              methodCall = this::moveLeft;
              break;
            case D:
              facing = Facing.EAST;
              methodCall = this::moveRight;
              break;
          }

          if (facing == null && methodCall == null) return;

          Vector vector = facing.getVector().multiply(movingSpeed());
          if (!rayTrace(facing, movingSpeed(), ObstacleObject.class).isEmpty()
              || getPosition()
                  .getGameField()
                  .getBorder()
                  .contains(
                      getPosition().getLocation().add(new Point2D(vector.getX(), vector.getY()))))
            return;

          methodCall.run();
        });
  }

  @Override
  public void damage(int amount) {
    super.damage(amount);

    if (healthProperty.get() <= 0) die();
  }

  @Override
  public void setWeapon(Weapon weapon) {
    this.weapon = weapon;
  }

  @Override
  public Weapon getWeapon() {
    return weapon;
  }

  @Override
  public ObservableList<Item> getItems() {
    return items;
  }

  @Override
  public void onPressKey(KeyEvent keyEvent) {
    if (!pressedKeys.contains(keyEvent.getCode())) pressedKeys.add(keyEvent.getCode());
  }

  @Override
  public void onReleaseKey(KeyEvent keyEvent) {
    pressedKeys.remove(keyEvent.getCode());
  }

  @Override
  public void onMouseMove(MouseEvent event) {
    double angle =
        Math.toDegrees(Math.atan2(event.getY() - getTranslateY(), event.getX() - getTranslateX()));
    setRotate(angle);
  }

  @Override
  public void onMouseClick(MouseEvent event) {

    if (event.getButton() == MouseButton.PRIMARY && hasWeapon() && weapon.getAmmo() > 0) {

      double angle = Math.toRadians(getRotate());
      double x = Math.cos(angle);
      double y = Math.sin(angle);

      Point2D velocity = new Point2D(x, y);
      weapon.shoot(getPosition(), velocity.multiply(10), getRotate());

    } else if (hasWeapon() && weapon.getAmmo() <= 0) {

      for (Iterator<Item> iterator = items.iterator(); iterator.hasNext(); ) {
        if (iterator.next().getItemType() == ItemObject.ItemType.AMMO) {

          weapon.reload();
          iterator.remove();

          break;
        }
      }
    } else if (!hasWeapon()) {

      for (Iterator<Item> iterator = items.iterator(); iterator.hasNext(); ) {
        Item item = iterator.next();

        if (item.getItemType() == ItemObject.ItemType.WEAPON) {

          setWeapon(new WeaponImpl());
          iterator.remove();

          break;
        }
      }
    }

    if (event.getButton() == MouseButton.SECONDARY) {

      for (Iterator<Item> iterator = items.iterator(); iterator.hasNext(); ) {
        Item item = iterator.next();

        if (item.getItemType() == ItemObject.ItemType.MOLOTOV) {

          double angle = Math.toRadians(getRotate());
          double x = Math.cos(angle);
          double y = Math.sin(angle);

          Point2D velocity = new Point2D(x, y);

          MolotovProjectileObject projectile =
              new MolotovProjectileObject(getPosition(), velocity.multiply(5));
          getPosition().getGameField().getEntities().add(projectile);

          iterator.remove();
          break;
        }
      }
    }
  }

  @Override
  protected int movingSpeed() {
    return 5;
  }

  private void die() {
    setFill(
        ImageUtil.getImagePattern(
            "images/game/figure_dead_icon.png", ImageUtil.ImageResolution.LARGE));
  }
}
