package dev.luzifer.backend.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Scheduler {
    
    private final List<Task> tasks = new ArrayList<>();
    
    public void schedule(Runnable task) {
        synchronized (tasks) {
            tasks.add(new Task(task, new Date()));
        }
    }
    
    public void schedule(Runnable task, long delay) {
        synchronized (tasks) {
            tasks.add(new Task(task, new Date(System.currentTimeMillis() + delay)));
        }
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    @Override
    public String toString() {
        return "Scheduler{" +
                "tasks=" + tasks +
                '}';
    }
    
    static class Task {
        
        private final Runnable runnable;
        private final Date executionDate;
        
        public Task(Runnable runnable, Date executionDate) {
            this.runnable = runnable;
            this.executionDate = executionDate;
        }
        
        public boolean runIfAllowed() {
            if (executionDate.getTime() <= System.currentTimeMillis()) {
                runnable.run();
                return true;
            }
            return false;
        }
    }
}
