package de.metaphoriker.view;

import de.metaphoriker.view.game.GameWindow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

@View
public class GameView extends Pane implements Initializable {

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    GameWindow gameWindow = new GameWindow();
    setPrefSize(gameWindow.getPrefWidth(), gameWindow.getPrefHeight());
    getChildren().add(gameWindow);

    Platform.runLater(
        () -> {
          gameWindow.initGame();
          gameWindow.start();
        });
  }
}
