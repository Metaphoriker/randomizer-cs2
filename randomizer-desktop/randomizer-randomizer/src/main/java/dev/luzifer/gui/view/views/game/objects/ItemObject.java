package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.entity.Item;
import dev.luzifer.gui.view.views.game.objects.entity.Player;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ItemObject extends Rectangle implements Item {
    
    private static final int DEFAULT_ITEM_HEALTH = 1;
    
    private final ItemType itemType;
    private final Position position;
    
    private int health;
    
    public ItemObject(Position position, ItemType itemType) {
        
        this.health = DEFAULT_ITEM_HEALTH;
        this.position = position;
        this.itemType = itemType;
        
        setWidth(32);
        setHeight(32);
        
        setTranslateX(position.getPosition().getX());
        setTranslateY(position.getPosition().getY());
        
        switch (itemType) {
            case WEAPON:
                setFill(ImageUtil.getImagePattern("images/weapon_icon.png", ImageUtil.ImageResolution.ORIGINAL));
                break;
            case AMMO:
                setFill(ImageUtil.getImagePattern("images/ammo_box_icon.png", ImageUtil.ImageResolution.ORIGINAL));
                break;
            case MOLOTOV:
                setFill(ImageUtil.getImagePattern("images/molotov_icon.png", ImageUtil.ImageResolution.ORIGINAL));
                break;
            default:
                setFill(Color.BLACK);
        }
    }
    
    @Override
    public void update() {
        
        position.getGameField().getEntities().stream()
                .filter(Player.class::isInstance)
                .forEach(entity -> {
            
            PlayerObject player = (PlayerObject) entity;
            
            if(player.getBoundsInParent().intersects(getBoundsInParent())) {
                player.getItems().add(this);
                damage(1);
            }
        });
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
    public void damage(int amount) {
        this.health = 0;
    }
    
    @Override
    public int getHealth() {
        return health;
    }
    
    @Override
    public ItemType getItemType() {
        return itemType;
    }
    
    public enum ItemType {
        WEAPON,
        AMMO,
        MOLOTOV
    }
}
