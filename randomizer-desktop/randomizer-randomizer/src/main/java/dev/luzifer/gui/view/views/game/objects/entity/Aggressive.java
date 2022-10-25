package dev.luzifer.gui.view.views.game.objects.entity;

public interface Aggressive {

    void attack(LivingEntity entity);
    
    void setTarget(LivingEntity entity);
    
    LivingEntity getTarget();

}
