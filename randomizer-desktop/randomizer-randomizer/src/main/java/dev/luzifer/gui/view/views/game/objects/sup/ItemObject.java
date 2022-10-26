package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sub.PlayerObject;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Item;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Player;
import javafx.scene.paint.Color;

public class ItemObject extends GameObject implements Item { // TODO: Make this abstract
    
    private static final int DEFAULT_ITEM_HEALTH = 1;
    
    private final ItemType itemType;
    
    private int health;
    
    public ItemObject(Position position, ItemType itemType, int width, int height) {
        super(position, width, height);
        
        this.health = DEFAULT_ITEM_HEALTH;
        this.itemType = itemType;
        
        // TODO: Remove this and make subtypes
        switch (itemType) {
            case WEAPON:
                setFill(ImageUtil.getImagePattern("images/game/weapon_icon.png", ImageUtil.ImageResolution.ORIGINAL));
                break;
            case AMMO:
                setFill(ImageUtil.getImagePattern("images/game/ammo_box_icon.png", ImageUtil.ImageResolution.ORIGINAL));
                break;
            case MOLOTOV:
                setFill(ImageUtil.getImagePattern("images/game/molotov_icon.png", ImageUtil.ImageResolution.ORIGINAL));
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
        return position; // Since it will never change
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
    public int getMaxHealth() {
        return -1;
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
