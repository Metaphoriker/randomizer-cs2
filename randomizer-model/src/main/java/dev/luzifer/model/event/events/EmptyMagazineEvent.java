package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.Interval;

import java.awt.event.InputEvent;
import java.util.concurrent.ThreadLocalRandom;

public class EmptyMagazineEvent extends Event implements Interval {
    
    @Override
    public void execute() {
        
        int key = InputEvent.BUTTON1_DOWN_MASK;
        
        robot.mousePress(key);
        robot.delay(ThreadLocalRandom.current().nextInt(min(), max()));
        robot.mouseRelease(key);
    }
    
    @Override
    public String description() {
        return "Holds left click for a few seconds to empty the magazine";
    }
    
    @Override
    public int min() {
        return 3000;
    }
    
    @Override
    public int max() {
        return 5000;
    }
}
