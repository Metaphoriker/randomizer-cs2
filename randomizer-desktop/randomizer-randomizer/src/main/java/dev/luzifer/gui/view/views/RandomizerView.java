package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
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
    
        toggleButton.textProperty().bindBidirectional(getViewModel().getNextStateProperty());
        
        for(EventCluster cluster : getViewModel().getClusters()) {
            
            EventDispatcher.registerGenericClusterHandler(cluster, ec -> Platform.runLater(() -> {
                
                Label label = new Label(cluster.getName());
                label.setGraphic(ImageUtil.getImageView("images/loading_gif.gif"));
                logVBox.getChildren().add(label);
            }));
            
            EventDispatcher.registerOnFinish(cluster, ec -> Platform.runLater(() -> {
                
                Label label = (Label) logVBox.getChildren().get(logVBox.getChildren().size() - 1);
                label.setGraphic(ImageUtil.getImageView("images/checkmark_icon.png"));
                logVBox.getChildren().set(logVBox.getChildren().size() - 1, label);
            }));
        }
    }
    
    @FXML
    private void onToggle(ActionEvent actionEvent) {
        getViewModel().reactToButtonWhatever();
    }
}
