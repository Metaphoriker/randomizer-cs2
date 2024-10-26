package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationContext;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.action.sequence.ActionSequenceDispatcher;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

public class RandomizerViewModel {

  private final ActionSequenceDispatcher actionSequenceDispatcher;
  private final ApplicationContext applicationContext;

  @Getter
  private final ObjectProperty<ActionSequence> currentActionSequenceProperty =
      new SimpleObjectProperty<>();

  @Inject
  public RandomizerViewModel(
      ActionSequenceDispatcher actionSequenceDispatcher, ApplicationContext applicationContext) {
    this.actionSequenceDispatcher = actionSequenceDispatcher;
    this.applicationContext = applicationContext;
    setupInternalHandler();
  }

  public void setApplicationStateToRunning() {
    applicationContext.setApplicationState(ApplicationState.RUNNING);
  }

  public void setApplicationStateToStopped() {
    applicationContext.setApplicationState(ApplicationState.IDLING);
  }

  private void setupInternalHandler() {
    actionSequenceDispatcher.registerGenericSequenceHandler(currentActionSequenceProperty::set);
  }
}
