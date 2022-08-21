package dev.luzifer.backend.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepository {

    private final Map<Event, Boolean> eventRegistry = new HashMap<>();
    
    public void registerEvent(Event event) {
        eventRegistry.put(event, true);
    }
    
    public void enableEvent(Event event) {
        eventRegistry.put(event, true);
    }
    
    public void disableEvent(Event event) {
        eventRegistry.put(event, false);
    }
    
    public boolean isRegistered(Event event) {
        return eventRegistry.containsKey(event);
    }
    
    public boolean isEnabled(Event event) {
        return eventRegistry.getOrDefault(event, false);
    }
    
    public List<Event> getRegisteredEvents() {
        return sortedEvents();
    }
    
    // The following methods could be way more performant, but it's negligible for now
    
    public Event getByName(String name) {
        return eventRegistry.keySet().stream().filter(e -> e.name().equals(name)).findFirst().orElse(null);
    }
    
    public List<Event> getEnabledEvents() {
        return sortedEvents().stream().filter(this::isEnabled).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public List<Event> getDisabledEvents() {
        return sortedEvents().stream().filter(e -> !isEnabled(e)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    private List<Event> sortedEvents() {
        
        List<Event> list = new ArrayList<>(eventRegistry.keySet());
        list.sort(Comparator.comparing(o -> o.getClass().getSimpleName().length()));
        
        return list;
    }

}
