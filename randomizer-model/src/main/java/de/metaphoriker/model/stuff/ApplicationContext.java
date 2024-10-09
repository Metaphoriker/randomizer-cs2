package de.metaphoriker.model.stuff;

import de.metaphoriker.model.ApplicationState;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;

public class ApplicationContext {

  private static final File APPDATA_FOLDER =
      new File(System.getenv("APPDATA") + File.separator + "randomizer");

  private static final List<Consumer<ApplicationState>> changeListener = new ArrayList<>();

  @Getter private static volatile ApplicationState applicationState = ApplicationState.IDLING;

  private ApplicationContext() {}

  public static void registerApplicationStateChangeListener(Consumer<ApplicationState> listener) {
    changeListener.add(listener);
  }

  public static void setApplicationState(ApplicationState applicationState) {
    ApplicationContext.applicationState = applicationState;
    changeListener.forEach(changeListener -> changeListener.accept(applicationState));
  }

  public static File getAppdataFolder() {
    return APPDATA_FOLDER;
  }
}
