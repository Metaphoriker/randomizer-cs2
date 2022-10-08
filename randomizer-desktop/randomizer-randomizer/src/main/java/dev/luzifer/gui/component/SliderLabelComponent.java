package dev.luzifer.gui.component;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class SliderLabelComponent extends HBox {
    
    private final Slider slider = new Slider();
    private final Label label = new Label();
    
    public SliderLabelComponent(String title, int min, int max, int value) {
        super();
        
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
        
        label.setText(title + ": " + value);
        
        slider.valueProperty().addListener((observableValue, number, t1) ->
                label.setText(title + ": " + t1.intValue()));
        
        getChildren().addAll(slider, label);
    }
    
    public Slider getSlider() {
        return slider;
    }
    
    public Label getLabel() {
        return label;
    }
    
    public int getValue() {
        return (int) slider.getValue();
    }
    
}
