package de.metaphoriker.model.action.sequence;

import de.metaphoriker.model.action.Action;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceDispatcher {

  private static final Map<Object, Consumer<Object>> ON_FINISH_MAP = new ConcurrentHashMap<>();
  private static final Map<Class<? extends ActionSequence>, List<Consumer<ActionSequence>>>
      ACTION_SEQUENCE_HANDLERS = new ConcurrentHashMap<>();
  private static final Map<Class<? extends Action>, List<Consumer<Action>>> ACTION_HANDLERS =
      new ConcurrentHashMap<>();

  private ActionSequenceDispatcher() {}

  private static void dispatch(Action action) {
    dispatchActionToHandlers(
        action, ACTION_HANDLERS.getOrDefault(Action.class, Collections.emptyList()));
    dispatchActionToHandlers(
        action, ACTION_HANDLERS.getOrDefault(action.getClass(), Collections.emptyList()));
    action.execute();
    Optional.ofNullable(ON_FINISH_MAP.get(action)).ifPresent(handler -> handler.accept(action));
    log.info("Action erfolgreich dispatched: {}", action);
  }

  public static void dispatchSequence(ActionSequence actionSequence) {
    dispatchActionSequenceToHandlers(
        actionSequence,
        ACTION_SEQUENCE_HANDLERS.getOrDefault(actionSequence.getClass(), Collections.emptyList()));
    dispatchActionSequenceToHandlers(
        actionSequence,
        ACTION_SEQUENCE_HANDLERS.getOrDefault(ActionSequence.class, Collections.emptyList()));
    actionSequence.getActions().forEach(ActionSequenceDispatcher::dispatch);
    Optional.ofNullable(ON_FINISH_MAP.get(actionSequence))
        .ifPresent(handler -> handler.accept(actionSequence));
    log.info("ActionSequence erfolgreich dispatched: {}", actionSequence);
  }

  public static void registerOnFinish(Object key, Consumer<Object> onFinish) {
    ON_FINISH_MAP.put(key, onFinish);
  }

  public static void registerActionHandler(
      Class<? extends Action> actionClass, Consumer<Action> handler) {
    ACTION_HANDLERS.computeIfAbsent(actionClass, _ -> new ArrayList<>()).add(handler);
  }

  public static void registerGenericActionHandler(Consumer<Action> handler) {
    registerActionHandler(Action.class, handler);
  }

  public static void registerSequenceHandler(
      Class<? extends ActionSequence> actionSequenceClass, Consumer<ActionSequence> handler) {
    ACTION_SEQUENCE_HANDLERS
        .computeIfAbsent(actionSequenceClass, _ -> new ArrayList<>())
        .add(handler);
  }

  public static void registerGenericSequenceHandler(Consumer<ActionSequence> handler) {
    registerSequenceHandler(ActionSequence.class, handler);
  }

  private static void dispatchActionToHandlers(Action action, List<Consumer<Action>> handlers) {
    synchronized (handlers) {
      for (Consumer<Action> handler : handlers) {
        try {
          handler.accept(action);
        } catch (Exception e) {
          log.error("Error while handling action: {}", action, e);
        }
      }
    }
  }

  private static void dispatchActionSequenceToHandlers(
      ActionSequence actionSequence, List<Consumer<ActionSequence>> handlers) {
    synchronized (handlers) {
      for (Consumer<ActionSequence> handler : handlers) {
        try {
          handler.accept(actionSequence);
        } catch (Exception e) {
          log.error("Error while handling action sequence: {}", actionSequence, e);
        }
      }
    }
  }
}
