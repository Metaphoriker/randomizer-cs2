package de.metaphoriker.randomizer.ui.view.viewmodel;

import de.metaphoriker.model.action.sequence.ActionSequence;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

@Getter
public class SequenceBuilderViewModel {

  private final Property<ActionSequence> currentActionSequenceProperty =
      new SimpleObjectProperty<>();

  public void setCurrentActionSequence(ActionSequence actionSequence) {
    currentActionSequenceProperty.setValue(actionSequence);
  }
}
