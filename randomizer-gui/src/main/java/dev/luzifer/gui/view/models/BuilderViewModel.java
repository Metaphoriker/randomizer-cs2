package dev.luzifer.gui.view.models;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventRepository;
import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.gui.view.ViewModel;

import java.util.List;

public class BuilderViewModel implements ViewModel {
    
    private final EventRepository eventRepository;
    private final EventClusterRepository eventClusterRepository;
    
    public BuilderViewModel(EventRepository eventRepository, EventClusterRepository eventClusterRepository) {
        this.eventRepository = eventRepository;
        this.eventClusterRepository = eventClusterRepository;
    }
    
    public Event getEvent(String name) {
        return eventRepository.getByName(name);
    }
    
    public boolean isEventEnabled(Event event) {
        return eventRepository.isEnabled(event);
    }
    
    public List<Event> getEvents() {
        return eventRepository.getRegisteredEvents();
    }
    
    public List<EventCluster> getEventClusters() {
        return eventClusterRepository.getClusters();
    }
    
}
