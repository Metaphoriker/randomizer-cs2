package de.metaphoriker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.google.inject.Inject;
import com.google.inject.Injector;
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

  public static Injector injector;

  @Getter private final ActionSequenceRepository actionSequenceRepository;
  @Getter private final ActionRegistry actionRegistry;

  private final KeyBindRepository keyBindRepository;
  private final ActionExecutorRunnable actionExecutorRunnable;

  @Inject
  public Main(
      ActionSequenceRepository actionSequenceRepository,
      KeyBindRepository keyBindRepository,
      ActionRegistry actionRegistry,
      ActionExecutorRunnable actionExecutorRunnable) {
    this.actionSequenceRepository = actionSequenceRepository;
    this.keyBindRepository = keyBindRepository;
    this.actionRegistry = actionRegistry;
    this.actionExecutorRunnable = actionExecutorRunnable;
  }

  public static void main(String[] args) {
    log.debug("Starte JavaFX Anwendung...");
    Application.launch(RandomizerStarter.class, args);
  }

  public void startApplication() throws IOException, URISyntaxException {
    log.debug("Initialisiere Applikation...");
    setupAppdataFolder();
    loadKeyBinds();
    registerEvents();
    cacheCluster();
    startExecutors();
    setupListeners();
    Messages.cache();
    setupGlobalExceptionHandler();
    registerNativeKeyHook();
  }

  private void loadKeyBinds() {
    log.debug("Lade KeyBinds...");
    ConfigLoader.loadKeyBinds(keyBindRepository);
  }

  private void setupListeners() {
    log.debug("Richte Listener ein...");
    Speaker.addListener(
        notification -> {
          if (notification.getNotifier() == FileSystemWatcher.class) cacheCluster();
        });
  }

  private void setupGlobalExceptionHandler() {
    log.debug("Richte Global Exception Handler ein...");
    Thread.currentThread()
        .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
  }

  private void cacheCluster() {
    log.debug("Cache Cluster...");
    actionSequenceRepository.loadActionSequences();
  }

  private void startExecutors() {
    log.debug("Starte Executor...");
    startThread(new Thread(actionExecutorRunnable));
    startThread(new Thread(new FileSystemWatcher()));
  }

  private void startThread(Thread thread) {
    log.debug("Starte Thread: {}", thread.getName());
    thread.setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
    thread.setDaemon(true);
    thread.start();
  }

  private void setupAppdataFolder() throws IOException, URISyntaxException {
    log.debug("Richte Appdata Verzeichnis ein...");
    File updater = installUpdater();
    startUpdaterIfNecessary(updater.getAbsolutePath());
  }

  private File installUpdater() {
    log.debug("Installiere Updater...");
    File updaterJar = new File(ApplicationContext.getAppdataFolder(), "randomizer-updater.jar");
    try {
      updaterJar.createNewFile();
    } catch (IOException e) {
      log.error("Fehler beim Erstellen der Updater Datei", e);
      throw new RuntimeException(e);
    }

    if (Updater.isUpdateAvailable(updaterJar, Updater.UPDATER_VERSION_URL)) {
      Updater.update(updaterJar, Updater.UPDATER_DOWNLOAD_URL);
    }

    return updaterJar;
  }

  private void startUpdaterIfNecessary(String path) throws IOException, URISyntaxException {
    log.debug("Starte Updater falls notwendig...");
    File jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    if (Updater.isUpdateAvailable(Updater.getCurrentVersion(), Updater.RANDOMIZER_VERSION_URL)) {
      log.debug("Updater gestartet");
      Runtime.getRuntime()
          .exec("java -jar " + path + " -randomizerLocation=" + jarPath.getAbsolutePath());
      System.exit(0);
    }
  }

  private void registerEvents() {
    log.debug("Registriere Aktionen...");
    actionRegistry.register(new MouseMoveAction(KeyBind.EMPTY_KEYBIND));
    actionRegistry.register(new PauseAction(KeyBind.EMPTY_KEYBIND));
    keyBindRepository
        .getKeyBinds()
        .forEach(
            keyBind -> {
              Action action = new Action(keyBind);
              actionRegistry.register(action);
            });
  }

  private void registerNativeKeyHook() {
    try {
      log.debug("Registriere Native Key Hook...");
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      log.error("Fehler bei der Registrierung des Native Hooks", e);
    }
  }
}
