package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.entity.Aggressive;
import dev.luzifer.gui.view.views.game.objects.entity.LivingEntity;
import dev.luzifer.gui.view.views.game.objects.entity.Moveable;
import javafx.geometry.Point2D;

public class EnemyObject extends AbstractLivingGameObject implements LivingEntity, Moveable, Aggressive {
    
    private static final int DEFAULT_ENEMY_HEALTH = 10;
    
    private final int damage;
    
    private LivingEntity target;
    
    public EnemyObject(int damage, Position position) {
        super(position);
    
        this.damage = damage;
        healthProperty.set(DEFAULT_ENEMY_HEALTH);
        
        setFill(ImageUtil.getImagePattern("images/enemy_icon.png", ImageUtil.ImageResolution.ORIGINAL));
        
        setWidth(30);
        setHeight(30);
    }
    
    @Override
    public void update() {
        
        if (target != null && !target.isDead()) {
            
            followTarget();
            
            if (getPosition().getPosition().distance(target.getPosition().getPosition()) <= 5)
                attack(target);
        }
    }
    
    @Override
    public void attack(LivingEntity entity) {
        entity.damage(damage);
    }
    
    @Override
    public void setTarget(LivingEntity target) {
        this.target = target;
    }
    
    @Override
    public LivingEntity getTarget() {
        return target;
    }
    
    public void stepTowardsTarget() {
        
        Point2D targetPoint = target.getPosition().getPosition();
        Point2D currentPosition = position.getPosition();
        
        if (targetPoint.getX() > currentPosition.getX())
            moveRight();
        else if (targetPoint.getX() < currentPosition.getX())
            moveLeft();
        
        if (targetPoint.getY() > currentPosition.getY())
            moveDown();
        else if (targetPoint.getY() < currentPosition.getY())
            moveUp();
    }
    
    public void followTarget() {
        
        Point2D playerPosition = target.getPosition().getPosition();
        Point2D enemyPosition = getPosition().getPosition();
        
        double distance = playerPosition.distance(enemyPosition);
        
        double x = (playerPosition.getX() - enemyPosition.getX()) / distance;
        double y = (playerPosition.getY() - enemyPosition.getY()) / distance;
        
        setTranslateX(getTranslateX() + x);
        setTranslateY(getTranslateY() + y);
    }
    
    @Override
    protected int movingSpeed() {
        return 3;
    }
    
}
