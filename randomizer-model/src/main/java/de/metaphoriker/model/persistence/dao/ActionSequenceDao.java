package de.metaphoriker.model.persistence.dao;

import com.google.inject.Inject;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.persistence.JsonUtil;
import de.metaphoriker.model.ApplicationContext;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceDao {

  public static final File ACTION_SEQUENCE_FOLDER =
      new File(ApplicationContext.getAppdataFolder() + File.separator + "sequences");

  static {
    if (!ACTION_SEQUENCE_FOLDER.exists() && !ACTION_SEQUENCE_FOLDER.mkdirs()) {
      log.error(
          "Fehler beim Erstellen des actionSequence-Ordners: {}",
          ACTION_SEQUENCE_FOLDER.getAbsolutePath());
    }
  }

  @Inject JsonUtil jsonUtil;

  public synchronized void saveActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    File file = new File(ACTION_SEQUENCE_FOLDER, actionSequence.getName() + ".sequence");

    try (PrintWriter writer = new PrintWriter(file)) {
      String json = jsonUtil.serialize(actionSequence);
      writer.println(json);
      writer.flush();
      log.info("ActionSequence erfolgreich gespeichert: {}", actionSequence.getName());
    } catch (IOException e) {
      log.error("Fehler beim Speichern der ActionSequence: {}", actionSequence.getName(), e);
    }
  }

  public synchronized void deleteActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    File file = new File(ACTION_SEQUENCE_FOLDER, actionSequence.getName() + ".sequence");
    if (!file.exists()) {
      log.warn("ActionSequence-Datei existiert nicht: {}", file.getAbsolutePath());
      return;
    }

    try {
      Files.delete(file.toPath());
      log.info("ActionSequence erfolgreich gelöscht: {}", actionSequence.getName());
    } catch (IOException e) {
      log.error("Fehler beim Löschen der ActionSequence: {}", actionSequence.getName(), e);
      throw new RuntimeException(e);
    }
  }

  public List<ActionSequence> loadActionSequences() {
    List<ActionSequence> actionSequences = new ArrayList<>();
    File[] actionSequenceFiles = ACTION_SEQUENCE_FOLDER.listFiles();

    if (actionSequenceFiles == null || actionSequenceFiles.length == 0) {
      log.warn(
          "Keine ActionSequence-Dateien gefunden im Ordner: {}",
          ACTION_SEQUENCE_FOLDER.getAbsolutePath());
      return actionSequences;
    }

    for (File file : actionSequenceFiles) {
      if (!file.getName().endsWith(".sequence")) continue;

      try {
        String content = new String(Files.readAllBytes(file.toPath()));
        ActionSequence actionSequence = jsonUtil.deserialize(content);
        actionSequences.add(actionSequence);
      } catch (IOException e) {
        log.error("Fehler beim Laden der ActionSequence aus Datei: {}", file.getAbsolutePath(), e);
      }
    }

    log.info("Insgesamt {} ActionSequences erfolgreich geladen", actionSequences.size());
    return actionSequences;
  }
}
