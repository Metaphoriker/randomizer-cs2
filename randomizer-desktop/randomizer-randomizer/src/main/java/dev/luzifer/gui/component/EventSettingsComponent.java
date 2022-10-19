package dev.luzifer.gui.component;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.Interval;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EventSettingsComponent extends VBox {
    
    private final Label title = new Label();
    
    private final VBox vBox = new VBox();
    
    private final Event event;
    
    public EventSettingsComponent(Event event) {
        
        this.event = event;
        
        setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px;");
        
        title.setText(event.name() + " Settings");
        title.setStyle("" +
                "-fx-font-weight: bold;" +
                " -fx-font-size: 12px;" +
                " -fx-padding: 10px 0 0 0;" +
                " -fx-border-color: black;" +
                " -fx-border-width: 0 0 1 0;");
        
        if(event instanceof Interval) {
            
            SliderLabelComponent minSlider = new SliderLabelComponent("Min", 0, 10000, ((Interval) event).min());
            SliderLabelComponent maxSlider = new SliderLabelComponent("Max", 0, 10000, ((Interval) event).max());
            
            vBox.getChildren().addAll(minSlider, maxSlider);
        }
        
        getChildren().addAll(title, vBox);
    }
    
    public void apply() {
        if(event instanceof Interval) {
            ((Interval) event).setMin((int) ((SliderLabelComponent) vBox.getChildren().get(0)).getSlider().getValue());
            ((Interval) event).setMax((int) ((SliderLabelComponent) vBox.getChildren().get(1)).getSlider().getValue());
        }
    }
    
}
