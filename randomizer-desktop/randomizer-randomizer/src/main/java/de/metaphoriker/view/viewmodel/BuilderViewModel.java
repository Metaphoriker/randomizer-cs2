package de.metaphoriker.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.handling.ActionRepository;
import de.metaphoriker.model.action.sequence.ActionSequenceRepository;

import java.util.List;

public class BuilderViewModel {

  @Inject private ActionRepository actionRepository;
  @Inject private ActionSequenceRepository actionSequenceRepository;

  @Inject
  public BuilderViewModel() {}

  public List<Action> getActions() {
    return actionRepository.getActions().keySet().stream()
        .filter(action -> actionRepository.isEnabled(action))
        .toList();
  }
}
