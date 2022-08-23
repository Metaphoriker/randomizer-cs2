package dev.luzifer.model.event;

import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.event.cluster.EventClusterRepository;

import java.util.concurrent.ThreadLocalRandom;

public class EventExecutor extends Thread {
    
    private final EventRepository eventRepository;
    private final EventClusterRepository eventClusterRepository;
    
    public EventExecutor(EventRepository eventRepository, EventClusterRepository eventClusterRepository) {
        this.eventRepository = eventRepository;
        this.eventClusterRepository = eventClusterRepository;
    }
    
    @Override
    public void run() {
        
        while (true) {
            
            int random = ThreadLocalRandom.current().nextInt(0, 100);
    
            if(random >= 25) {
                Event event = eventRepository.getEnabledEvents().get(ThreadLocalRandom.current().nextInt(0, eventRepository.getEnabledEvents().size() - 1));
                EventDispatcher.dispatch(event);
            } else {
                EventCluster eventCluster = eventClusterRepository.getClusters().get(ThreadLocalRandom.current().nextInt(0, eventClusterRepository.getClusters().size() - 1));
                for (Event event : eventCluster.getEvents())
                    EventDispatcher.dispatch(event); // TODO: don't dispatch disabled events or the entire cluster if contained
            }
    
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10*1000, 45*1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
