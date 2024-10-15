package de.metaphoriker.model.action.sequence;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceRepository {

  private final ActionSequenceDao actionSequenceDao = new ActionSequenceDao();
  private final Map<String, ActionSequence> actionSequencesMap = new HashMap<>();

  private boolean isCacheUpdated = false;

  public synchronized void saveActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    if (actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn(
          "ActionSequence mit dem Namen '{}' existiert bereits und wird überschrieben.",
          actionSequence.getName());
    }

    actionSequencesMap.put(actionSequence.getName(), actionSequence);
    actionSequenceDao.saveActionSequence(actionSequence);
    isCacheUpdated = true;
    log.info("ActionSequence '{}' gespeichert.", actionSequence.getName());
  }

  public synchronized void deleteActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    if (!actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn(
          "Versucht, eine nicht existierende ActionSequence zu löschen: {}",
          actionSequence.getName());
      return;
    }

    removeActionSequence(actionSequence.getName());
    actionSequenceDao.deleteActionSequence(actionSequence);
    isCacheUpdated = true;
    log.info("ActionSequence '{}' gelöscht.", actionSequence.getName());
  }

  public synchronized void addActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    if (actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn(
          "ActionSequence mit dem Namen '{}' existiert bereits im Speicher.",
          actionSequence.getName());
      return;
    }

    actionSequencesMap.put(actionSequence.getName(), actionSequence);
    log.info("ActionSequence '{}' hinzugefügt.", actionSequence.getName());
  }

  public synchronized void removeActionSequence(String name) {
    if (actionSequencesMap.remove(name) == null) {
      log.warn(
          "Keine ActionSequence mit dem Namen '{}' im Speicher gefunden, um sie zu entfernen.",
          name);
      return;
    }
    log.info("ActionSequence '{}' entfernt.", name);
  }

  public synchronized void loadActionSequences() {
    if (!isCacheUpdated) {
      updateCache();
      log.info("Cache aktualisiert.");
    } else {
      log.info("Cache ist aktuell.");
    }
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
        .forEach(sequence -> actionSequencesMap.put(sequence.getName(), sequence));
    isCacheUpdated = true;
  }
}
