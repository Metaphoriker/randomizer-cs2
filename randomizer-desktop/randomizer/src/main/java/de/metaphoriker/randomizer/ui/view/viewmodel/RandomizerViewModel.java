package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.action.sequence.ActionSequenceDispatcher;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

public class RandomizerViewModel {

  private final ActionSequenceDispatcher actionSequenceDispatcher;
  @Getter
  private final ObjectProperty<ActionSequence> currentActionSequenceProperty =
      new SimpleObjectProperty<>();

  @Inject
  public RandomizerViewModel(ActionSequenceDispatcher actionSequenceDispatcher) {
    this.actionSequenceDispatcher = actionSequenceDispatcher;
    setupInternalHandler();
  }

  private void setupInternalHandler() {
    actionSequenceDispatcher.registerGenericSequenceHandler(currentActionSequenceProperty::set);
  }
}
