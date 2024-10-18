package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;
import de.metaphoriker.model.action.sequence.ActionSequence;
import java.util.List;

public class SequencesViewModel {

  private final ActionSequenceRepository actionSequenceRepository;

  @Inject
  public SequencesViewModel(ActionSequenceRepository actionSequenceRepository) {
    this.actionSequenceRepository = actionSequenceRepository;
  }

  public List<ActionSequence> getActionSequences() {
    return actionSequenceRepository.getActionSequences();
  }
}
