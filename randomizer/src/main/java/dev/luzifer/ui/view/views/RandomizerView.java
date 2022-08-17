package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.event.Event;
import dev.luzifer.event.EventDispatcher;
import dev.luzifer.ui.AppStarter;
import dev.luzifer.ui.ApplicationState;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.RandomizerViewModel;
import dev.luzifer.update.UpdateChecker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;


public class RandomizerView extends View<RandomizerViewModel> {
    
    @FXML
    private Button button;
    
    @FXML
    private Tab logbookTab;
    
    @FXML
    private VBox firstVBox;
    
    @FXML
    private Tab eventTab;
    
    @FXML
    private VBox secondVBox;
    
    @FXML
    private TextArea infoArea;
    
    @FXML
    private WebView webView;
    
    @FXML
    private Tab informationTab;
    
    @FXML
    private Tab settingsTab;
    
    @FXML
    private Label updateLabel;
    
    public RandomizerView(RandomizerViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    
        logbookTab.setGraphic(new ImageView("images/logbook_icon.png"));
        eventTab.setGraphic(new ImageView("images/glossary_icon.png"));
        informationTab.setGraphic(new ImageView("images/information_icon.png"));
        settingsTab.setGraphic(new ImageView("images/setting_icon.png"));
        updateLabel.setGraphic(new ImageView("images/update_icon.png"));
        
        updateLabel.setStyle("-fx-font-weight: bold");
        updateLabel.setTextFill(Color.RED);
        updateLabel.setCursor(Cursor.HAND);
        
        updateLabel.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/Luziferium/randomizer-csgo").toURI());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    
        // this belongs into the viewmodel with properties
        UpdateChecker updateChecker = new UpdateChecker();
        updateLabel.setVisible(updateChecker.hasUpdate());
        
        informationTab.setOnSelectionChanged(event -> {
            if(informationTab.isSelected()) {
                webView.getEngine().load("https://i.imgur.com/MxAE8Wp.mp4");
            } else {
                webView.getEngine().load("");
            }
        });
        
        EventDispatcher.registerGenericHandler(event -> Platform.runLater(() -> {
            
            Label label = new Label(event.getClass().getSimpleName(), new ImageView("images/event_icon.png"));
            label.setStyle("-fx-background-color: #006400;");
            firstVBox.getChildren().add(0, label);
    
            Main.getScheduler().schedule(() -> Platform.runLater(() -> label.setStyle("-fx-background-color: #90ee90;")), 1500);
            Main.getScheduler().schedule(() -> Platform.runLater(() -> label.setStyle("")), 3000);
        }));
        
        for(Event event : EventDispatcher.getEvents()) {
            
            Label label = new Label(event.getClass().getSimpleName(), new ImageView("images/event_icon.png"));
            label.setOnMouseEntered(dragEnter -> {
                if(!label.isFocused())
                    label.setStyle("-fx-background-color: #d3d3d3;");
            });
            label.setOnMouseExited(dragExit -> {
                if(!label.isFocused())
                    label.setStyle("");
            });
            label.setOnMouseClicked(e -> {
                
                for(Node node : secondVBox.getChildren())
                    if(node != label)
                        node.setStyle("");
                
                label.requestFocus();
                label.setStyle("-fx-background-color: #A9A9A9;");
                infoArea.setText(event.description());
            });
            
            secondVBox.getChildren().add(label);
        }
    }
    
    @FXML
    public void onPress() {
        
        getViewModel().toggleRunning();
        if(AppStarter.getState() == ApplicationState.RUNNING) {
            button.setText("Stop");
        } else {
            button.setText("Start");
        }
    }
    
}
