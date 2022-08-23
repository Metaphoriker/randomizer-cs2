package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;

import java.awt.event.KeyEvent;

public class DropWeaponEvent extends Event {
    
    @Override
    public void execute() {
        
        int key = KeyEvent.VK_G;
    
        robot.keyPress(key);
        robot.keyRelease(key);
    }
    
    @Override
    public String description() {
        return "Presses G to drop weapon";
    }
}
