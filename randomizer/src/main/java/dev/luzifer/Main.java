package dev.luzifer;

import dev.luzifer.event.EventDispatcher;
import dev.luzifer.event.EventExecutor;
import dev.luzifer.event.events.CrouchEvent;
import dev.luzifer.event.events.DropWeaponEvent;
import dev.luzifer.event.events.EmptyMagazineEvent;
import dev.luzifer.event.events.EscapeEvent;
import dev.luzifer.event.events.JumpEvent;
import dev.luzifer.event.events.MouseLeftClickEvent;
import dev.luzifer.event.events.MouseMoveEvent;
import dev.luzifer.event.events.MouseRightClickEvent;
import dev.luzifer.event.events.MoveEvent;
import dev.luzifer.event.events.ReloadEvent;
import dev.luzifer.event.events.ShiftEvent;
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
        
        registerEvents();
        
        EventExecutor eventExecutor = new EventExecutor();
        eventExecutor.setDaemon(true);
        eventExecutor.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOG_FILE));
        eventExecutor.start();
    }
    
    private static void registerEvents() {
    
        EventDispatcher.registerEvent(new MouseLeftClickEvent());
        EventDispatcher.registerEvent(new MouseRightClickEvent());
        EventDispatcher.registerEvent(new MouseMoveEvent());
        EventDispatcher.registerEvent(new MoveEvent());
        EventDispatcher.registerEvent(new CrouchEvent());
        EventDispatcher.registerEvent(new ShiftEvent());
        EventDispatcher.registerEvent(new JumpEvent());
        EventDispatcher.registerEvent(new ReloadEvent());
        EventDispatcher.registerEvent(new EscapeEvent());
        EventDispatcher.registerEvent(new DropWeaponEvent());
        EventDispatcher.registerEvent(new EmptyMagazineEvent());
    }
    
}
