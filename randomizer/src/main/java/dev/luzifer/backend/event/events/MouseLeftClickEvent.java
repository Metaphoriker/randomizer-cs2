package dev.luzifer.backend.event.events;

import dev.luzifer.backend.event.Event;

import java.awt.event.InputEvent;

public class MouseLeftClickEvent extends Event {
    
    @Override
    public void execute() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    @Override
    public String description() {
        return "Presses left mouse button";
    }
}
