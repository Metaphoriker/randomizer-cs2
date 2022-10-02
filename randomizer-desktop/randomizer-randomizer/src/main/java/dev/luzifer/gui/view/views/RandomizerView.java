package dev.luzifer.gui.view.views;

import dev.luzifer.gui.component.TitledClusterContainer;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import dev.luzifer.model.event.EventDispatcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
        getViewModel().getEvents().forEach(event -> {
            EventDispatcher.registerOnFinish(event, e -> {
                Platform.runLater(() -> {
    
                    TitledClusterContainer titledClusterContainer = (TitledClusterContainer) logVBox.getChildren().stream()
                            .filter(TitledClusterContainer.class::isInstance)
                            .filter(node -> ((TitledClusterContainer) node).getEventLabels().stream()
                                    .anyMatch(label -> label.getText().equals(event.name())))
                            .reduce((first, second) -> second)
                            .orElse(null);
    
                    if(titledClusterContainer != null)
                        titledClusterContainer.finish(event);
                });
            });
        });
    
        getViewModel().getClusters().forEach(cluster -> {
            EventDispatcher.registerGenericClusterHandler(cluster, ec -> Platform.runLater(() -> {
                TitledClusterContainer titledClusterContainer = new TitledClusterContainer(cluster.getName(), cluster);
                logVBox.getChildren().add(0, titledClusterContainer);
            }));
    
            EventDispatcher.registerOnFinish(cluster, ec -> Platform.runLater(() -> {
                
                TitledClusterContainer titledClusterContainer = (TitledClusterContainer) logVBox.getChildren().stream()
                        .filter(TitledClusterContainer.class::isInstance)
                        .filter(node -> ((TitledClusterContainer) node).getEventCluster().equals(cluster))
                        .reduce((first, second) -> second)
                        .orElse(null);
                
                if(titledClusterContainer != null)
                    titledClusterContainer.finish();
            }));
        });
    }
    
    @FXML
    private void onToggle(ActionEvent actionEvent) {
        getViewModel().reactToButtonWhatever();
    }
}
