package dev.luzifer.backend.event;

public class EventExecutor extends Thread {
    
    @Override
    public void run() {
        
        while (true) {
            
            // TODO: Implement event execution
            /*
                int random = ThreadLocalRandom.current().nextInt(0, 100);
                if(random >= 25) {
                    Event event = EventDispatcher.getRegisteredEvents().get(ThreadLocalRandom.current().nextInt(0, EventDispatcher.getRegisteredEvents().size()));
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
             */
        }
    }
}
