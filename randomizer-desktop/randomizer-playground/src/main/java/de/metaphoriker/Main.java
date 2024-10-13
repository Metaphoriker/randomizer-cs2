package de.metaphoriker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.metaphoriker.model.ModelModule;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.custom.MouseMoveAction;
import de.metaphoriker.model.action.custom.PauseAction;
import de.metaphoriker.model.action.handling.ActionExecutorRunnable;
import de.metaphoriker.model.action.handling.ActionRegistry;
import de.metaphoriker.model.action.sequence.ActionSequenceRepository;
import de.metaphoriker.model.cfg.ConfigLoader;
import de.metaphoriker.model.cfg.keybind.KeyBind;
import de.metaphoriker.model.cfg.keybind.KeyBindRepository;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  @Getter private final ActionSequenceRepository eventClusterRepository;
  @Getter private final ActionRegistry eventRegistry;

  private final KeyBindRepository keyBindRepository;
  private final ActionExecutorRunnable actionExecutorRunnable;

  @Inject
  public Main(
      ActionSequenceRepository eventClusterRepository,
      KeyBindRepository keyBindRepository,
      ActionRegistry eventRegistry,
      ActionExecutorRunnable actionExecutorRunnable) {
    this.eventClusterRepository = eventClusterRepository;
    this.keyBindRepository = keyBindRepository;
    this.eventRegistry = eventRegistry;
    this.actionExecutorRunnable = actionExecutorRunnable;
  }

  public static void main(String[] args) throws IOException, URISyntaxException {
    Injector injector = Guice.createInjector(new ModelModule());
    Main mainInstance = injector.getInstance(Main.class);
    mainInstance.startApplication(args);
  }

  public void startApplication(String[] args) throws IOException, URISyntaxException {
    setupAppdataFolder();
    loadKeyBinds();
    registerEvents();
    cacheCluster();
    startExecutors();
    setupListeners();
    Messages.cache();
    setupGlobalExceptionHandler();
    registerNativeKeyHook();
    Application.launch(RandomizerStarter.class);
  }

  private void loadKeyBinds() {
    ConfigLoader.loadKeyBinds(keyBindRepository);
  }

  private void setupListeners() {
    Speaker.addListener(
        notification -> {
          if (notification.getNotifier() == FileSystemWatcher.class) cacheCluster();
        });
  }

  private void setupGlobalExceptionHandler() {
    Thread.currentThread()
        .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
  }

  private void cacheCluster() {
    eventClusterRepository.loadActionSequences();
  }

  private void startExecutors() {
    startThread(new Thread(actionExecutorRunnable));
    startThread(new Thread(new FileSystemWatcher()));
  }

  private void startThread(Thread thread) {
    thread.setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
    thread.setDaemon(true);
    thread.start();
  }

  private void setupAppdataFolder() throws IOException, URISyntaxException {
    File updater = installUpdater();
    startUpdaterIfNecessary(updater.getAbsolutePath());
  }

  private File installUpdater() {
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

  private void startUpdaterIfNecessary(String path) throws IOException, URISyntaxException {
    File jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    if (Updater.isUpdateAvailable(Updater.getCurrentVersion(), Updater.RANDOMIZER_VERSION_URL)) {
      Runtime.getRuntime()
          .exec("java -jar " + path + " -randomizerLocation=" + jarPath.getAbsolutePath());
      System.exit(0);
    }
  }

  private void registerEvents() {
    eventRegistry.register(new MouseMoveAction(KeyBind.EMPTY_KEYBIND));
    eventRegistry.register(new PauseAction(KeyBind.EMPTY_KEYBIND));
    keyBindRepository
        .getKeyBinds()
        .forEach(
            keyBind -> {
              Action action = new Action(keyBind);
              eventRegistry.register(action);
            });
  }

  private void registerNativeKeyHook() {
    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      log.error("Error while registering native hook", e);
    }
  }
}
