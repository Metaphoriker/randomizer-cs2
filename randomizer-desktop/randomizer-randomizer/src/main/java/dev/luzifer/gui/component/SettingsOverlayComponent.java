package dev.luzifer.gui.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SettingsOverlayComponent extends Pane {
    
    private final SliderLabelComponent minSlider = new SliderLabelComponent("Min", 0, 120, 30);
    private final SliderLabelComponent maxSlider = new SliderLabelComponent("Max", 0, 120, 120);

    private final Button applyButton = new Button("Apply");
    
    private final Label failedLabel = new Label("Failed to apply settings");

    public SettingsOverlayComponent() {
        
        setVisible(false);
        
        failedLabel.setStyle("-fx-text-fill: red");
        failedLabel.setOpacity(0);
        
        TitledPane titledPane = new TitledPane("Settings", new VBox(minSlider, maxSlider, applyButton));
        titledPane.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1)
                setVisible(false);
        });
        
        getChildren().addAll(new VBox(titledPane, failedLabel));
    }
    
    public SliderLabelComponent getMinSlider() {
        return minSlider;
    }
    
    public SliderLabelComponent getMaxSlider() {
        return maxSlider;
    }
    
    public Button getApplyButton() {
        return applyButton;
    }
    
    public Label getFailedLabel() {
        return failedLabel;
    }
}
