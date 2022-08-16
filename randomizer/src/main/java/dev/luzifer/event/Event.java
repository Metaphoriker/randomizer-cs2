package dev.luzifer.event;

import java.awt.*;

public abstract class Event {
    
    protected static final Robot robot;
    
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String description() {
        return "NO DESCRIPTION";
    }
    
    public abstract void execute();
    
}
