package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.entity.LivingEntity;
import dev.luzifer.gui.view.views.game.objects.entity.Projectile;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public class ProjectileObject extends Rectangle implements Projectile { // 1

    private final Point2D velocity;
    private final Position position;
    
    private int health = 1;
    
    public ProjectileObject(Position position, Point2D velocity) { // 2
        super(20, 20);
        
        this.position = position;
        this.velocity = velocity;
        
        setTranslateX(position.getPosition().getX());
        setTranslateY(position.getPosition().getY());
    }
    
    @Override
    public void update() {
        
        setTranslateX(getTranslateX() + velocity.getX());
        setTranslateY(getTranslateY() + velocity.getY());
        
        getPosition().getGameField().getEntities().stream()
                .filter(ObstacleObject.class::isInstance)
                .map(ObstacleObject.class::cast)
                .filter(entity -> entity.getBoundsInParent().intersects(getBoundsInParent()))
                .forEach(entity -> damage(1));
    }
    
    @Override
    public Position getPosition() {
        return new Position(position.getGameField(), new Point2D(getTranslateX(), getTranslateY()));
    }
    
    @Override
    public Point2D getVelocity() {
        return velocity;
    }
    
    @Override
    public void setHealth(int health) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void damage(int amount) {
        health -= amount;
    }
    
    @Override
    public int getHealth() {
        return health;
    }
}
