package dev.luzifer.ui.view.component.models;

import dev.luzifer.backend.event.Event;
import dev.luzifer.ui.view.component.ComponentModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EventComponentModel implements ComponentModel {
    
    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty descriptionProperty = new SimpleStringProperty();
    
    private Event eventWrapped;
    
    public void acceptEvent(Event event) {
        
        this.eventWrapped = event;
        
        nameProperty.set(event.name());
        descriptionProperty.set(event.description());
    }
    
    public boolean isEventAccepted() {
        return eventWrapped != null;
    }
    
    public Event getEventWrapped() {
        return eventWrapped;
    }
    
    public StringProperty getNameProperty() {
        return nameProperty;
    }
    
    public StringProperty getDescriptionProperty() {
        return descriptionProperty;
    }
}
