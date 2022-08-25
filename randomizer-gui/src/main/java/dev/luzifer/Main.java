package dev.luzifer;

import dev.luzifer.model.event.EventExecutor;
import dev.luzifer.model.event.EventRepository;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.model.event.events.CrouchEvent;
import dev.luzifer.model.event.events.DropWeaponEvent;
import dev.luzifer.model.event.events.EmptyMagazineEvent;
import dev.luzifer.model.event.events.EscapeEvent;
import dev.luzifer.model.event.events.IWannaKnifeEvent;
import dev.luzifer.model.event.events.JumpEvent;
import dev.luzifer.model.event.events.MouseLeftClickEvent;
import dev.luzifer.model.event.events.MouseMoveEvent;
import dev.luzifer.model.event.events.MouseRightClickEvent;
import dev.luzifer.model.event.events.MoveEvent;
import dev.luzifer.model.event.events.ReloadEvent;
import dev.luzifer.model.event.events.ShiftEvent;
import dev.luzifer.model.exception.UncaughtExceptionLogger;
import dev.luzifer.model.messages.Messages;
import dev.luzifer.model.scheduler.Scheduler;
import dev.luzifer.model.scheduler.SchedulerThread;
import dev.luzifer.gui.AppStarter;
import javafx.application.Application;

import java.io.File;

public class Main {
    
    private static final File LOG_FILE = new File("log.txt");
    private static final UncaughtExceptionLogger DEFAULT_UNCAUGHT_EXCEPTION_LOGGER = new UncaughtExceptionLogger(LOG_FILE);

    private static final EventRepository EVENT_REPOSITORY = new EventRepository();
    private static final EventClusterRepository EVENT_CLUSTER_REPOSITORY = new EventClusterRepository();

    private static final Scheduler SCHEDULER = new Scheduler();
    
    public static void main(String[] args) {

        registerEvents();

        setupScheduler();
        // setupEventExecutor();

        Messages.cache();
        Thread.currentThread().setUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

        Application.launch(AppStarter.class);
    }

    // I don't like those singletons

    public static Scheduler getScheduler() {
        return SCHEDULER;
    }
    
    public static EventRepository getEventRepository() {
        return EVENT_REPOSITORY;
    }
    
    public static EventClusterRepository getEventClusterRepository() {
        return EVENT_CLUSTER_REPOSITORY;
    }
    
    private static void setupScheduler() {
    
        SchedulerThread schedulerThread = new SchedulerThread(SCHEDULER);
        schedulerThread.setUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }
    
    private static void setupEventExecutor() {
        
        EventExecutor eventExecutor = new EventExecutor(EVENT_REPOSITORY, EVENT_CLUSTER_REPOSITORY);
        eventExecutor.setUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
        eventExecutor.setDaemon(true);
        eventExecutor.start();
    }
    
    private static void registerEvents() {
    
        EVENT_REPOSITORY.registerEvent(new MouseLeftClickEvent());
        EVENT_REPOSITORY.registerEvent(new MouseRightClickEvent());
        EVENT_REPOSITORY.registerEvent(new MouseMoveEvent());
        EVENT_REPOSITORY.registerEvent(new MoveEvent());
        EVENT_REPOSITORY.registerEvent(new CrouchEvent());
        EVENT_REPOSITORY.registerEvent(new ShiftEvent());
        EVENT_REPOSITORY.registerEvent(new JumpEvent());
        EVENT_REPOSITORY.registerEvent(new ReloadEvent());
        EVENT_REPOSITORY.registerEvent(new EscapeEvent());
        EVENT_REPOSITORY.registerEvent(new DropWeaponEvent());
        EVENT_REPOSITORY.registerEvent(new EmptyMagazineEvent());
        EVENT_REPOSITORY.registerEvent(new IWannaKnifeEvent());
    }
    
}
