package de.metaphoriker.gui.view.models;

import de.metaphoriker.Main;
import de.metaphoriker.gui.view.ViewModel;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.action.sequence.ActionSequenceRepository;
import de.metaphoriker.model.json.JsonUtil;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BuilderViewModel implements ViewModel {

  private final ActionSequenceRepository actionSequenceRepository;

  public BuilderViewModel(ActionSequenceRepository actionSequenceRepository) {
    this.actionSequenceRepository = actionSequenceRepository;
  }

  public void saveCluster(String name, String content) {
    actionSequenceRepository.saveActionSequence(ActionSequence.formatEventCluster(name, content));
  }

  public String serialize(Action action) {
    return JsonUtil.serialize(action);
  }

  public Action deserialize(String json) {
    return JsonUtil.deserialize(json);
  }

  public Action getEvent(String name) {
    return Main.getEventRegistry().getByName(name);
  }

  public Action getRandomEvent() {

    Set<Action> actions = Main.getEventRegistry().getActions();
    int index = ThreadLocalRandom.current().nextInt(0, actions.size());

    return (Action) actions.toArray()[index];
  }

  public Set<Action> getEvents() {
    return Main.getEventRegistry().getActions();
  }

  public List<ActionSequence> loadEventClusters() {
    return actionSequenceRepository.loadActionSequences();
  }

  public List<ActionSequence> getClusters() {
    return actionSequenceRepository.getActionSequences();
  }
}
