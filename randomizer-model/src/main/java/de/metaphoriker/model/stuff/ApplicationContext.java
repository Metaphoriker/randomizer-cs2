package de.metaphoriker.model.stuff;

import de.metaphoriker.model.ApplicationState;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;

@Getter
public class ApplicationContext {

  private static final File APPDATA_FOLDER =
      new File(System.getenv("APPDATA") + File.separator + "randomizer");

  private final List<Consumer<ApplicationState>> changeListener = new ArrayList<>();

  private volatile ApplicationState applicationState = ApplicationState.IDLING;

  public void registerApplicationStateChangeListener(Consumer<ApplicationState> listener) {
    changeListener.add(listener);
  }

  public void setApplicationState(ApplicationState applicationState) {
    this.applicationState = applicationState;
    changeListener.forEach(changeListener -> changeListener.accept(applicationState));
  }

  public static File getAppdataFolder() {
    return APPDATA_FOLDER;
  }
}
