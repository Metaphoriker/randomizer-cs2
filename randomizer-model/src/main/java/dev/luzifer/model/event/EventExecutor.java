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
            
            EventDispatcher.dispatchCluster(eventClusterRepository.getClusters().get(ThreadLocalRandom.current().nextInt(0, eventClusterRepository.getClusters().size())));
            
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10*1000, 11*1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
