package dev.luzifer.gui.view.views.game.objects.sub;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Aggressive;
import dev.luzifer.gui.view.views.game.objects.sup.entity.LivingEntity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Moveable;
import dev.luzifer.gui.view.views.game.objects.sup.AbstractLivingGameObject;
import javafx.geometry.Point2D;

public class EnemyObject extends AbstractLivingGameObject implements LivingEntity, Moveable, Aggressive {
    
    private static final int DEFAULT_ENEMY_HEALTH = 10;
    
    private LivingEntity target;
    private int range = 10;
    private int damage = 2;
    
    public EnemyObject(Position position) {
        super(position, 30, 30);
    
        healthProperty.set(DEFAULT_ENEMY_HEALTH);
        setFill(ImageUtil.getImagePattern("images/game/enemy_icon.png", ImageUtil.ImageResolution.ORIGINAL));
    }
    
    @Override
    public void update() {
        
        if (target != null && !target.isDead()) {
            
            followTarget();
            
            if (getPosition().getLocation().distance(target.getPosition().getLocation()) <= range)
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
    public void setRange(int range) {
        this.range = range;
    }
    
    @Override
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    @Override
    public int getDamage() {
        return damage;
    }
    
    @Override
    public int getRange() {
        return range;
    }
    
    @Override
    public LivingEntity getTarget() {
        return target;
    }
    
    public void stepTowardsTarget() {
        
        Point2D targetPoint = target.getPosition().getLocation();
        Point2D currentPosition = getPosition().getLocation();
        
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
        
        Point2D playerPosition = target.getPosition().getLocation();
        Point2D enemyPosition = getPosition().getLocation();
        
        double distance = playerPosition.distance(enemyPosition);
        
        double x = (playerPosition.getX() - enemyPosition.getX()) / distance;
        double y = (playerPosition.getY() - enemyPosition.getY()) / distance;
        
        setTranslateX(getTranslateX() + x);
        setTranslateY(getTranslateY() + y);
    }
    
    @Override
    protected int movingSpeed() {
        return 1;
    }
    
}
