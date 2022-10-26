package dev.luzifer.gui.view.views.game.objects.sup.entity;

public interface Aggressive {

    void attack(LivingEntity entity);
    
    void setTarget(LivingEntity entity);
    
    void setRange(int range);
    
    void setDamage(int damage);
    
    int getDamage();
    
    int getRange();
    
    LivingEntity getTarget();

}
