package de.metaphoriker.model.action.sequence;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.json.JsonUtil;
import de.metaphoriker.model.stuff.ApplicationContext;
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

  public synchronized void saveActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "ActionSequence darf nicht null sein");

    File file = new File(ACTION_SEQUENCE_FOLDER, actionSequence.getName() + ".sequence");

    log.debug("Speichere ActionSequence in Datei: {}", file.getAbsolutePath());
    try (PrintWriter writer = new PrintWriter(file)) {
      StringBuilder stringBuilder = new StringBuilder();
      for (Action action : actionSequence.getActions()) {
        stringBuilder.append(JsonUtil.serialize(action)).append(";");
      }

      writer.println(stringBuilder);
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

    log.debug("Lösche ActionSequence-Datei: {}", file.getAbsolutePath());
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

    log.debug(
        "Lade ActionSequence-Dateien aus dem Ordner: {}", ACTION_SEQUENCE_FOLDER.getAbsolutePath());
    for (File file : actionSequenceFiles) {
      if (!file.getName().endsWith(".sequence")) continue;

      try {
        String content = Files.readAllLines(file.toPath()).get(0);
        String[] actions = content.split(";");
        List<Action> actionList = new ArrayList<>(actions.length);

        for (String action : actions) {
          actionList.add(JsonUtil.deserialize(action));
        }

        String name = file.getName().replace(".sequence", "");
        actionSequences.add(new ActionSequence(name, actionList));
        log.debug("ActionSequence geladen: {}", name);
      } catch (IOException e) {
        log.error("Fehler beim Laden der ActionSequence aus Datei: {}", file.getAbsolutePath(), e);
      }
    }

    log.info("Insgesamt {} ActionSequences erfolgreich geladen", actionSequences.size());
    return actionSequences;
  }
}
