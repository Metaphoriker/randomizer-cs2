package dev.luzifer.gui.swing.game.model.api;

public interface Aggressive {

    void attack(LivingEntity entity);

    void setTarget(LivingEntity entity);

    LivingEntity getTarget();

    int getDamage();

    int getRange();

}
