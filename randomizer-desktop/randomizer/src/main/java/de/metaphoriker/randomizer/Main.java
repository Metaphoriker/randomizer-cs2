package de.metaphoriker.randomizer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.metaphoriker.model.ModelModule;
import de.metaphoriker.randomizer.bootstrap.RandomizerBootstrap;
import de.metaphoriker.randomizer.bootstrap.RandomizerModule;
import de.metaphoriker.randomizer.ui.RandomizerApplication;
import javafx.application.Application;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  private static final String TEST_MODE_FLAG = "-testMode=";

  @Getter private static final Injector injector = initializeInjector();
  @Getter private static boolean testMode = false;

  public static void main(String[] args) {
    testMode = hasTestModeFlag(args);
    if (testMode) {
      log.debug("Anwendung wird im Testmodus gestartet");
    }
    RandomizerBootstrap randomizerBootstrap = injector.getInstance(RandomizerBootstrap.class);
    randomizerBootstrap.initializeApplication();
    log.debug("Starte JavaFX Anwendung...");
    Application.launch(RandomizerApplication.class, args);
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
