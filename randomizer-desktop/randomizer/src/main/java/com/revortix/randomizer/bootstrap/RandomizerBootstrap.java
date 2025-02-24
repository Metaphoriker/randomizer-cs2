package com.revortix.randomizer.bootstrap;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.google.inject.Inject;
import com.revortix.model.ApplicationContext;
import com.revortix.model.action.Action;
import com.revortix.model.action.ActionKey;
import com.revortix.model.action.impl.BaseAction;
import com.revortix.model.action.impl.MouseMoveAction;
import com.revortix.model.action.impl.PauseAction;
import com.revortix.model.action.repository.ActionRepository;
import com.revortix.model.action.repository.ActionSequenceRepository;
import com.revortix.model.action.sequence.ActionSequenceExecutorRunnable;
import com.revortix.model.config.ConfigLoader;
import com.revortix.model.config.keybind.KeyBindRepository;
import com.revortix.model.exception.UncaughtExceptionLogger;
import com.revortix.model.messages.Messages;
import com.revortix.model.watcher.FileSystemWatcher;
import com.revortix.randomizer.Main;
import com.revortix.randomizer.config.RandomizerConfig;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * The RandomizerBootstrap class is responsible for initializing and configuring the application. It sets up
 * repositories, executors, file watchers, exception handlers, and loads necessary configurations.
 */
@Slf4j
public class RandomizerBootstrap {

    @Getter
    private final ActionSequenceRepository actionSequenceRepository;
    @Getter
    private final ActionRepository actionRepository;
    private final KeyBindRepository keyBindRepository;
    private final ActionSequenceExecutorRunnable actionSequenceExecutorRunnable;
    private final RandomizerConfig randomizerConfig;
    private final RandomizerUpdater randomizerUpdater;

    @Inject
    public RandomizerBootstrap(
            ActionSequenceRepository actionSequenceRepository,
            ActionRepository actionRepository,
            KeyBindRepository keyBindRepository,
            ActionSequenceExecutorRunnable actionSequenceExecutorRunnable,
            RandomizerConfig randomizerConfig,
            RandomizerUpdater randomizerUpdater) {
        this.actionSequenceRepository = actionSequenceRepository;
        this.actionRepository = actionRepository;
        this.keyBindRepository = keyBindRepository;
        this.actionSequenceExecutorRunnable = actionSequenceExecutorRunnable;
        this.randomizerConfig = randomizerConfig;
        this.randomizerUpdater = randomizerUpdater;
    }

    public void initializeApplication() {
        log.info("Initialisiere Applikation...");
        loadConfiguration();
        randomizerUpdater.installUpdater();
        if (!Main.isTestMode() && randomizerConfig.isAutoupdateEnabled()) randomizerUpdater.runUpdaterIfNeeded();
        loadKeyBinds();
        ladeUserKeyBinds();
        registerActions();
        setupFileWatcher();
        Messages.cache();
        setupGlobalExceptionHandler();
        registerNativeKeyHook();
        cacheActionSequences();
        startExecutor();
    }

    private void loadConfiguration() {
        log.info("Lade Konfiguration...");
        randomizerConfig.setDirectory(ApplicationContext.getAppdataFolder());
        randomizerConfig.initialize();

        ActionSequenceExecutorRunnable.setMinWaitTime(randomizerConfig.getMinInterval());
        ActionSequenceExecutorRunnable.setMaxWaitTime(randomizerConfig.getMaxInterval());
    }

    private void loadKeyBinds() {
        log.info("Lade KeyBinds...");
        try {
            String configPath = ConfigLoader.findDefaultConfigFile();
            if(configPath != null) {
                ConfigLoader.loadDefaultKeyBinds(configPath, keyBindRepository);
                log.info("Default KeyBinds erfolgreich geladen!");
                return;
            }
        } catch (Exception e) {
        }

        log.info("KeyBinds nicht gefunden - erwarte User Input!");
        sendDialog(input -> {
            if(input == null) {
                loadKeyBinds();
                return;
            }
            try {
                ConfigLoader.loadDefaultKeyBinds(input, keyBindRepository);
                log.info("Default KeyBinds erfolgreich geladen!");
                return;
            } catch(Exception e) {
            }
            loadKeyBinds();
        });
    }

  private void sendDialog(Consumer<String> inputConsumer) {
    log.info("Zeige Dialog, um Pfad einzugeben...");
    SwingUtilities.invokeLater(
        () -> {
          JFrame frame = new JFrame("Eingabe erforderlich");
          String input =
              JOptionPane.showInputDialog(
                  frame,
                  "Bitte geben Sie den Pfad ein:",
                  "Pfad eingeben",
                  JOptionPane.PLAIN_MESSAGE);
          if (input != null && !input.isEmpty()) {
            log.info("Pfad eingegeben: {}", input);
            inputConsumer.accept(input);
          } else {
              inputConsumer.accept(null);
          }
          frame.dispose();
        });
  }

    private void ladeUserKeyBinds() {
        log.info("Lade User KeyBinds...");
        try {
            String configPath = ConfigLoader.findUserConfigFile();
            if (configPath != null) {
                ConfigLoader.loadUserKeyBindings(configPath, keyBindRepository);
                log.info("User KeyBinds erfolgreich geladen!");
                return;
            }
        } catch (Exception e) {}

        log.info("User KeyBinds nicht gefunden - erwarte User Input!");
        sendDialog(input -> {
            try {
                ConfigLoader.loadUserKeyBindings(input, keyBindRepository);
                log.info("User KeyBinds erfolgreich geladen!");
                return;
            } catch(Exception e) {
            }
            ladeUserKeyBinds();
        });
    }

    private void setupFileWatcher() {
        log.info("Starte FileWatcher");
        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher();
        fileSystemWatcher.addFileChangeListener(_ -> cacheActionSequences());
        startThread(new Thread(fileSystemWatcher));
    }

    private void setupGlobalExceptionHandler() {
        log.info("Richte Global Exception Handler ein...");
        Thread.currentThread()
                .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
    }

    private void cacheActionSequences() {
        log.info("Cache ActionSequences...");
        actionSequenceRepository.updateActionSequencesCache();
    }

    private void startExecutor() {
        log.info("Starte Executor...");
        startThread(new Thread(actionSequenceExecutorRunnable));
    }

    private void startThread(Thread thread) {
        log.info("Starte Thread: {}", thread.getName());
        thread.setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);
        thread.setDaemon(true);
        thread.start();
    }

    private void registerActions() {
        log.info("Registriere Aktionen...");
        actionRepository.register(new PauseAction());
        // actionRepository.register(new MouseMoveAction());
        // actionRepository.register(new BaseAction("Escape", ActionKey.of("ESCAPE")));
        registerKeyBindActions();
        log.info("{} Aktionen registriert", actionRepository.getActions().size());
    }

    private void registerKeyBindActions() {
        keyBindRepository
                .getKeyBinds()
                .forEach(
                        keyBind -> {
                            Action action = new BaseAction(keyBind.getAction(), ActionKey.of(keyBind.getKey()));
                            actionRepository.register(action);
                        });
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
