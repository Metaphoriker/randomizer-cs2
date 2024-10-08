package de.metaphoriker.gui.view.views.game;

import de.metaphoriker.gui.view.views.game.objects.sub.PlayerObject;
import de.metaphoriker.gui.view.views.game.objects.sub.WallObject;
import de.metaphoriker.gui.view.views.game.objects.sup.ObstacleObject;
import de.metaphoriker.gui.view.views.game.objects.sup.entity.Entity;
import de.metaphoriker.gui.view.views.game.objects.sup.entity.Player;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public class GameField {

  private final ObservableList<Entity> entities = FXCollections.observableList(new ArrayList<>());
  private final GameSequence gameSequence = new GameSequence(this);

  private Rectangle border;
  private Runnable onGameOver;

  public Player spawnPlayer() {

    PlayerObject playerObject = new PlayerObject(new Position(this, new Point2D(300, 300)));
    entities.add(playerObject);

    return playerObject;
  }

  public void spawnObstacles() {
    for (int i = 0; i < ThreadLocalRandom.current().nextInt(3, 10); i++) {

      WallObject.WallType wallType;
      if (ThreadLocalRandom.current().nextBoolean()) wallType = WallObject.WallType.HORIZONTAL;
      else wallType = WallObject.WallType.VERTICAL;

      ObstacleObject obstacleObject =
          new WallObject(
              new Position(
                  this,
                  new Point2D(
                      ThreadLocalRandom.current().nextDouble(0, border.getWidth()),
                      ThreadLocalRandom.current().nextDouble(0, border.getHeight()))),
              wallType);

      entities.add(obstacleObject);
    }
  }

  public void startGame() {
    gameSequence.setDaemon(true);
    gameSequence.start();
  }

  public void stopGame() {
    gameSequence.interrupt();
  }

  public void gameOver() {
    stopGame();
    if (onGameOver != null) onGameOver.run();
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
