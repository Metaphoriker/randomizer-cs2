package dev.luzifer.gui.component;

import dev.luzifer.model.event.Event;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EventSettingsComponent extends VBox {
    
    private final Label title = new Label();
    
    private final VBox vBox = new VBox();
    
    public EventSettingsComponent(Event event) {
        
        title.setText(event.name() + " Settings");
        title.setStyle("" +
                "-fx-font-weight: bold;" +
                " -fx-font-size: 12px;" +
                " -fx-padding: 10px 0 0 0;" +
                " -fx-border-color: black;" +
                " -fx-border-width: 0 0 1 0;" +
                " -fx-background-color: cyan;");
        
        // Build settings
        
        getChildren().addAll(title, vBox);
    }
    
}
