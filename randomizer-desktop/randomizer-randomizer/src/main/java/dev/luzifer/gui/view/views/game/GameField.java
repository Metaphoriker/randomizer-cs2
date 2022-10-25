package dev.luzifer.gui.view.views.game;

import dev.luzifer.gui.view.views.game.objects.PlayerObject;
import dev.luzifer.gui.view.views.game.objects.entity.Entity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class GameField {
    
    private final ObservableList<Entity> entities = FXCollections.observableList(new ArrayList<>());
    private final GameSequence gameSequence = new GameSequence(this);
    
    private Rectangle border;
    private Runnable onGameOver;
    
    public void spawnPlayer() {
        PlayerObject playerObject = new PlayerObject(new Position(this, new Point2D(300, 300)));
        entities.add(playerObject);
    }
    
    public void startGame() {
        gameSequence.start();
    }
    
    public void stopGame() {
        gameSequence.stop();
    }
    
    public void gameOver() {
        stopGame();
        if(onGameOver != null)
            onGameOver.run();
    }
    
    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }
    
    public void setBorder(Rectangle border) {
        this.border = border;
    }
    
    public Rectangle getBorder() {
        return border;
    }
    
    public ObservableList<Entity> getEntities() {
        return entities;
    }
}
