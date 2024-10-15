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

    log.debug("Speichere ActionSequence: {}", actionSequence.getName());
    actionSequencesMap.put(actionSequence.getName(), actionSequence);
    actionSequenceDao.saveActionSequence(actionSequence);
    isCacheUpdated = true;
  }

  public synchronized void deleteActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    if (!actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn(
          "Versucht, eine nicht existierende ActionSequence zu löschen: {}",
          actionSequence.getName());
      return;
    }

    log.debug("Lösche ActionSequence: {}", actionSequence.getName());
    removeActionSequence(actionSequence.getName());
    actionSequenceDao.deleteActionSequence(actionSequence);
    isCacheUpdated = true;
  }

  public synchronized void addActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    if (actionSequencesMap.containsKey(actionSequence.getName())) {
      log.warn(
          "ActionSequence mit dem Namen '{}' existiert bereits im Speicher.",
          actionSequence.getName());
      return;
    }

    log.debug("Füge ActionSequence hinzu: {}", actionSequence.getName());
    actionSequencesMap.put(actionSequence.getName(), actionSequence);
  }

  public synchronized void removeActionSequence(String name) {
    log.debug("Entferne ActionSequence: {}", name);
    if (actionSequencesMap.remove(name) == null) {
      log.warn(
          "Keine ActionSequence mit dem Namen '{}' im Speicher gefunden, um sie zu entfernen.",
          name);
    }
  }

  public synchronized List<ActionSequence> loadActionSequences() {
    if (!isCacheUpdated) {
      log.debug("Cache ist nicht aktuell, aktualisiere Cache");
      updateCache();
    }
    return getActionSequences();
  }

  public synchronized Optional<ActionSequence> getActionSequence(String name) {
    log.debug("Hole ActionSequence: {}", name);
    return Optional.ofNullable(actionSequencesMap.get(name));
  }

  public synchronized List<ActionSequence> getActionSequences() {
    log.debug("Gebe Kopie der aktuellen ActionSequence-Liste zurück");
    return new ArrayList<>(actionSequencesMap.values());
  }

  private synchronized void updateCache() {
    log.debug("Aktualisiere Cache mit ActionSequences aus der Datenbank");
    actionSequencesMap.clear();
    actionSequenceDao
        .loadActionSequences()
        .forEach(sequence -> actionSequencesMap.put(sequence.getName(), sequence));
    isCacheUpdated = true;
  }
}
