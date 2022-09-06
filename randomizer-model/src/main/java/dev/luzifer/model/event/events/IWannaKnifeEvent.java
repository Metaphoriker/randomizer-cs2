package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;

import java.awt.event.KeyEvent;

public class IWannaKnifeEvent extends Event {
    
    @Override
    public void execute() {
        
        int key = KeyEvent.VK_3;
        
        robot.keyPress(key);
        robot.keyRelease(key);
    }
    
    @Override
    public String description() {
        return "go knife";
    }
}
