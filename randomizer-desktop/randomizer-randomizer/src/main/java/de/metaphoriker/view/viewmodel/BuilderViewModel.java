package de.metaphoriker.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.repository.ActionRepository;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;
import java.util.List;

public class BuilderViewModel {

  private final ActionRepository actionRepository;
  private final ActionSequenceRepository actionSequenceRepository;

  @Inject
  public BuilderViewModel(
      ActionRepository actionRepository, ActionSequenceRepository actionSequenceRepository) {
    this.actionRepository = actionRepository;
    this.actionSequenceRepository = actionSequenceRepository;
  }

  public List<Action> getEnabledActions() {
    return actionRepository.getActions().keySet().stream()
        .filter(actionRepository::isEnabled)
        .toList();
  }
}
