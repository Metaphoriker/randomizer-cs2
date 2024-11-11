package com.revortix.model.persistence.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revortix.model.ApplicationContext;
import com.revortix.model.action.Action;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ActionDao {

    private static final File STORAGE_FILE =
            new File(ApplicationContext.getAppdataFolder() + File.separator + "actions.json");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Loads the states of actions from a storage file.
     *
     * @return A map containing the action names as keys and their corresponding boolean states. If the storage file
     * does not exist or an error occurs during loading, an empty map is returned.
     *
     * @throws RuntimeException if an error occurs while loading the file.
     */
    public Map<String, Boolean> load() {
        if (!STORAGE_FILE.exists()) {
            log.warn("Speicherdatei existiert nicht: {}", STORAGE_FILE.getAbsolutePath());
            return new HashMap<>();
        }

        try {
            return OBJECT_MAPPER.readValue(STORAGE_FILE, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error(
                    "Fehler beim Laden der Zustände der Aktionen aus Datei: {}",
                    STORAGE_FILE.getAbsolutePath(),
                    e);
            throw new RuntimeException("Fehler beim Laden der Aktionen aus Datei", e);
        }
    }

    /**
     * Saves the state of the provided actions to persistent storage.
     *
     * @param actions A map where keys represent Action objects and values represent their enabled/disabled state
     *                (true/false). The state of each action in this map will be saved to a file for future retrieval.
     */
    public void save(Map<Action, Boolean> actions) {
        Map<String, Boolean> actionStates = new HashMap<>();
        actions.forEach((action, enabled) -> actionStates.put(action.getName(), enabled));

        try {
            OBJECT_MAPPER.writeValue(STORAGE_FILE, actionStates);
            log.info(
                    "Zustände von {} Aktionen erfolgreich gespeichert in Datei: {}",
                    actions.size(),
                    STORAGE_FILE.getAbsolutePath());
        } catch (IOException e) {
            log.error(
                    "Fehler beim Speichern der Zustände der Aktionen in Datei: {}",
                    STORAGE_FILE.getAbsolutePath(),
                    e);
            throw new RuntimeException("Fehler beim Speichern der Aktionen in Datei", e);
        }
    }

    /**
     * Loads the state of each action and applies the state to the provided actions map. The state is loaded from
     * persistent storage and matched against the action names.
     *
     * @param actions A map linking Action objects to their enabled/disabled state. Upon loading, this map will be
     *                updated to reflect the persisted states where available.
     */
    public void loadStates(Map<Action, Boolean> actions) {
        Map<String, Boolean> loadedActions = load();
        actions
                .keySet()
                .forEach(
                        action -> {
                            Boolean enabled = loadedActions.get(action.getName());
                            if (enabled != null) {
                                actions.put(action, enabled);
                            }
                        });
        log.info("Zustände der Aktionen erfolgreich geladen und angewendet");
    }
}
