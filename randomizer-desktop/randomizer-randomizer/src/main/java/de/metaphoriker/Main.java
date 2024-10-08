package de.metaphoriker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import de.metaphoriker.gui.AppStarter;
import de.metaphoriker.model.event.EventExecutorRunnable;
import de.metaphoriker.model.event.EventRegistry;
import de.metaphoriker.model.event.cluster.EventClusterRepository;
import de.metaphoriker.model.event.events.CrouchEvent;
import de.metaphoriker.model.event.events.DropWeaponEvent;
import de.metaphoriker.model.event.events.EmptyMagazineEvent;
import de.metaphoriker.model.event.events.EscapeEvent;
import de.metaphoriker.model.event.events.IWannaKnifeEvent;
import de.metaphoriker.model.event.events.IWannaNadeEvent;
import de.metaphoriker.model.event.events.InteractEvent;
import de.metaphoriker.model.event.events.JumpEvent;
import de.metaphoriker.model.event.events.MouseLeftClickEvent;
import de.metaphoriker.model.event.events.MouseMoveEvent;
import de.metaphoriker.model.event.events.MouseRightClickEvent;
import de.metaphoriker.model.event.events.MoveEvent;
import de.metaphoriker.model.event.events.PauseEvent;
import de.metaphoriker.model.event.events.ReloadEvent;
import de.metaphoriker.model.event.events.ShiftEvent;
import de.metaphoriker.model.exception.UncaughtExceptionLogger;
import de.metaphoriker.model.messages.Messages;
import de.metaphoriker.model.notify.Speaker;
import de.metaphoriker.model.scheduler.Scheduler;
import de.metaphoriker.model.scheduler.SchedulerThread;
import de.metaphoriker.model.stuff.WhateverThisFuckerIs;
import de.metaphoriker.model.updater.Updater;
import de.metaphoriker.model.watcher.FileSystemWatcher;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;

public class Main {

  private static final EventClusterRepository EVENT_CLUSTER_REPOSITORY =
      new EventClusterRepository();
  private static final Scheduler SCHEDULER = new Scheduler();

  public static void main(String[] args) throws IOException, URISyntaxException {
    setupAppdataFolder();

    registerEvents();
    cacheCluster();

    startScheduler();
    startEventExecutor();
    startFileWatcher();

    Speaker.addListener(
        notification -> {
          if (notification.getNotifier() == FileSystemWatcher.class) cacheCluster();
        });

    Messages.cache();
    Thread.currentThread()
        .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

    registerNativeKeyHook();

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
    if (Updater.isUpdateAvailable(Updater.getCurrentVersion(), Updater.RANDOMIZER_VERSION_URL)) {
      Runtime.getRuntime()
          .exec("java -jar " + path + " -randomizerLocation=" + jarPath.getAbsolutePath());
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

    if (Updater.isUpdateAvailable(updaterJar, Updater.UPDATER_VERSION_URL))
      Updater.update(updaterJar, Updater.UPDATER_DOWNLOAD_URL);

    return updaterJar;
  }

  private static void startScheduler() {
    SchedulerThread schedulerThread = new SchedulerThread(SCHEDULER);
    schedulerThread.setUncaughtExceptionHandler(
        UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
    schedulerThread.setDaemon(true);
    schedulerThread.start();
  }

  private static void startEventExecutor() {
    Thread eventExecutor = new Thread(new EventExecutorRunnable(EVENT_CLUSTER_REPOSITORY));
    eventExecutor.setUncaughtExceptionHandler(
        UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
    eventExecutor.setDaemon(true);
    eventExecutor.start();
  }

  private static void startFileWatcher() {
    Thread fileWatcher = new Thread(new FileSystemWatcher());
    fileWatcher.setUncaughtExceptionHandler(
        UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
    fileWatcher.setDaemon(true);
    fileWatcher.start();
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
    EventRegistry.register(new PauseEvent());
    EventRegistry.register(new InteractEvent());
    EventRegistry.register(new IWannaNadeEvent());
  }

  private static void registerNativeKeyHook() {
    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      e.printStackTrace();
    }
  }
}
