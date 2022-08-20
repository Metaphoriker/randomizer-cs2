package dev.luzifer.scheduler;

public class SchedulerThread extends Thread {
    
    private final Scheduler scheduler;
    
    public SchedulerThread(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    @Override
    public void run() {
        
        while (!isInterrupted()) {
    
            scheduler.getTasks().removeIf(Scheduler.Task::runIfAllowed);
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
