package de.metaphoriker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.metaphoriker.model.ModelModule;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static boolean testMode = false;
  public static Injector injector;

  public static void main(String[] args) {
    testMode = hasTestModeFlag(args);
    if (testMode) log.debug("Anwendung wird im Testmodus gestartet");
    initializeInjector();
    RandomizerBootstrap randomizerBootstrap = Main.injector.getInstance(RandomizerBootstrap.class);
    randomizerBootstrap.startApplication();
    log.debug("Starte JavaFX Anwendung...");
    Application.launch(RandomizerApplication.class, args);
  }

  private static boolean hasTestModeFlag(String[] args) {
    for (String arg : args) {
      if (arg.startsWith("-testMode=")) {
        return Boolean.parseBoolean(arg.substring(arg.indexOf("=") + 1));
      }
    }
    return false;
  }

  private static void initializeInjector() {
    Main.injector = Guice.createInjector(new ModelModule(), new RandomizerModule());
  }
}
