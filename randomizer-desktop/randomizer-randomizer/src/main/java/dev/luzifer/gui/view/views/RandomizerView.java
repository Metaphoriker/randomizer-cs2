package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventDispatcher;
import dev.luzifer.model.event.cluster.EventCluster;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RandomizerView extends View<RandomizerViewModel> {
    
    @FXML
    private VBox root;

    @FXML
    private VBox logVBox;

    @FXML
    private Button toggleButton;

    public RandomizerView(RandomizerViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    
        
        /* TODO: dev nodes on german, y'all suck
         *  - fuehrt aktuell 6x das MouseLeftClickEvent aus, da der den Handler dafür 3x registriert
         *  - event API überarbeiten, die Events sind alle die gleiche instanz, was sich mit dem EventDispatcher nicht verträgt
         *  - nvm. vergiss das was oben steht, ich bin einfach dumm und führ das immer wieder aus und registriere immer wieder handler aufs neue
         *  - nvm nvm, keine ahnung was kaputt ist
         */
        for(EventCluster cluster : getViewModel().getClusters()) {
            
            EventDispatcher.registerGenericClusterHandler(cluster, ec -> {
                Platform.runLater(() -> {
                    logVBox.getChildren().add(new Label(cluster.getName()));
                });
            });
    
            for(Event event : cluster.getEvents()) {
                EventDispatcher.registerHandler(event.getClass(), e -> {
                    Platform.runLater(() -> {
                
                        Label label = new Label(event.getClass().getSimpleName());
                        label.setGraphic(ImageUtil.getImageView("images/loading_gif.gif"));
                
                        logVBox.getChildren().add(label);
                    });
                });
        
                EventDispatcher.registerOnFinish(event,  e -> {
                    Platform.runLater(() -> {
                        logVBox.getChildren().stream()
                                .filter(node -> node instanceof Label)
                                .filter(node -> ((Label) node).getText().equals(event.getClass().getSimpleName()))
                                .map(node -> (Label) node)
                                .findFirst().ifPresent(node -> {
                                    node.setGraphic(ImageUtil.getImageView("images/checkmark_icon.png"));
                                });
                    });
                });
            }
        }
    }
    
    @FXML
    private void onToggle(ActionEvent actionEvent) {

    }
}
