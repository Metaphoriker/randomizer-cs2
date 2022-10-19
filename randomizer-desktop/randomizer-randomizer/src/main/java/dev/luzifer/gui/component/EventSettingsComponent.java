package dev.luzifer.gui.component;

import dev.luzifer.model.event.Event;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EventSettingsComponent extends VBox {
    
    private final Label title = new Label();
    
    private final VBox vBox = new VBox();
    
    public EventSettingsComponent(Event event) {
        
        title.setText(event.name() + " Settings");
        
        // Build settings
        
        getChildren().addAll(title, vBox);
    }
    
}
