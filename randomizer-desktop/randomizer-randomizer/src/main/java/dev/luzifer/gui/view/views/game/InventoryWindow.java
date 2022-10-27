package dev.luzifer.gui.view.views.game;

import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Item;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Player;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class InventoryWindow extends VBox {

    private final Label title = new Label("Inventory");
    private final FlowPane flowPane = new FlowPane();

    private final Player player;
    
    public InventoryWindow(Pane parent, Player player) {
    
        this.player = player;
        player.getItems().addListener((ListChangeListener<Item>) c -> fill());

        CSSUtil.applyStyle(this, getClass());

        title.getStyleClass().add("title");
        flowPane.getStyleClass().add("inventory");

        flowPane.setBackground(ImageUtil.getBackground("images/game/inventory_background.jpg")); // TODO: Make this png for consistency

        flowPane.setPrefSize(200, 400);
        title.setPrefSize(flowPane.getPrefWidth(), 30);

        setLayoutX(0);
        setLayoutY(parent.getPrefHeight() - flowPane.getPrefHeight() - title.getPrefHeight());
        
        setVisible(false);

        getChildren().addAll(title, flowPane);
        parent.getChildren().add(this);
    }
    
    private void fill() {

        flowPane.getChildren().clear(); // TODO: Give each item their own images or cache them and reference to them
        
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

            flowPane.getChildren().add(imageView);
        }
    }
    
}
