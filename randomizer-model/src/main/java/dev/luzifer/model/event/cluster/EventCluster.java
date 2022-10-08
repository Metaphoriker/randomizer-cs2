package dev.luzifer.model.event.cluster;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EventCluster {

    public static EventCluster formatEventCluster(String name, String content) {

        String[] events = content.split(";");
        List<Event> eventSet = new ArrayList<>();

        for(String event : events)
            eventSet.add(EventRegistry.getByName(event));

        return new EventCluster(name, eventSet);
    }

    private final String name;
    private final List<Event> events;
    
    public EventCluster(String name, List<Event> events) {
        this.name = name;
        this.events = events;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }
    
    @Override
    public String toString() {
        return "EventCluster{" +
                "name='" + name + '\'' +
                ", events=" + events +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventCluster that = (EventCluster) o;
        return Objects.equals(name, that.name) && events.equals(that.events);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + events.hashCode();
        return result;
    }
}
