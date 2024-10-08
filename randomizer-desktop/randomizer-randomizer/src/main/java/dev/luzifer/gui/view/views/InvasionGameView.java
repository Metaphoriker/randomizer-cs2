package dev.luzifer.gui.view.views;

import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.GameViewModel;
import dev.luzifer.gui.view.views.game.GameWindow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class InvasionGameView extends View<GameViewModel> {

  @FXML private Pane gameField;

  public InvasionGameView(GameViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);

    GameWindow gameWindow = new GameWindow();
    gameField.setPrefSize(gameWindow.getPrefWidth(), gameWindow.getPrefHeight());
    gameField.getChildren().add(gameWindow);

    Platform.runLater(
        () -> {
          gameWindow.initGame();
          gameWindow.start();
        });
  }
}
