package dev.luzifer.gui.component;

import dev.luzifer.model.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class EventComponent extends VBox {
    
    private final HBox innerHBox = new HBox();
    private final Label nameLabel = new Label();
    private final Button button = new Button("Settings");
    
    private final Pane pane = new Pane();
    
    private final Event represent;
    
    public EventComponent(Event event) {
        
        this.represent = event;
        
        innerHBox.setSpacing(75);
        
        nameLabel.setText(event.name());
    
        button.setFont(new Font("Arial", 8));
        button.setOnAction(click -> {
            
            pane.setVisible(!pane.isVisible());
            
            if(pane.isVisible())
                getChildren().add(pane);
            else
                getChildren().remove(pane);
        });
    
        pane.getChildren().add(new Label(event.description()));
        pane.setVisible(false);
    
        Region placeHolder = new Region();
        HBox.setHgrow(placeHolder, Priority.ALWAYS);
        
        innerHBox.getChildren().addAll(nameLabel, placeHolder, button);
        getChildren().addAll(innerHBox);
    }
    
    public Event getRepresent() {
        return represent;
    }
}
