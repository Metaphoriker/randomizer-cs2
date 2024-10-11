package de.metaphoriker.view.game.objects.sup.entity;

import de.metaphoriker.view.game.objects.sup.inventory.Weapon;
import javafx.collections.ObservableList;

public interface Player extends Moveable, Controllable, LivingEntity {

  void setWeapon(Weapon weapon);

  Weapon getWeapon();

  ObservableList<Item> getItems();

  default boolean hasWeapon() {
    return getWeapon() != null;
  }

  default boolean hasItems() {
    return !getItems().isEmpty();
  }
}
