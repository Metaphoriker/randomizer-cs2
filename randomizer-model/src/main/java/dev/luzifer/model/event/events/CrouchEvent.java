package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;

import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class CrouchEvent extends Event {
    
    @Override
    public void execute() {
    
        int key = KeyEvent.VK_CONTROL;
    
        robot.keyPress(key);
        robot.delay(ThreadLocalRandom.current().nextInt(1000, 5000));
        robot.keyRelease(key);
    }
    
    @Override
    public String description() {
        return "Presses CTRL to crouch";
    }
}
