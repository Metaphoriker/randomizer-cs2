package dev.luzifer.gui.view.views.game;

import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.objects.sub.EnemyObject;
import dev.luzifer.gui.view.views.game.objects.sup.ItemObject;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Item;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Player;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameWindow extends Pane {
    
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 600;
    
    private final Rectangle border = new Rectangle();
    
    private final Label gameOverLabel = new Label("Game Over");
    private final Label scoreLabel = new Label("Score: 0");
    private final Label howToPlayLabel = new Label("WASD\nM-CLICK\nMOUSE\nI\nSHIFT");
    
    private final Label ammoBoxesLabel = new Label("x0");
    private final Label molotovLabel = new Label("x0");
    
    {
        CSSUtil.applyStyle(this, GameWindow.class);
        
        gameOverLabel.getStyleClass().add("game-over");
        scoreLabel.getStyleClass().add("score");
        howToPlayLabel.getStyleClass().add("how-to-play");
        
        ammoBoxesLabel.getStyleClass().add("ammo-box");
        molotovLabel.getStyleClass().add("molotov");
    }
    
    private final GameField gameField = new GameField();
    
    private int score = 0; // Property
    
    public GameWindow() {
    
        setPrefSize(WIDTH, HEIGHT);
        
        setupBorder();
        setupLabelPictures();
    
        getChildren().addAll(border, gameOverLabel, scoreLabel, howToPlayLabel, ammoBoxesLabel, molotovLabel);
    }
    
    public void initGame() {
        setupGame();
    }
    
    public void start() {
        gameField.startGame();
    }
    
    public void stop() {
        gameField.stopGame();
    }
    
    private void setupGame() {
        
        syncWithGame();
    
        gameField.setOnGameOver(() -> {
            gameOverLabel.setOpacity(1);
            stop();
        });
        gameField.setBorder(border);
        
        setupPlayer();
        gameField.spawnObstacles();
    }
    
    private void setupPlayer() {
        
        Player player = gameField.spawnPlayer();
        InventoryWindow inventoryWindow = new InventoryWindow(this, player);
    
        getScene().setOnKeyPressed(keyEvent -> {
            
            if(keyEvent.getCode() == KeyCode.I) {
                inventoryWindow.setVisible(!inventoryWindow.isVisible());
                return;
            }
            
            player.onPressKey(keyEvent);
        });
        getScene().setOnKeyReleased(player::onReleaseKey);
        getScene().setOnMouseMoved(player::onMouseMove);
        getScene().setOnMousePressed(player::onMouseClick);
    
        player.getItems().addListener((ListChangeListener<Item>) c1 -> {
            while(c1.next()) {
                molotovLabel.setText("x" + player.getItems().stream()
                        .filter(ItemObject.class::isInstance)
                        .filter(item1 -> item1.getItemType() == ItemObject.ItemType.MOLOTOV)
                        .count());
            
                ammoBoxesLabel.setText("x" + player.getItems().stream()
                        .filter(ItemObject.class::isInstance)
                        .filter(item1 -> item1.getItemType() == ItemObject.ItemType.AMMO)
                        .count());
            }
        });
    }
    
    private void syncWithGame() {
        
        gameField.getEntities().addListener((ListChangeListener<Entity>) c -> {
            while(c.next()) {
                if(c.wasAdded()) {
                    c.getAddedSubList().stream()
                            .filter(Node.class::isInstance).forEach(entity -> getChildren().add(0, (Node) entity));
                } else if(c.wasRemoved()) {
                    c.getRemoved().stream()
                            .filter(Node.class::isInstance).forEach(entity -> {
                                
                                if(entity instanceof EnemyObject) {
                                    score += ((EnemyObject) entity).getMaxHealth();
                                    scoreLabel.setText("Score: " + score);
                                }
                                
                                getChildren().remove((Node) entity);
                            });
                }
            }
        });
    }
    
    private void setupLabelPictures() {
        ammoBoxesLabel.setGraphic(ImageUtil.getImageView("images/game/ammo_box_icon.png"));
        molotovLabel.setGraphic(ImageUtil.getImageView("images/game/molotov_icon.png"));
    }
    
    private void setupBorder() {
        
        border.setWidth(WIDTH);
        border.setHeight(HEIGHT);
        
        border.setFill(null);
        border.setStroke(new Color(0.5, 0.5, 0.5, 1));
    }
    
}
