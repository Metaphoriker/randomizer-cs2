package dev.luzifer.backend.event.events;

import dev.luzifer.backend.event.Event;

import java.awt.event.InputEvent;
import java.util.concurrent.ThreadLocalRandom;

public class EmptyMagazineEvent extends Event {
    
    @Override
    public void execute() {
        
        int key = InputEvent.BUTTON1_DOWN_MASK;
        
        robot.mousePress(key);
        robot.delay(ThreadLocalRandom.current().nextInt(3000, 5000));
        robot.mouseRelease(key);
    }
    
    @Override
    public String description() {
        return "Holds left click for a few seconds to empty the magazine";
    }
}
