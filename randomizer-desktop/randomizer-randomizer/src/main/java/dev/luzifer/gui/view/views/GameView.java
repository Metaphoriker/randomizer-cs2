package dev.luzifer.gui.view.views;

import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.GameViewModel;
import dev.luzifer.gui.view.views.game.GameWindow;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameView extends View<GameViewModel> {
    
    @FXML
    private Pane gameField;
    
    public GameView(GameViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        
        GameWindow gameWindow = new GameWindow();
        gameField.getChildren().add(gameWindow);
        
        Platform.runLater(() -> {
            gameWindow.initGame();
            gameWindow.start();
        });
    }
}
