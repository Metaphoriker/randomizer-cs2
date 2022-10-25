package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.entity.LivingEntity;
import javafx.scene.shape.Rectangle;

public abstract class AbstractStaticObject extends Rectangle implements LivingEntity {

    private final Position position;
    
    private int health = 1;
    
    protected AbstractStaticObject(Position position) {
        super(20, 20);
        
        this.position = position;

        setTranslateX(position.getPosition().getX());
        setTranslateY(position.getPosition().getY());
    }

    @Override
    public void update() {
        // Nothing to do here
    }

    @Override
    public Position getPosition() {
        return position;
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
