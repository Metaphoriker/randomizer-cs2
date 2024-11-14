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
import com.revortix.model.config.keybind.KeyBind;
import com.revortix.model.config.keybind.KeyBindNameTypeMapper;
import com.revortix.model.config.keybind.KeyBindRepository;
import com.revortix.model.exception.UncaughtExceptionLogger;
import com.revortix.model.messages.Messages;
import com.revortix.model.updater.Updater;
import com.revortix.model.watcher.FileSystemWatcher;
import com.revortix.randomizer.Main;
import com.revortix.randomizer.config.RandomizerConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

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
    private final KeyBindNameTypeMapper keyBindNameTypeMapper;
    private final ActionSequenceExecutorRunnable actionSequenceExecutorRunnable;
    private final RandomizerConfig randomizerConfig;

    @Inject
    public RandomizerBootstrap(
            ActionSequenceRepository actionSequenceRepository,
            ActionRepository actionRepository,
            KeyBindRepository keyBindRepository,
            KeyBindNameTypeMapper keyBindNameTypeMapper,
            ActionSequenceExecutorRunnable actionSequenceExecutorRunnable,
            RandomizerConfig randomizerConfig) {
        this.actionSequenceRepository = actionSequenceRepository;
        this.actionRepository = actionRepository;
        this.keyBindRepository = keyBindRepository;
        this.keyBindNameTypeMapper = keyBindNameTypeMapper;
        this.actionSequenceExecutorRunnable = actionSequenceExecutorRunnable;
        this.randomizerConfig = randomizerConfig;
    }

    public void initializeApplication() {
        log.info("Initialisiere Applikation...");
        loadConfiguration();
        if (!Main.isTestMode() && randomizerConfig.isAutoupdateEnabled()) installAndRunUpdaterIfNeeded();
        loadKeyBinds();
        registerActions();
        cacheActionSequences();
        setupFileWatcher();
        startExecutor();
        Messages.cache();
        setupGlobalExceptionHandler();
        registerNativeKeyHook();
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
        ConfigLoader.loadKeyBinds(keyBindRepository);
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

    private static void startProcess(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        Process process = processBuilder.start();
        process.waitFor();
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
                startProcess(processBuilder);
                System.exit(0); // we want to close the randomizer in order to update it
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
        registerKeyBindActions();
        registerUnboundActions();
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

    /**
     * Registers actions, which are not bound by the user. They are empty and just registered for visual reasons. They
     * are disabled right away.
     */
    private void registerUnboundActions() {
        log.info("Register Unbound Actions...");
        Map<String, KeyBindNameTypeMapper.NameType> descriptors = keyBindNameTypeMapper.getDescriptorToNameMap();
        tailorToUnboundActionNames(descriptors);
        registerUnboundActions(descriptors);
    }

    private void tailorToUnboundActionNames(Map<String, KeyBindNameTypeMapper.NameType> descriptors) {
        actionRepository.getActions().keySet().stream().map(Action::getName).forEach(descriptors::remove);
    }

    private void registerUnboundActions(Map<String, KeyBindNameTypeMapper.NameType> descriptors) {
        descriptors.forEach(
                (name, _) -> {
                    Action action = new BaseAction(name, ActionKey.of(KeyBind.EMPTY_KEY_BIND.getKey()));
                    actionRepository.register(action);
                    actionRepository.disable(action);
                });
        log.info("Registered Unbound Actions: {}", descriptors.keySet());
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
