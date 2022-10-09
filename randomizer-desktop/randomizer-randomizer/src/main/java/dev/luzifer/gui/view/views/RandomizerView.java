package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.FuckYouControl;
import dev.luzifer.gui.component.SliderLabelComponent;
import dev.luzifer.gui.component.TitledClusterContainer;
import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
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
        
        setupStylingAndGraphics();
        
        SliderLabelComponent minSlider = new SliderLabelComponent("Min", 0, 120, 30);
        SliderLabelComponent maxSlider = new SliderLabelComponent("Max", 0, 120, 120);
        
        setupOverlay(minSlider, maxSlider);
        setupBindings(minSlider, maxSlider);
        
        setupExecutionReactions();
    }
    
    @FXML
    private void onToggle(ActionEvent actionEvent) {
        getViewModel().toggleApplicationState();
    }
    
    @FXML
    private void onOpenSettings(ActionEvent actionEvent) {
        FuckYouControl.show(settingsOverlay, settingsButton);
    }
    
    @FXML
    private void onCloseSettings(ActionEvent actionEvent) {
        FuckYouControl.hide(settingsOverlay);
    }
    
    private void setupStylingAndGraphics() {
    
        CSSUtil.applyTheme(root, CSSUtil.Theme.MISTER_SILVER);
        
        getIcons().add(ImageUtil.getImage("images/shuffle_icon.png"));
        settingsButton.setGraphic(ImageUtil.getImageView("images/settings_icon.png", ImageUtil.ImageResolution.SMALL));
    }
    
    private void setupOverlay(SliderLabelComponent minSlider, SliderLabelComponent maxSlider) {
        
        fuckYourselfVBox.getChildren().add(minSlider);
        fuckYourselfVBox.getChildren().add(maxSlider);
        
        FuckYouControl fuckYouControl = new FuckYouControl(root);
        fuckYouControl.addOverlay(settingsOverlay);
    }
    
    private void setupBindings(SliderLabelComponent minSlider, SliderLabelComponent maxSlider) {
        
        getViewModel().getVisibleProperty().bindBidirectional(settingsOverlay.visibleProperty());
        getViewModel().getNextStateProperty().bindBidirectional(toggleButton.textProperty());
        
        getViewModel().getMinWaitTimeProperty().bind(minSlider.getSlider().valueProperty());
        getViewModel().getMaxWaitTimeProperty().bind(maxSlider.getSlider().valueProperty());
    }
    
    private void setupExecutionReactions() {
    
        getViewModel().setOnClusterExecution(cluster -> Platform.runLater(() -> {
            TitledClusterContainer titledClusterContainer = new TitledClusterContainer(cluster.getName(), cluster);
            logVBox.getChildren().add(0, titledClusterContainer);
        }));
    
        getViewModel().setOnClusterExecutionFinished(cluster -> Platform.runLater(() -> {
                    logVBox.getChildren().stream()
                            .filter(TitledClusterContainer.class::isInstance)
                            .map(TitledClusterContainer.class::cast)
                            .filter(container -> container.getEventCluster().equals(cluster))
                            .forEach(TitledClusterContainer::finish);
                }
        ));
    
        getViewModel().setOnEventExecutionFinished(event -> Platform.runLater(() -> {
            logVBox.getChildren().stream()
                    .filter(TitledClusterContainer.class::isInstance)
                    .map(TitledClusterContainer.class::cast)
                    .filter(container -> !container.isFinished())
                    .filter(container -> container.getEventCluster().getEvents().contains(event))
                    .forEach(container -> container.finish(event));
        }));
    
        getViewModel().setOnEventExecution(event -> Platform.runLater(() -> {
            logVBox.getChildren().stream()
                    .filter(TitledClusterContainer.class::isInstance)
                    .map(TitledClusterContainer.class::cast)
                    .filter(container -> !container.isFinished())
                    .filter(container -> container.getEventCluster().getEvents().contains(event))
                    .forEach(container -> container.visualizeExecution(event));
        }));
    }
}
