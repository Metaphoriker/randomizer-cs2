package dev.luzifer.gui.swing.game.model.api;

public interface LivingEntity extends Entity, Moveable {

    void setHealth(int health);

    int getMaxHealth();
}
