package dev.luzifer.event.events;

import dev.luzifer.event.Event;

import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class ShiftEvent extends Event {
    
    @Override
    public void execute() {
    
        int key = KeyEvent.VK_SHIFT;
    
        robot.keyPress(key);
        robot.delay(ThreadLocalRandom.current().nextInt(1000, 5000));
        robot.keyRelease(key);
    }
    
    @Override
    public String description() {
        return "Presses SHIFT to shift";
    }
}
