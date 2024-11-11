package com.revortix.model.action.repository;

import com.revortix.model.action.Action;
import com.revortix.model.persistence.dao.ActionDao;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/** Haelt die Grundinstanzen der Aktionen mit den jeweils zugewiesenen Keybinds. */
@Slf4j
public class ActionRepository {

    private static final ActionDao ACTION_REGISTRY_STORAGE = new ActionDao();

    private final Map<Action, Boolean> actions = new LinkedHashMap<>();

    /**
     * Registers a new action in the repository by adding it to the internal map of actions and saving the state.
     *
     * @param action The action to be registered and enabled. The action will be added to the repository and its state
     *               will be persisted immediately.
     */
    public void register(Action action) {
        actions.put(action, true);
        log.info("Action erfolgreich registriert: {}", action);
    }

    /**
     * Unregisters the given action, removing it from the repository and logging the unregistration event.
     *
     * @param action The action to be unregistered.
     */
    public void unregister(Action action) {
        actions.remove(action);
        log.info("Action erfolgreich abgemeldet: {}", action);
    }

    /**
     * Enables the specified action.
     *
     * @param action The action to be enabled.
     */
    public void enable(Action action) {
        actions.put(action, true);
        log.info("Action erfolgreich aktiviert: {}", action);
    }

    /**
     * Loads the states of the actions if they exist in the persistent storage.
     *
     * <p>This method calls the {@code loadStates()} method of {@code ACTION_REGISTRY_STORAGE}, which
     * loads the states from a storage file and updates the {@code actions} map with the persisted states.
     *
     * <p>The action states (enabled/disabled) are initially stored within the map, and upon loading,
     * the map will be updated to reflect the persisted states where available.
     *
     * <p>A log entry is created indicating the successful loading of action states.
     */
    public void loadStatesIfExist() {
        ACTION_REGISTRY_STORAGE.loadStates(actions);
        log.info("Zustände der Actions erfolgreich geladen");
    }

    /**
     * Disables the specified action by setting its state to false in the internal actions map and saving the updated
     * states to persistent storage.
     *
     * @param action the Action to be disabled.
     */
    public void disable(Action action) {
        actions.put(action, false);
        log.info("Action erfolgreich deaktiviert: {}", action);
    }

    /**
     * Checks if the specified action is currently enabled.
     *
     * @param action The action to check for its enabled state.
     *
     * @return true if the action is enabled or if it is not found in the repository, false otherwise.
     */
    public boolean isEnabled(Action action) {
        return actions.getOrDefault(action, true);
    }

    /**
     * Persists all actions currently in memory to a storage system.
     *
     * <p>This method invokes the save method on the ACTION_REGISTRY_STORAGE with the current list of
     * actions to ensure they are stored.
     *
     * <p>This functionality is crucial for maintaining state and ensuring that no actions are lost
     * between sessions or instances.
     */
    public void saveAll() {
        ACTION_REGISTRY_STORAGE.save(actions);
    }

    /**
     * Retrieves a map of registered actions along with their enabled/disabled state.
     *
     * @return a Map containing {@link Action} objects as keys and their corresponding Boolean state indicating whether
     * the action is enabled (true) or disabled (false).
     *
     * @throws RuntimeException if cloning an action fails.
     */
    public Map<Action, Boolean> getActions() {
        Map<Action, Boolean> actions = new LinkedHashMap<>();
        this.actions.forEach(
                (action, enabled) -> {
                    try {
                        actions.put(action.clone(), enabled);
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                });
        return actions;
    }

    /**
     * Retrieves an Action by its name and returns a cloned copy of it.
     *
     * @param actionName the name of the action to retrieve
     *
     * @return a cloned copy of the Action with the specified name
     *
     * @throws IllegalArgumentException if no action with the specified name is found
     * @throws RuntimeException         if the action cannot be cloned
     */
    public Action getByName(String actionName) {
        Action originalAction =
                actions.keySet().stream()
                        .filter(action -> action.getName().equals(actionName))
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalArgumentException("Action nicht gefunden: " + actionName));

        try {
            return originalAction.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning not supported for action: " + actionName, e);
        }
    }
}
