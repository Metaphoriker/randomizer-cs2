package com.revortix.randomizer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revortix.model.ApplicationContext;
import com.revortix.model.ModelModule;
import com.revortix.randomizer.bootstrap.RandomizerBootstrap;
import com.revortix.randomizer.bootstrap.RandomizerModule;
import com.revortix.randomizer.ui.RandomizerApplication;
import javafx.application.Application;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Main {

    private static final String TEST_MODE_FLAG = "-testMode=";

    @Getter
    private static final Injector injector = initializeInjector();
    @Getter
    private static boolean testMode = false;

    public static void main(String[] args) {
        verifyTestMode(args);
        initializeApplication();
        launchApplication(args);
    }

    private static void launchApplication(String[] args) {
        log.debug("Starte JavaFX Anwendung...");
        Application.launch(RandomizerApplication.class, args);
    }

    private static void initializeApplication() {
        RandomizerBootstrap randomizerBootstrap = injector.getInstance(RandomizerBootstrap.class);
        randomizerBootstrap.initializeApplication();
    }

    private static void verifyTestMode(String[] args) {
        testMode = hasTestModeFlag(args);
        if (testMode) {
            log.debug("Anwendung wird im Testmodus gestartet");
        }
    }

    private static boolean hasTestModeFlag(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(TEST_MODE_FLAG)) {
                return Boolean.parseBoolean(arg.substring(TEST_MODE_FLAG.length()));
            }
        }
        return false;
    }

    private static Injector initializeInjector() {
        log.debug("Initialisiere Guice Injector");
        return Guice.createInjector(new ModelModule(), new RandomizerModule());
    }
}
