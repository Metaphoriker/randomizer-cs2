package dev.luzifer.gui.view.views;

import dev.luzifer.gui.component.FuckYouControl;
import dev.luzifer.gui.component.SliderLabelComponent;
import dev.luzifer.gui.component.TitledClusterContainer;
import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import dev.luzifer.model.event.EventDispatcher;
import dev.luzifer.model.event.EventExecutorRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
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
    
    @FXML
    private Button settingsButton;
    
    @FXML
    private Pane settingsOverlay;
    
    @FXML
    private VBox fuckYourselfVBox;

    public RandomizerView(RandomizerViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    
        CSSUtil.applyNightTheme(root);
        
        getIcons().add(ImageUtil.getImage("images/shuffle_icon.png"));
        settingsButton.setGraphic(ImageUtil.getImageView("images/settings_icon.png", ImageUtil.ImageResolution.SMALL));
    
        fuckYourselfVBox.getChildren().add(new SliderLabelComponent("Min", 0, 120, 30));
        fuckYourselfVBox.getChildren().add(new SliderLabelComponent("Max", 0, 120, 120));
        
        FuckYouControl fuckYouControl = new FuckYouControl(root);
        fuckYouControl.addOverlay(settingsOverlay);
        
        settingsOverlay.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if(!t1) {
                
                SliderLabelComponent min = (SliderLabelComponent) fuckYourselfVBox.getChildren().get(0);
                SliderLabelComponent max = (SliderLabelComponent) fuckYourselfVBox.getChildren().get(1);
                
                if(min.getValue() >= max.getValue() || max.getValue() <= min.getValue()) {
                    FuckYouControl.show(settingsOverlay, settingsButton);
                    return;
                }
                
                EventExecutorRunnable.setMinWaitTime(min.getValue() * 1000);
                EventExecutorRunnable.setMaxWaitTime(max.getValue() * 1000);
            }
        });
        toggleButton.textProperty().bindBidirectional(getViewModel().getNextStateProperty());
        
        getViewModel().getClusters().forEach(cluster -> {
            
            EventDispatcher.registerGenericClusterHandler(cluster, cl -> {
                TitledClusterContainer container = new TitledClusterContainer(cl.getName(), cl);
                Platform.runLater(() -> logVBox.getChildren().add(0, container));
            });
            
            EventDispatcher.registerOnFinish(cluster, cl -> {
                Platform.runLater(() -> {
                    logVBox.getChildren().stream()
                            .filter(TitledClusterContainer.class::isInstance)
                            .map(TitledClusterContainer.class::cast)
                            .filter(container -> container.getEventCluster().equals(cl))
                            .forEach(TitledClusterContainer::finish);
                });
            });
        });
    
        getViewModel().getEvents().forEach(event -> {
            EventDispatcher.registerOnFinish(event, e -> {
                Platform.runLater(() -> {
                    logVBox.getChildren().stream()
                            .filter(TitledClusterContainer.class::isInstance)
                            .map(TitledClusterContainer.class::cast)
                            .filter(container -> !container.isFinished())
                            .filter(container -> container.getEventCluster().getEvents().contains(event))
                            .forEach(container -> container.finish(event));
                });
            });
        });
        
        EventDispatcher.registerGenericHandler(event -> {
            Platform.runLater(() -> {
                logVBox.getChildren().stream()
                        .filter(TitledClusterContainer.class::isInstance)
                        .map(TitledClusterContainer.class::cast)
                        .filter(container -> !container.isFinished())
                        .filter(container -> container.getEventCluster().getEvents().contains(event))
                        .forEach(container -> container.visualizeExecution(event));
            });
        });
    }
    
    @FXML
    private void onToggle(ActionEvent actionEvent) {
        getViewModel().reactToButtonWhatever();
    }
    
    @FXML
    private void onOpenSettings(ActionEvent actionEvent) {
        FuckYouControl.show(settingsOverlay, settingsButton);
    }
    
    @FXML
    private void onCloseSettings(ActionEvent actionEvent) {
        FuckYouControl.hide(settingsOverlay);
    }
}
