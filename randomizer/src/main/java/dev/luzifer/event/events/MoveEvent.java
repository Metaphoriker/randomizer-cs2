package dev.luzifer.event.events;

import dev.luzifer.event.Event;

import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class MoveEvent extends Event {
    
    @Override
    public void execute() {
    
        int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D};
        int key = keys[ThreadLocalRandom.current().nextInt(0, keys.length)];
    
        int amount = ThreadLocalRandom.current().nextInt(1, 5);
        int index = 0;
    
        while(index++ < amount) {
            
            robot.keyPress(key);
            robot.delay(ThreadLocalRandom.current().nextInt(40, 120));
            robot.keyRelease(key);
        }
    }
    
    @Override
    public String description() {
        return "Presses either W-A-S-D 1~5 times to move";
    }
}
