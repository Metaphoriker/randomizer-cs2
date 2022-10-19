package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.Interval;

import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class ShiftEvent extends Event implements Interval {
    
    @Override
    public void execute() {
    
        int key = KeyEvent.VK_SHIFT;
    
        robot.keyPress(key);
        robot.delay(ThreadLocalRandom.current().nextInt(min(), max()));
        robot.keyRelease(key);
    }
    
    @Override
    public String description() {
        return "Presses SHIFT to shift";
    }
    
    @Override
    public int min() {
        return 1000;
    }
    
    @Override
    public int max() {
        return 5000;
    }
}
