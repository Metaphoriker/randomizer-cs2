package dev.luzifer;

import dev.luzifer.event.EventExecutor;
import dev.luzifer.exception.UncaughtExceptionLogger;
import dev.luzifer.scheduler.Scheduler;
import dev.luzifer.scheduler.SchedulerThread;
import dev.luzifer.ui.AppStarter;
import javafx.application.Application;

import java.io.File;

public class Main {
    
    private static final File LOG_FILE = new File("log.txt");
    private static Scheduler scheduler;
    
    public static void main(String[] args) {
        
        setupScheduler();
        setupEventExecutor();
        Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOG_FILE));
        Application.launch(AppStarter.class);
    }
    
    public static Scheduler getScheduler() {
        return scheduler;
    }
    
    private static void setupScheduler() {
    
        scheduler = new Scheduler();
        
        SchedulerThread schedulerThread = new SchedulerThread(scheduler);
        schedulerThread.setDaemon(true);
        schedulerThread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOG_FILE));
        schedulerThread.start();
    }
    
    private static void setupEventExecutor() {
        
        EventExecutor eventExecutor = new EventExecutor();
        eventExecutor.setDaemon(true);
        eventExecutor.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOG_FILE));
        eventExecutor.start();
    }
    
}
