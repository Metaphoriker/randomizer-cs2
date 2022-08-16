package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.event.Event;
import dev.luzifer.event.EventDispatcher;
import dev.luzifer.ui.AppStarter;
import dev.luzifer.ui.ApplicationState;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.RandomizerViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class RandomizerView extends View<RandomizerViewModel> {
    
    @FXML
    private Button button;
    
    @FXML
    private Tab firstTab;
    
    @FXML
    private VBox firstVBox;
    
    @FXML
    private Tab secondTab;
    
    @FXML
    private VBox secondVBox;
    
    @FXML
    private TextArea infoArea;
    
    @FXML
    private WebView webView;
    
    @FXML
    private Tab thirdTab;
    
    public RandomizerView(RandomizerViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    
        firstTab.setGraphic(new ImageView("images/logbook_icon.png"));
        secondTab.setGraphic(new ImageView("images/glossary_icon.png"));
        
        thirdTab.setOnSelectionChanged(event -> {
            if(thirdTab.isSelected()) {
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
        
        getViewModel().callback();
        if(AppStarter.getState() == ApplicationState.RUNNING) {
            button.setText("Stop");
        } else {
            button.setText("Start");
        }
    }
    
}
