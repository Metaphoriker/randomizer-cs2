package dev.luzifer.backend.event;

public record EventCluster(Event... events) {
    
    public Event[] getEvents() {
        return events;
    }
    
}
