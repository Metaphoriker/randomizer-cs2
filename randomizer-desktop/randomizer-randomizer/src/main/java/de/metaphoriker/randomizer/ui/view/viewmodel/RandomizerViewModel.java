package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationContext;
import de.metaphoriker.model.ApplicationState;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

public class RandomizerViewModel {

  @Getter
  private final Property<ApplicationState> applicationStateProperty = new SimpleObjectProperty<>();

  private final ApplicationContext applicationContext;

  @Inject
  public RandomizerViewModel(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    registerStateListener();
  }

  public void setIdling() {
    applicationContext.setApplicationState(ApplicationState.IDLING);
  }

  public void setRunning() {
    applicationContext.setApplicationState(ApplicationState.RUNNING);
  }

  private void registerStateListener() {
    applicationContext.registerApplicationStateChangeListener(applicationStateProperty::setValue);
  }
}
