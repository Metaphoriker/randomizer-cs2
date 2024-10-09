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
          "Failed to create actionSequence folder: {}", ACTION_SEQUENCE_FOLDER.getAbsolutePath());
    }
  }

  public synchronized void saveActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "Cluster cannot be null");

    File file = new File(ACTION_SEQUENCE_FOLDER, actionSequence.getName() + ".sequence");

    try (PrintWriter writer = new PrintWriter(file)) {
      StringBuilder stringBuilder = new StringBuilder();
      for (Action action : actionSequence.getActions()) {
        stringBuilder.append(JsonUtil.serialize(action)).append(";");
      }

      writer.println(stringBuilder);
      writer.flush();
      log.info("Successfully saved actionSequence: {}", actionSequence.getName());
    } catch (IOException e) {
      log.error("Failed to save actionSequence: {}", actionSequence.getName(), e);
    }
  }

  public synchronized void deleteActionSequence(ActionSequence actionSequence) {
    Objects.requireNonNull(actionSequence, "Cluster cannot be null");

    File file = new File(ACTION_SEQUENCE_FOLDER, actionSequence.getName() + ".sequence");
    if (!file.exists()) {
      log.warn("Cluster file does not exist: {}", file.getAbsolutePath());
      return;
    }

    try {
      Files.delete(file.toPath());
      log.info("Successfully deleted actionSequence: {}", actionSequence.getName());
    } catch (IOException e) {
      log.error("Failed to delete actionSequence: {}", actionSequence.getName(), e);
      throw new RuntimeException(e);
    }
  }

  public List<ActionSequence> loadActionSequences() {
    List<ActionSequence> actionSequences = new ArrayList<>();
    File[] actionSequenceFiles = ACTION_SEQUENCE_FOLDER.listFiles();

    if (actionSequenceFiles == null || actionSequenceFiles.length == 0) {
      log.warn("No actionSequence files found in: {}", ACTION_SEQUENCE_FOLDER.getAbsolutePath());
      return actionSequences;
    }

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
      } catch (IOException e) {
        log.error("Failed to load actionSequence from file: {}", file.getAbsolutePath(), e);
      }
    }

    log.info("Successfully loaded {} actionSequences", actionSequences.size());
    return actionSequences;
  }
}
