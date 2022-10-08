package dev.luzifer.gui.view.views;

import dev.luzifer.gui.component.TitledClusterContainer;
import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventDispatcher;
import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.notify.Speaker;
import dev.luzifer.model.watcher.FileSystemWatcher;
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

        CSSUtil.applyNightTheme(root);
        getIcons().add(ImageUtil.getImage("images/shuffle_icon.png"));

        toggleButton.textProperty().bindBidirectional(getViewModel().getNextStateProperty());

        // what a bad design... & big fat boilerplate TODO: refact this + its viewmodel stuff as well
        Speaker.addListener(notification -> {

            if(notification.getNotifier() == FileSystemWatcher.class) {

                EventCluster cluster = getViewModel().getCluster(notification.getKey().replace(".cluster", ""));
    
                EventDispatcher.registerGenericClusterHandler(cluster, ec -> Platform.runLater(() -> {
                    TitledClusterContainer titledClusterContainer = new TitledClusterContainer(cluster.getName(), cluster);
                    logVBox.getChildren().add(0, titledClusterContainer);
                }));
    
                EventDispatcher.registerOnFinish(cluster, ec -> Platform.runLater(() -> {
                    logVBox.getChildren().stream()
                            .filter(TitledClusterContainer.class::isInstance)
                            .filter(node -> ((TitledClusterContainer) node).getEventCluster().equals(cluster))
                            .reduce((first, second) -> second)
                            .map(TitledClusterContainer.class::cast)
                            .ifPresent(TitledClusterContainer::finish);
                }));
            }
        });

        // TODO: Viewmodel stuff
        getViewModel().getClusters().forEach(cluster -> {
            
            EventDispatcher.registerGenericClusterHandler(cluster, ec -> Platform.runLater(() -> {
                TitledClusterContainer titledClusterContainer = new TitledClusterContainer(cluster.getName(), cluster);
                logVBox.getChildren().add(0, titledClusterContainer);
            }));
    
            EventDispatcher.registerOnFinish(cluster, ec -> Platform.runLater(() -> {
                logVBox.getChildren().stream()
                        .filter(TitledClusterContainer.class::isInstance)
                        .filter(node -> ((TitledClusterContainer) node).getEventCluster().equals(cluster))
                        .reduce((first, second) -> second)
                        .map(TitledClusterContainer.class::cast)
                        .ifPresent(TitledClusterContainer::finish);
            }));
        });
        
        getViewModel().getEvents().forEach(event -> {
            EventDispatcher.registerOnFinish(event, ec -> Platform.runLater(() -> {
                logVBox.getChildren().stream()
                        .filter(TitledClusterContainer.class::isInstance)
                        .reduce((first, second) -> second)
                        .map(TitledClusterContainer.class::cast)
                        .filter(titledClusterContainer -> {
                            
                            boolean contains = false;
                            for(Event e : titledClusterContainer.getEventCluster().getEvents()) {
                                if(e.equals(event)) {
                                    contains = true;
                                    break;
                                }
                            }
                            
                            return contains;
                        })
                        .ifPresent(titledClusterContainer -> titledClusterContainer.finish(event));
            }));
        });
    }
    
    @FXML
    private void onToggle(ActionEvent actionEvent) {
        getViewModel().reactToButtonWhatever();
    }
}
