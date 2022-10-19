package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.Interval;

public class PauseEvent extends Event implements Interval {

    @Override
    public void execute() {
        try {
            Thread.sleep(min());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String description() {
        return "A pause of 250ms";
    }
    
    @Override
    public int min() {
        return 250;
    }
    
    @Override
    public int max() {
        return 250;
    }
}
