package dev.luzifer.gui.view.views.game;

import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Item;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Player;
import javafx.collections.ListChangeListener;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class InventoryWindow extends FlowPane {
    
    private final Player player;
    
    public InventoryWindow(Pane parent, Player player) {
    
        this.player = player;
        
        CSSUtil.applyStyle(this, getClass());
        getStyleClass().add("inventory");
    
        setBackground(ImageUtil.getBackground("images/game/inventory_background.jpg")); // TODO: Make this png for consistency
        
        player.getItems().addListener((ListChangeListener<Item>) c -> fill());
        
        setPrefSize(200, 400);
        
        setLayoutX(0);
        setLayoutY(parent.getPrefHeight() - getPrefHeight());
        
        setVisible(false);
        parent.getChildren().add(this);
    }
    
    private void fill() {
    
        getChildren().clear(); // TODO: Give each item their own images or cache them and reference to them
        
        for(Item item : player.getItems()) {
        
            ImageView imageView;
            switch (item.getItemType()) {
                case WEAPON:
                    imageView = ImageUtil.getImageView("images/game/weapon_icon.png");
                    break;
                case AMMO:
                    imageView = ImageUtil.getImageView("images/game/ammo_box_icon.png");
                    break;
                case MOLOTOV:
                    imageView = ImageUtil.getImageView("images/game/molotov_icon.png");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemType());
            }
        
            getChildren().add(imageView);
        }
    }
    
}
