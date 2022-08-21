package dev.luzifer.backend.event.events;

import dev.luzifer.backend.event.Event;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

// always looks down, fix. maybe use angle near the middle of the screen?
public class MouseMoveEvent extends Event {
    
    @Override
    public void execute() {
        
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        
        int middleX = dimension.width / 2;
        int middleY = dimension.height / 2;
        
        int x = ThreadLocalRandom.current().nextInt(middleX-5, middleX+5);
        int y = ThreadLocalRandom.current().nextInt(middleY-5, middleY+5);
        
        robot.mouseMove(x, y);
    }
    
    @Override
    public String description() {
        return "Moves mouse randomly around the middle of the screen";
    }
}
