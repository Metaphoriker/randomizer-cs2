package dev.luzifer.event;

import dev.luzifer.ui.AppStarter;
import dev.luzifer.ui.ApplicationState;

import java.util.concurrent.ThreadLocalRandom;

public class EventExecutor extends Thread {
    
    @Override
    public void run() {
        
        while (true) {
        
            if(AppStarter.getState() == ApplicationState.RUNNING) {
            
                int random = ThreadLocalRandom.current().nextInt(0, 100);
                if(random >= 25) {
                    Event event = EventDispatcher.getEvents()[ThreadLocalRandom.current().nextInt(0, EventDispatcher.getEvents().length)];
                    EventDispatcher.dispatch(event);
                } else {
                    EventCluster eventCluster = EventDispatcher.getEventClusters()[ThreadLocalRandom.current().nextInt(0, EventDispatcher.getEventClusters().length)];
                    for (Event event : eventCluster.getEvents())
                        EventDispatcher.dispatch(event);
                }
                
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10*1000, 45*1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
