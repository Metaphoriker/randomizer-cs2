package dev.luzifer.model.scheduler;

import lombok.Value;

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
    
    List<Task> getTasks() {
        return tasks;
    }
    
    @Override
    public String toString() {
        return "Scheduler{" +
                "tasks=" + tasks +
                '}';
    }
    
    @Value
    static class Task {
        
        Runnable runnable;
        Date executionDate;
        
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
