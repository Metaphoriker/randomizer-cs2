package de.metaphoriker.model.action.handling;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionDispatcher {

  private static final Map<Object, Consumer<Object>> ON_FINISH_MAP = new ConcurrentHashMap<>();
  private static final Map<ActionSequence, List<Consumer<ActionSequence>>>
      ACTION_SEQUENCE_HANDLERS = new ConcurrentHashMap<>();
  private static final Map<Class<? extends Action>, List<Consumer<Action>>> ACTION_HANDLERS =
      new ConcurrentHashMap<>();

  private ActionDispatcher() {}

  public static void dispatch(Action action) {
    log.debug("Dispatching Action: {}", action);
    dispatchToHandlers(action, ACTION_HANDLERS.getOrDefault(Action.class, Collections.emptyList()));
    dispatchToHandlers(
        action, ACTION_HANDLERS.getOrDefault(action.getClass(), Collections.emptyList()));
    action.execute();
    Optional.ofNullable(ON_FINISH_MAP.get(action)).ifPresent(handler -> handler.accept(action));
    log.debug("Action dispatched and executed: {}", action);
  }

  public static void dispatchCluster(ActionSequence actionSequence) {
    log.debug("Dispatching ActionSequence: {}", actionSequence);
    ACTION_SEQUENCE_HANDLERS
        .getOrDefault(actionSequence, Collections.emptyList())
        .forEach(handler -> handler.accept(actionSequence));
    actionSequence.getActions().forEach(ActionDispatcher::dispatch);
    Optional.ofNullable(ON_FINISH_MAP.get(actionSequence))
        .ifPresent(handler -> handler.accept(actionSequence));
    log.debug("ActionSequence dispatched and executed: {}", actionSequence);
  }

  public static void registerOnFinish(Object key, Consumer<Object> onFinish) {
    log.debug("Registriere OnFinish-Handler für: {}", key);
    ON_FINISH_MAP.put(key, onFinish);
  }

  public static void registerHandler(
      Class<? extends Action> actionClass, Consumer<Action> handler) {
    log.debug("Registriere Handler für Action-Klasse: {}", actionClass);
    ACTION_HANDLERS.computeIfAbsent(actionClass, k -> new ArrayList<>()).add(handler);
  }

  public static void registerGenericHandler(Consumer<Action> handler) {
    registerHandler(Action.class, handler);
  }

  public static void registerGenericClusterHandler(
      ActionSequence actionSequence, Consumer<ActionSequence> handler) {
    log.debug("Registriere Handler für ActionSequence: {}", actionSequence);
    ACTION_SEQUENCE_HANDLERS.computeIfAbsent(actionSequence, _ -> new ArrayList<>()).add(handler);
  }

  private static void dispatchToHandlers(Action action, List<Consumer<Action>> handlers) {
    handlers.forEach(
        handler -> {
          log.debug("Handler ausführen für Action: {}", action);
          handler.accept(action);
        });
  }
}
