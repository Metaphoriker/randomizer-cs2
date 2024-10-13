package de.metaphoriker.model.action.handling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to store the state of actions.
 *
 * <p>The state consists of the action itself and a boolean flag indicating whether the action is
 * enabled or not.
 */
public class ActionRegistryStorage {

  private static final File STORAGE_FILE =
      new File(ApplicationContext.getAppdataFolder() + File.separator + "actions.json");
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public Map<String, Boolean> load() {
    if (!STORAGE_FILE.exists()) {
      return new HashMap<>();
    }

    try {
      return OBJECT_MAPPER.readValue(STORAGE_FILE, new TypeReference<>() {});
    } catch (IOException e) {
      throw new RuntimeException("Failed to load actions from file", e);
    }
  }

  public void save(Map<Action, Boolean> actions) {
    Map<String, Boolean> actionStates = new HashMap<>();
    actions.forEach((action, enabled) -> actionStates.put(action.name(), enabled));

    try {
      OBJECT_MAPPER.writeValue(STORAGE_FILE, actionStates);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save actions to file", e);
    }
  }

  public void loadStates(Map<Action, Boolean> actions) {
    Map<String, Boolean> loadedActions = load();
    actions
        .keySet()
        .forEach(
            action -> {
              Boolean enabled = loadedActions.get(action.name());
              if (enabled != null) {
                actions.put(action, enabled);
              }
            });
  }
}
