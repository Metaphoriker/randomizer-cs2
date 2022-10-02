package dev.luzifer;

import dev.luzifer.model.event.EventExecutorRunnable;
import dev.luzifer.model.event.EventRegistry;
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
import dev.luzifer.model.stuff.WhateverThisFuckerIs;
import dev.luzifer.model.messages.Messages;
import dev.luzifer.model.scheduler.Scheduler;
import dev.luzifer.model.scheduler.SchedulerThread;
import dev.luzifer.gui.AppStarter;
import dev.luzifer.model.updater.Updater;
import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    private static final EventClusterRepository EVENT_CLUSTER_REPOSITORY = new EventClusterRepository();
    private static final Scheduler SCHEDULER = new Scheduler();
    
    public static void main(String[] args) throws IOException, URISyntaxException {
        
        setupAppdataFolder();
        
        registerEvents();
        cacheCluster();

        startScheduler();
        startEventExecutor();

        Messages.cache();
        Thread.currentThread().setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

        Application.launch(AppStarter.class);
    }
    
    public static Scheduler getScheduler() {
        return SCHEDULER;
    }
    
    public static EventClusterRepository getEventClusterRepository() {
        return EVENT_CLUSTER_REPOSITORY;
    }
    
    private static void cacheCluster() {
        EVENT_CLUSTER_REPOSITORY.loadClusters();
    }
    
    private static void startUpdaterIfNecessary(String path) throws IOException, URISyntaxException {
    
        File jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if(Updater.isUpdateAvailable(Updater.getCurrentVersion(), Updater.RANDOMIZER_VERSION_URL)) {
            Runtime.getRuntime().exec("java -jar " + path + " -randomizerLocation=" + jarPath.getAbsolutePath());
            System.exit(0);
        }
    }
    
    private static void setupAppdataFolder() throws IOException, URISyntaxException {
        File updater = installUpdater();
        startUpdaterIfNecessary(updater.getAbsolutePath());
    }
    
    private static File installUpdater() {
        
        File updaterJar = new File(WhateverThisFuckerIs.getAppdataFolder(), "randomizer-updater.jar");
        try {
            updaterJar.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        if(Updater.isUpdateAvailable(updaterJar, Updater.UPDATER_VERSION_URL))
            Updater.update(updaterJar, Updater.UPDATER_DOWNLOAD_URL);
        
        return updaterJar;
    }

    private static void startScheduler() {
    
        SchedulerThread schedulerThread = new SchedulerThread(SCHEDULER);
        schedulerThread.setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }
    
    private static void startEventExecutor() {
    
        Thread eventExecutor = new Thread(new EventExecutorRunnable(EVENT_CLUSTER_REPOSITORY));
        eventExecutor.setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
        eventExecutor.setDaemon(true);
        eventExecutor.start();
    }
    
    private static void registerEvents() {
    
        EventRegistry.register(new MouseLeftClickEvent());
        EventRegistry.register(new MouseRightClickEvent());
        EventRegistry.register(new MouseMoveEvent());
        EventRegistry.register(new MoveEvent());
        EventRegistry.register(new CrouchEvent());
        EventRegistry.register(new ShiftEvent());
        EventRegistry.register(new JumpEvent());
        EventRegistry.register(new ReloadEvent());
        EventRegistry.register(new EscapeEvent());
        EventRegistry.register(new DropWeaponEvent());
        EventRegistry.register(new EmptyMagazineEvent());
        EventRegistry.register(new IWannaKnifeEvent());
    }
    
}
