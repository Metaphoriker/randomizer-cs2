package dev.luzifer.gui.view.views.game;

import javafx.geometry.Point2D;

public class Position {
    
    private final GameField gameField;
    private final Point2D position;
    
    public Position(GameField gameField, Point2D position) {
        this.gameField = gameField;
        this.position = position;
    }
    
    public GameField getGameField() {
        return gameField;
    }
    
    public Point2D getPosition() {
        return position;
    }
    
}
