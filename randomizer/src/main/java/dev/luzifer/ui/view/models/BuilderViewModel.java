package dev.luzifer.ui.view.models;

import dev.luzifer.backend.event.Event;
import dev.luzifer.backend.event.EventRepository;
import dev.luzifer.backend.event.cluster.EventCluster;
import dev.luzifer.backend.event.cluster.EventClusterRepository;
import dev.luzifer.ui.view.ViewModel;

import java.util.List;

public class BuilderViewModel implements ViewModel {
    
    private final EventRepository eventRepository;
    private final EventClusterRepository eventClusterRepository;
    
    public BuilderViewModel(EventRepository eventRepository, EventClusterRepository eventClusterRepository) {
        this.eventRepository = eventRepository;
        this.eventClusterRepository = eventClusterRepository;
    }
    
    public List<Event> getEvents() {
        return eventRepository.getRegisteredEvents();
    }
    
    public List<EventCluster> getEventClusters() {
        return eventClusterRepository.getClusters();
    }
    
}
