package de.metaphoriker.model.action.sequence;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceRepository {

  private final ActionSequenceDao actionSequenceDao = new ActionSequenceDao();
  private final Map<String, ActionSequence> actionSequencesMap = new HashMap<>();

  private boolean isCacheUpdated = false;

  public synchronized void saveActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence cannot be null");

    if (actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn(
          "ActionSequence with name '{}' already exists and will be overwritten.",
          actionSequence.getName());
    }

    actionSequencesMap.put(actionSequence.getName(), actionSequence);
    actionSequenceDao.saveActionSequence(actionSequence);
    isCacheUpdated = true;
  }

  public synchronized void deleteActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence cannot be null");

    if (!actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn("Attempted to delete a non-existent actionSequence: {}", actionSequence.getName());
      return;
    }

    removeActionSequence(actionSequence.getName());
    actionSequenceDao.deleteActionSequence(actionSequence);
    isCacheUpdated = true;
  }

  public synchronized void addActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "Cluster cannot be null");

    if (actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn("ActionSequence with name '{}' already exists in memory.", actionSequence.getName());
      return;
    }

    actionSequencesMap.put(actionSequence.getName(), actionSequence);
  }

  public synchronized void removeActionSequence(String name) {
    if (actionSequencesMap.remove(name) == null) {
      log.warn("No ActionSequence with name '{}' found in memory to remove.", name);
    }
    actionSequencesMap.remove(name);
  }

  public synchronized List<ActionSequence> loadActionSequences() {
    if (!isCacheUpdated) {
      updateCache();
    }
    return getActionSequences();
  }

  public synchronized Optional<ActionSequence> getActionSequence(String name) {
    return Optional.ofNullable(actionSequencesMap.get(name));
  }

  public synchronized List<ActionSequence> getActionSequences() {
    return new ArrayList<>(actionSequencesMap.values());
  }

  private synchronized void updateCache() {
    actionSequencesMap.clear();
    actionSequenceDao
        .loadActionSequences()
        .forEach(cluster -> actionSequencesMap.put(cluster.getName(), cluster));
    isCacheUpdated = true;
  }
}
