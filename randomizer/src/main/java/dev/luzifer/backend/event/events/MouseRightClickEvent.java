package dev.luzifer.backend.event.events;

import dev.luzifer.backend.event.Event;

import java.awt.event.InputEvent;

public class MouseRightClickEvent extends Event {
    
    @Override
    public void execute() {
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }
    
    @Override
    public String description() {
        return "Presses right mouse button";
    }
}
