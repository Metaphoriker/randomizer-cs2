package dev.luzifer.gui.view.models;

import dev.luzifer.gui.view.ViewModel;
import dev.luzifer.model.ApplicationState;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventRegistry;
import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.model.stuff.WhateverThisFuckerIs;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Set;

public class RandomizerViewModel implements ViewModel {
    
    private final StringProperty nextStateProperty = new SimpleStringProperty("Run");
    private final EventClusterRepository eventClusterRepository;
    
    public RandomizerViewModel(EventClusterRepository eventClusterRepository) {
        this.eventClusterRepository = eventClusterRepository;
    }
    
    public void reactToButtonWhatever() {
        if(WhateverThisFuckerIs.getApplicationState() == ApplicationState.IDLING) {
            WhateverThisFuckerIs.setApplicationState(ApplicationState.RUNNING);
            nextStateProperty.setValue("Idle");
        } else {
            WhateverThisFuckerIs.setApplicationState(ApplicationState.IDLING);
            nextStateProperty.setValue("Run");
        }
    }

    public EventCluster getCluster(String name) {
        return eventClusterRepository.getCluster(name);
    }

    public Set<Event> getEvents() {
        return EventRegistry.getEvents();
    }
    
    public List<EventCluster> getClusters() {
        return eventClusterRepository.loadClusters();
    }
    
    public StringProperty getNextStateProperty() {
        return nextStateProperty;
    }
    
}
