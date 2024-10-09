package de.metaphoriker.model.action.handling;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ActionDispatcher {

  private static final Map<Object, Consumer<Object>> ON_FINISH_MAP = new ConcurrentHashMap<>();
  private static final Map<ActionSequence, List<Consumer<ActionSequence>>>
      ACTION_SEQUENCE_HANDLERS = new ConcurrentHashMap<>();
  private static final Map<Class<? extends Action>, List<Consumer<Action>>> ACTION_HANDLERS =
      new ConcurrentHashMap<>();

  private ActionDispatcher() {}

  public static void dispatch(Action action) {
    List<Consumer<Action>> genericHandlers =
        ACTION_HANDLERS.getOrDefault(Action.class, Collections.emptyList());
    genericHandlers.forEach(handler -> handler.accept(action));
    List<Consumer<Action>> specificHandlers =
        ACTION_HANDLERS.getOrDefault(action.getClass(), Collections.emptyList());
    specificHandlers.forEach(handler -> handler.accept(action));
    action.execute();
    Optional.ofNullable(ON_FINISH_MAP.get(action)).ifPresent(handler -> handler.accept(action));
  }

  public static void dispatchCluster(ActionSequence actionSequence) {
    List<Consumer<ActionSequence>> actionSequenceHandlersOrDefault =
        ACTION_SEQUENCE_HANDLERS.getOrDefault(actionSequence, Collections.emptyList());
    actionSequenceHandlersOrDefault.forEach(handler -> handler.accept(actionSequence));
    actionSequence.getActions().forEach(ActionDispatcher::dispatch);
    Optional.ofNullable(ON_FINISH_MAP.get(actionSequence))
        .ifPresent(handler -> handler.accept(actionSequence));
  }

  public static void registerOnFinish(Object key, Consumer<Object> onFinish) {
    ON_FINISH_MAP.put(key, onFinish);
  }

  public static void registerHandler(
      Class<? extends Action> actionClass, Consumer<Action> handler) {
    ACTION_HANDLERS.computeIfAbsent(actionClass, k -> new ArrayList<>()).add(handler);
  }

  public static void registerGenericHandler(Consumer<Action> handler) {
    registerHandler(Action.class, handler);
  }

  public static void registerGenericClusterHandler(
      ActionSequence actionSequence, Consumer<ActionSequence> handler) {
    ACTION_SEQUENCE_HANDLERS.computeIfAbsent(actionSequence, _ -> new ArrayList<>()).add(handler);
  }
}
