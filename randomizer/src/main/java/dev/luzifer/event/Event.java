package dev.luzifer.event;

import java.awt.*;

public abstract class Event {
    
    private static final String EMPTY = "";
    
    protected static final Robot robot;
    
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String name() {
        return getClass().getSimpleName();
    }
    
    public String description() {
        return EMPTY;
    }
    
    public abstract void execute();
    
}
