package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.Interval;

import java.awt.event.InputEvent;
import java.util.concurrent.ThreadLocalRandom;

public class EmptyMagazineEvent extends Event implements Interval {
    
    private int min = 3000;
    private int max = 5000;
    
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
        return min;
    }
    
    @Override
    public int max() {
        return max;
    }
    
    @Override
    public void setMin(int min) {
        this.min = min;
    }
    
    @Override
    public void setMax(int max) {
        this.max = max;
    }
}
