package dev.luzifer.gui.view.views.game.objects.sub;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.WeaponImpl;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Item;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Player;
import dev.luzifer.gui.view.views.game.objects.sup.inventory.Weapon;

import dev.luzifer.gui.view.views.game.objects.sup.AbstractLivingGameObject;
import dev.luzifer.gui.view.views.game.objects.sup.ItemObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

public class PlayerObject extends AbstractLivingGameObject implements Player {
    
    private static final int DEFAULT_HEALTH = 100;
    
    private final EnumSet<KeyCode> pressedKeys = EnumSet.allOf(KeyCode.class);
    private final ObservableList<Item> items = FXCollections.observableList(new ArrayList<>());
    
    private Weapon weapon;
    
    public PlayerObject(Position position) {
        super(position, 50, 50);
        
        healthProperty.set(DEFAULT_HEALTH);
        
        setFill(ImageUtil.getImagePattern("images/figure_icon.png", ImageUtil.ImageResolution.ORIGINAL));
        
        pressedKeys.clear(); // Fix so it won't move right away when pressed a button elsewhere or whatever, idk
    }
    
    @Override
    public void update() {
        pressedKeys.forEach(key -> {
            
            Point2D predictedPosition = predictPosition(key);
            
            if(!getPosition().getGameField().getBorder().getBoundsInParent().contains(predictedPosition))
                return;
            
            for (Entity entity : getPosition().getGameField().getEntities()) {
                if (entity == this)
                    continue;
    
                if (entity instanceof ObstacleObject && ((ObstacleObject) entity).getBoundsInParent().contains(predictedPosition))
                    return;
            }
            
            switch (key) {
                case W:
                    moveUp();
                    break;
                case S:
                    moveDown();
                    break;
                case A:
                    moveLeft();
                    break;
                case D:
                    moveRight();
                    break;
                case SHIFT:
                    dash();
                    break;
            }
        });
    }
    
    @Override
    public void damage(int amount) {
        super.damage(amount);
        
        if (healthProperty.get() <= 0)
            die();
    }
    
    @Override
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    
    @Override
    public Weapon getWeapon() {
        return weapon;
    }
    
    @Override
    public ObservableList<Item> getItems() {
        return items;
    }
    
    @Override
    public void onPressKey(KeyEvent keyEvent) {
        if (!pressedKeys.contains(keyEvent.getCode()))
            pressedKeys.add(keyEvent.getCode());
    }
    
    @Override
    public void onReleaseKey(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());
    }
    
    @Override
    public void onMouseMove(MouseEvent event) {
        double angle = Math.toDegrees(Math.atan2(event.getY() - getTranslateY(), event.getX() - getTranslateX()));
        setRotate(angle);
    }
    
    @Override
    public void onMouseClick(MouseEvent event) {
    
        if (event.getButton() == MouseButton.PRIMARY && hasWeapon() && weapon.getAmmo() > 0) {
            
            double angle = Math.toRadians(getRotate());
            double x = Math.cos(angle);
            double y = Math.sin(angle);
    
            Point2D velocity = new Point2D(x, y);
            weapon.shoot(getPosition(), velocity.multiply(10), getRotate());
            
        } else if(hasWeapon() && weapon.getAmmo() <= 0) {
            
            for (Iterator<Item> iterator = items.iterator(); iterator.hasNext();) {
                if (iterator.next().getItemType() == ItemObject.ItemType.AMMO) {
                    
                    weapon.reload();
                    iterator.remove();
                    
                    break;
                }
            }
        } else if(!hasWeapon()) {
            
            for (Iterator<Item> iterator = items.iterator(); iterator.hasNext();) {
                Item item = iterator.next();
                
                if (item.getItemType() == ItemObject.ItemType.WEAPON) {
                    
                    setWeapon(new WeaponImpl());
                    iterator.remove();
                    
                    break;
                }
            }
        }
        
        if(event.getButton() == MouseButton.SECONDARY) {
            
            for (Iterator<Item> iterator = items.iterator(); iterator.hasNext();) {
                Item item = iterator.next();
                
                if (item.getItemType() == ItemObject.ItemType.MOLOTOV) {
    
                    double angle = Math.toRadians(getRotate());
                    double x = Math.cos(angle);
                    double y = Math.sin(angle);
    
                    Point2D velocity = new Point2D(x, y);
    
                    MolotovProjectileObject projectile = new MolotovProjectileObject(getPosition(), velocity.multiply(5));
                    getPosition().getGameField().getEntities().add(projectile);
                    
                    iterator.remove();
                    break;
                }
            }
            
        }
    }
    
    @Override
    protected int movingSpeed() {
        return 5;
    }
    
    private Point2D predictPosition(KeyCode keyCode) {
        
        Point2D currentPosition = new Point2D(getTranslateX(), getTranslateY());
        
        switch (keyCode) {
            case W:
                return currentPosition.add(0, -movingSpeed());
            case S:
                return currentPosition.add(0, movingSpeed());
            case A:
                return currentPosition.add(-movingSpeed(), 0);
            case D:
                return currentPosition.add(movingSpeed(), 0);
            case SHIFT:
                
                double angle = Math.toRadians(getRotate());
                double x = Math.cos(angle);
                double y = Math.sin(angle);
    
                Point2D velocity = new Point2D(x, y);
                return currentPosition.add(velocity.multiply(10));
            default:
                return currentPosition;
        }
    }
    
    private void die() {
        setFill(ImageUtil.getImagePattern("images/figure_dead_icon.png", ImageUtil.ImageResolution.ORIGINAL));
    }
    
}
