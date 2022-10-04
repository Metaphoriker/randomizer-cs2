package dev.luzifer.model.event;

import dev.luzifer.model.ApplicationState;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.model.stuff.WhateverThisFuckerIs;

import java.util.concurrent.ThreadLocalRandom;

public class EventExecutorRunnable implements Runnable {
    
    private final EventClusterRepository eventClusterRepository;
    
    public EventExecutorRunnable(EventClusterRepository eventClusterRepository) {
        this.eventClusterRepository = eventClusterRepository;
    }
    
    @Override
    public void run() {
        
        while (true) {
            
            if(WhateverThisFuckerIs.getApplicationState() == ApplicationState.RUNNING && !eventClusterRepository.getClusters().isEmpty())
                EventDispatcher.dispatchCluster(eventClusterRepository.getClusters().get(ThreadLocalRandom.current().nextInt(0, eventClusterRepository.getClusters().size())));
            
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(20*1000, 60*1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
