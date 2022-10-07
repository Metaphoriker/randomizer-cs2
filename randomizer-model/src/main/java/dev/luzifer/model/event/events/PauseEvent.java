package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;

public class PauseEvent extends Event {

    @Override
    public void execute() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String description() {
        return "A pause of 50ms";
    }
}
