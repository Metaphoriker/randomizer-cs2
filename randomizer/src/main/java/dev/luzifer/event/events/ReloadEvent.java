package dev.luzifer.event.events;

import dev.luzifer.event.Event;

import java.awt.event.KeyEvent;

public class ReloadEvent extends Event {
    
    @Override
    public void execute() {
        
        int key = KeyEvent.VK_R;
        
        robot.keyPress(key);
        robot.keyRelease(key);
    }
    
    @Override
    public String description() {
        return "Presses R to reload, but doesn't really work yet";
    }
}
