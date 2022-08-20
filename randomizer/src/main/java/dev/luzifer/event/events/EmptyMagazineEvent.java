package dev.luzifer.event.events;

import dev.luzifer.event.Event;

import java.awt.event.InputEvent;
import java.util.concurrent.ThreadLocalRandom;

public class EmptyMagazineEvent extends Event {
    
    @Override
    public void execute() {
        
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(ThreadLocalRandom.current().nextInt(3000, 5000));
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    @Override
    public String description() {
        return "Holds left click for a few seconds to empty the magazine";
    }
}
