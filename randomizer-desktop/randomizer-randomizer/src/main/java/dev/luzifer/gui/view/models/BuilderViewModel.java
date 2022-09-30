package dev.luzifer.gui.view.models;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventRegistry;
import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.gui.view.ViewModel;
import dev.luzifer.model.json.JsonUtil;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BuilderViewModel implements ViewModel {
    
    private final EventClusterRepository eventClusterRepository;
    
    public BuilderViewModel(EventClusterRepository eventClusterRepository) {
        this.eventClusterRepository = eventClusterRepository;
    }
    
    public void saveCluster(String name, String content) {
        eventClusterRepository.saveCluster(EventCluster.formatEventCluster(name, content));
    }
    
    public String serialize(Event event) {
        return JsonUtil.serialize(event);
    }
    
    public Event deserialize(String json) {
        return JsonUtil.deserialize(json);
    }
    
    public Event getEvent(String name) {
        return EventRegistry.getByName(name);
    }
    
    public Event getRandomEvent() {
        
        Set<Event> events = EventRegistry.getEvents();
        int index = ThreadLocalRandom.current().nextInt(0, events.size());
        
        return (Event) events.toArray()[index];
    }
    
    public Set<Event> getEvents() {
        return EventRegistry.getEvents();
    }

    public List<EventCluster> loadEventClusters() {
        return eventClusterRepository.loadClusters();
    }

    public List<EventCluster> getClusters() {
        return eventClusterRepository.getClusters();
    }
    
}
