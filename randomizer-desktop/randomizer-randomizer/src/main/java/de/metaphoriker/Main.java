package de.metaphoriker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import de.metaphoriker.gui.AppStarter;
import de.metaphoriker.model.cfg.ConfigLoader;
import de.metaphoriker.model.cfg.keybind.KeyBind;
import de.metaphoriker.model.cfg.keybind.KeyBindRepository;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.custom.PauseAction;
import de.metaphoriker.model.action.handling.ActionExecutorRunnable;
import de.metaphoriker.model.action.handling.ActionRegistry;
import de.metaphoriker.model.action.custom.MouseMoveAction;
import de.metaphoriker.model.action.sequence.ActionSequenceRepository;
import de.metaphoriker.model.exception.UncaughtExceptionLogger;
import de.metaphoriker.model.messages.Messages;
import de.metaphoriker.model.notify.Speaker;
import de.metaphoriker.model.stuff.ApplicationContext;
import de.metaphoriker.model.updater.Updater;
import de.metaphoriker.model.watcher.FileSystemWatcher;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;

public class Main {

  private static final ActionSequenceRepository EVENT_CLUSTER_REPOSITORY =
      new ActionSequenceRepository();
  private static final KeyBindRepository KEY_BIND_REPOSITORY = new KeyBindRepository();
  private static final ActionRegistry EVENT_REGISTRY = new ActionRegistry();

  public static void main(String[] args) throws IOException, URISyntaxException {
    setupAppdataFolder();

    ConfigLoader.loadKeyBinds(KEY_BIND_REPOSITORY);

    registerEvents();
    cacheCluster();

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

  public static ActionRegistry getEventRegistry() {
    return EVENT_REGISTRY;
  }

  public static ActionSequenceRepository getEventClusterRepository() {
    return EVENT_CLUSTER_REPOSITORY;
  }

  private static void cacheCluster() {
    EVENT_CLUSTER_REPOSITORY.loadActionSequences();
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
    File updaterJar = new File(ApplicationContext.getAppdataFolder(), "randomizer-updater.jar");
    try {
      updaterJar.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (Updater.isUpdateAvailable(updaterJar, Updater.UPDATER_VERSION_URL))
      Updater.update(updaterJar, Updater.UPDATER_DOWNLOAD_URL);

    return updaterJar;
  }

  private static void startEventExecutor() {
    Thread eventExecutor = new Thread(new ActionExecutorRunnable(EVENT_CLUSTER_REPOSITORY));
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
    EVENT_REGISTRY.register(new MouseMoveAction(KeyBind.EMPTY_KEYBIND));
    EVENT_REGISTRY.register(new PauseAction(KeyBind.EMPTY_KEYBIND));
    KEY_BIND_REPOSITORY
        .getKeyBinds()
        .forEach(
            keyBind -> {
              Action action = new Action(keyBind);
              EVENT_REGISTRY.register(action);
            });
  }

  private static void registerNativeKeyHook() {
    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      e.printStackTrace();
    }
  }
}
