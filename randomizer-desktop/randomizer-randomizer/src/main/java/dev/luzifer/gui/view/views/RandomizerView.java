package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import dev.luzifer.model.event.EventDispatcher;
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
    
        // TODO: viewmodel stuff
        EventDispatcher.registerGenericHandler(event -> {
            Platform.runLater(() -> {
                
                Label label = new Label(event.name());
                label.setFont(label.getFont().font(16));
                label.setGraphic(ImageUtil.getImageView("images/loading_gif.gif", ImageUtil.ImageResolution.SMALL));
                
                logVBox.getChildren().add(label);
            });
        });
        
        getViewModel().getEvents().forEach(event -> {
            EventDispatcher.registerOnFinish(event, e -> {
                Platform.runLater(() -> {
                    
                    Label label = (Label) logVBox.getChildren().stream()
                            .filter(node -> node instanceof Label)
                            .filter(node -> ((Label) node).getText().equals(event.name()))
                            .reduce((first, second) -> second)
                            .orElse(null);
                    
                    if(label != null)
                        label.setGraphic(ImageUtil.getImageView("images/checkmark_icon.png", ImageUtil.ImageResolution.SMALL));
                });
            });
        });
    
        getViewModel().getClusters().forEach(cluster -> {
            EventDispatcher.registerGenericClusterHandler(cluster, ec -> Platform.runLater(() -> {
        
                logVBox.getChildren().clear(); // TODO: not really "logging" anymore
                
                Label label = new Label(cluster.getName());
                label.setFont(label.getFont().font(32));
                label.setGraphic(ImageUtil.getImageView("images/loading_gif.gif", ImageUtil.ImageResolution.MEDIUM));
                
                logVBox.getChildren().add(label);
            }));
    
            EventDispatcher.registerOnFinish(cluster, ec -> Platform.runLater(() -> {
        
                logVBox.getChildren().forEach(node -> {
                    if(node instanceof Label) {
                        Label label = (Label) node;
                        if(label.getText().equals(cluster.getName()))
                            label.setGraphic(ImageUtil.getImageView("images/checkmark_icon.png", ImageUtil.ImageResolution.MEDIUM));
                    }
                });
            }));
        });
    }
    
    @FXML
    private void onToggle(ActionEvent actionEvent) {
        getViewModel().reactToButtonWhatever();
    }
}
