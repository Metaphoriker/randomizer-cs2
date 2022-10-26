package dev.luzifer.gui.view.views.game.objects.sup.entity;

import dev.luzifer.gui.view.views.game.objects.sup.inventory.Weapon;

import java.util.List;

public interface Player extends Moveable, Controllable, LivingEntity {

    void setWeapon(Weapon weapon);
    
    Weapon getWeapon();
    
    List<Item> getItems();
    
    default boolean hasWeapon() {
        return getWeapon() != null;
    }
    
    default boolean hasItems() {
        return !getItems().isEmpty();
    }
    
}
