package de.metaphoriker.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * The {@code ApplicationContext} class manages and notifies state changes within an application,
 * providing a centralized context for maintaining and accessing application state.
 */
@Getter
public class ApplicationContext {

  private static final File APPDATA_FOLDER =
      new File(System.getenv("APPDATA") + File.separator + "randomizer");

  private final List<Consumer<ApplicationState>> changeListener = new ArrayList<>();

  private volatile ApplicationState applicationState = ApplicationState.IDLING;

  /**
   * Registers a listener to be invoked whenever the application state changes.
   *
   * @param listener A consumer that processes the new application state when it changes.
   */
  public void registerApplicationStateChangeListener(Consumer<ApplicationState> listener) {
    changeListener.add(listener);
  }

  /**
   * Sets the current state of the application and notifies all registered listeners of the change.
   *
   * @param applicationState the new state of the application to be set
   */
  public void setApplicationState(ApplicationState applicationState) {
    this.applicationState = applicationState;
    changeListener.forEach(changeListener -> changeListener.accept(applicationState));
  }

  public static File getAppdataFolder() {
    return APPDATA_FOLDER;
  }
}