package dev.luzifer.gui.view.views.game.objects.sup.entity;

public interface LivingEntity extends Entity {
    
    void setHealth(int health);
    
    void damage(int amount);
    
    int getHealth();
    
    default boolean isDead() {
        return getHealth() <= 0;
    }
    
}
