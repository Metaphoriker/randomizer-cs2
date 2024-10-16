package de.metaphoriker.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.repository.ActionRepository;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;

import java.util.List;

public class BuilderViewModel {

  @Inject private ActionRepository actionRepository;
  @Inject private ActionSequenceRepository actionSequenceRepository;

  @Inject
  public BuilderViewModel() {}

  public List<Action> getEnabledActions() {
    return actionRepository.getActions().keySet().stream()
        .filter(action -> actionRepository.isEnabled(action))
        .toList();
  }
}
