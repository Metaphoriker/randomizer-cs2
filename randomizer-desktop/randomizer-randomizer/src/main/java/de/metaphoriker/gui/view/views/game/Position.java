package de.metaphoriker.gui.view.views.game;

import javafx.geometry.Point2D;

public class Position {

  private final GameField gameField;
  private final Point2D location;

  public Position(GameField gameField, Point2D location) {
    this.gameField = gameField;
    this.location = location;
  }

  public GameField getGameField() {
    return gameField;
  }

  public Point2D getLocation() {
    return location;
  }
}
