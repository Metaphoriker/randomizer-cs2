package dev.luzifer.model.event.cluster;

import dev.luzifer.model.event.Event;

import java.util.Arrays;
import java.util.Objects;

public class EventCluster {
    
    private final String name;
    private final Event[] events;
    
    public EventCluster(String name, Event... events) {
        this.name = name;
        this.events = events;
    }
    
    public String getName() {
        return name;
    }
    
    public Event[] getEvents() {
        return events;
    }
    
    @Override
    public String toString() {
        return "EventCluster{" +
                "name='" + name + '\'' +
                ", events=" + Arrays.toString(events) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventCluster that = (EventCluster) o;
        return Objects.equals(name, that.name) && Arrays.equals(events, that.events);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(events);
        return result;
    }
}
