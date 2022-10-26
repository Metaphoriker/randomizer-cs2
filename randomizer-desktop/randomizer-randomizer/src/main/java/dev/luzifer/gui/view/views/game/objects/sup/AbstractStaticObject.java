package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.entity.LivingEntity;

public abstract class AbstractStaticObject extends GameObject implements LivingEntity {
    
    private int health = 1;
    
    protected AbstractStaticObject(Position position) {
        super(position, 20, 20);
    }

    @Override
    public void update() {
        // Nothing to do here
    }

    @Override
    public Position getPosition() {
        return position; // Since it will never change
    }
    
    @Override
    public void setHealth(int health) {
        this.health = health;
    }
    
    @Override
    public int getHealth() {
        return health;
    }
    
    @Override
    public void damage(int amount) {
        health -= amount;
    }
}
