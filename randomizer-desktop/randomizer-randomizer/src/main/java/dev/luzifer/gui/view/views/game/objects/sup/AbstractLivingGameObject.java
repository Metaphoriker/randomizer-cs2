package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.HealthBar;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.entity.LivingEntity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Moveable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;

public abstract class AbstractLivingGameObject extends GameObject implements LivingEntity, Moveable {
    
    protected final IntegerProperty healthProperty = new SimpleIntegerProperty(10);
    
    private int maxHealth;
    
    protected AbstractLivingGameObject(Position position, double width, double height) {
        super(position, width, height);
        
        HealthBar healthBar = new HealthBar(getHealth());
        healthBar.translateXProperty().bind(translateXProperty());
        healthBar.translateYProperty().bind(translateYProperty().subtract(10));
        
        healthProperty.addListener((observable, oldValue, newValue) -> {
            healthBar.updateHealth(newValue.intValue());
            maxHealth = healthBar.getMaxHealth();
        });
        position.getGameField().getEntities().add(healthBar);
    }
    
    @Override
    public void setHealth(int health) {
        healthProperty.set(health);
    }
    
    @Override
    public int getHealth() {
        return healthProperty.get();
    }
    
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }
    
    @Override
    public void damage(int amount) {
        healthProperty.set(getHealth()-amount);
    }
    
    @Override
    public void moveRight() {
        setTranslateX(getTranslateX() + movingSpeed());
    }
    
    @Override
    public void moveLeft() {
        setTranslateX(getTranslateX() - movingSpeed());
    }
    
    @Override
    public void moveUp() {
        setTranslateY(getTranslateY() - movingSpeed());
    }
    
    @Override
    public void moveDown() {
        setTranslateY(getTranslateY() + movingSpeed());
    }
    
    @Override
    public void dash() {
        
        double angle = Math.toRadians(getRotate());
        double x = Math.cos(angle);
        double y = Math.sin(angle);
    
        Point2D velocity = new Point2D(x, y);
    
        setTranslateX(getTranslateX() + velocity.getX() * 10);
        setTranslateY(getTranslateY() + velocity.getY() * 10);
    }
    
    @Override
    public void rotateLeft() {
        setRotate(getRotate() - movingSpeed());
    }
    
    @Override
    public void rotateRight() {
        setRotate(getRotate() + movingSpeed());
    }
    
    protected abstract int movingSpeed();
    
}
