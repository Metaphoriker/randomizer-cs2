package dev.luzifer.event;

public record EventCluster(Event... events) {
    
    public Event[] getEvents() {
        return events;
    }
    
}
