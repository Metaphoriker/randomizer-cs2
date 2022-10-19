package dev.luzifer.model.event;

public interface Interval {
    
    int min();
    
    int max();
    
    /*
     * TODO: Actually those 2 methods break the concept of an Value Object.
     *  But anyways, since a new instance of the event should be created, it is not a problem yet.
     *
     * TODO: Maybe it would be better to create a new abstract class which represents an IntervalEvent
     */
    
    default void setMin(int min) {
        throw new UnsupportedOperationException();
    }
    
    default void setMax(int max) {
        throw new UnsupportedOperationException();
    }
    
}
