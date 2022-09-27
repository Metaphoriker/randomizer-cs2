package dev.luzifer.model.event;

import dev.luzifer.model.event.cluster.EventClusterRepository;

import java.util.concurrent.ThreadLocalRandom;

public class EventExecutor extends Thread {
    
    private final EventClusterRepository eventClusterRepository;
    
    public EventExecutor(EventClusterRepository eventClusterRepository) {
        this.eventClusterRepository = eventClusterRepository;
    }
    
    @Override
    public void run() {
        
        while (!isInterrupted()) { // TODO: check for application state
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10*1000, 45*1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
