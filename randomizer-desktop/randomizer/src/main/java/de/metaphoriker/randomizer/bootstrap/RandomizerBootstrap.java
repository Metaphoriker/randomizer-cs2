package de.metaphoriker.randomizer.bootstrap;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationContext;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.ActionKey;
import de.metaphoriker.model.action.impl.BaseAction;
import de.metaphoriker.model.action.impl.MouseMoveAction;
import de.metaphoriker.model.action.impl.PauseAction;
import de.metaphoriker.model.action.repository.ActionRepository;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;
import de.metaphoriker.model.action.sequence.ActionSequenceExecutorRunnable;
import de.metaphoriker.model.config.ConfigLoader;
import de.metaphoriker.model.config.keybind.KeyBindRepository;
import de.metaphoriker.model.exception.UncaughtExceptionLogger;
import de.metaphoriker.model.messages.Messages;
import de.metaphoriker.model.updater.Updater;
import de.metaphoriker.model.watcher.FileSystemWatcher;
import de.metaphoriker.randomizer.Main;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerBootstrap {

  @Getter private final ActionSequenceRepository actionSequenceRepository;
  @Getter private final ActionRepository actionRepository;
  private final KeyBindRepository keyBindRepository;
  private final ActionSequenceExecutorRunnable actionSequenceExecutorRunnable;
  private final FileSystemWatcher fileSystemWatcher;

  @Inject
  public RandomizerBootstrap(
      ActionSequenceRepository actionSequenceRepository,
      ActionRepository actionRepository,
      KeyBindRepository keyBindRepository,
      ActionSequenceExecutorRunnable actionSequenceExecutorRunnable,
      FileSystemWatcher fileSystemWatcher) {
    this.actionSequenceRepository = actionSequenceRepository;
    this.actionRepository = actionRepository;
    this.keyBindRepository = keyBindRepository;
    this.actionSequenceExecutorRunnable = actionSequenceExecutorRunnable;
    this.fileSystemWatcher = fileSystemWatcher;
  }

  public void initializeApplication() {
    log.info("Initialisiere Applikation...");
    if (!Main.testMode) installAndRunUpdaterIfNeeded();
    loadKeyBinds();
    registerActions();
    cacheActions();
    startExecutors();
    setupListeners();
    Messages.cache();
    setupGlobalExceptionHandler();
    registerNativeKeyHook();
  }

  private void loadKeyBinds() {
    log.info("Lade KeyBinds...");
    ConfigLoader.loadKeyBinds(keyBindRepository);
  }

  private void setupListeners() {
    log.info("Richte Listener ein...");
    fileSystemWatcher.addFileChangeListener(_ -> cacheActions());
  }

  private void setupGlobalExceptionHandler() {
    log.info("Richte Global Exception Handler ein...");
    Thread.currentThread()
        .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
  }

  private void cacheActions() {
    log.info("Cache Actions...");
    actionSequenceRepository.updateActionSequencesCache();
  }

  private void startExecutors() {
    log.info("Starte Executor...");
    startThread(new Thread(actionSequenceExecutorRunnable));
    startThread(new Thread(new FileSystemWatcher()));
  }

  private void startThread(Thread thread) {
    log.info("Starte Thread: {}", thread.getName());
    thread.setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
    thread.setDaemon(true);
    thread.start();
  }

  private void installAndRunUpdaterIfNeeded() {
    log.info("Richte Appdata Verzeichnis ein...");
    File updater = installUpdater();
    startUpdaterIfNecessary(updater.getAbsolutePath());
  }

  private File installUpdater() {
    log.info("Installiere Updater...");
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

  private void startUpdaterIfNecessary(String path) {
    log.info("Starte Updater falls notwendig...");
    try {
      File jarPath =
          new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
      if (Updater.isUpdateAvailable(Updater.getCurrentVersion(), Updater.RANDOMIZER_VERSION_URL)) {
        log.info("Updater gestartet");

        ProcessBuilder processBuilder =
            new ProcessBuilder(
                "java", "-jar", path, "-randomizerLocation=" + jarPath.getAbsolutePath());

        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();

        System.exit(0);
      }
    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void registerActions() {
    log.info("Registriere Aktionen...");
    actionRepository.register(new PauseAction());
    actionRepository.register(new MouseMoveAction());
    actionRepository.register(new BaseAction("Escape", ActionKey.of("ESCAPE")));
    keyBindRepository
        .getKeyBinds()
        .forEach(
            keyBind -> {
              Action action = new BaseAction(keyBind.getAction(), ActionKey.of(keyBind.getKey()));
              actionRepository.register(action);
            });
    actionRepository.loadStatesIfExist();
  }

  private void registerNativeKeyHook() {
    try {
      log.info("Registriere Native Key Hook...");
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      log.error("Fehler bei der Registrierung des Native Hooks", e);
    }
  }
}
