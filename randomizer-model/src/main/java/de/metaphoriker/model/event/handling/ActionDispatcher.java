package de.metaphoriker.model.event.handling;

import de.metaphoriker.model.event.Action;
import de.metaphoriker.model.event.cluster.ActionSequence;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ActionDispatcher {

  private static final Map<Object, Consumer<Object>> ON_FINISH_MAP = new ConcurrentHashMap<>();
  private static final Map<ActionSequence, List<Consumer<ActionSequence>>> EVENT_CLUSTER_HANDLERS =
      new ConcurrentHashMap<>();
  private static final Map<Class<? extends Action>, List<Consumer<Action>>> EVENT_HANDLERS =
      new ConcurrentHashMap<>();

  private ActionDispatcher() {}

  public static void dispatch(Action action) {
    List<Consumer<Action>> genericHandlers =
        EVENT_HANDLERS.getOrDefault(Action.class, Collections.emptyList());
    genericHandlers.forEach(handler -> handler.accept(action));
    List<Consumer<Action>> specificHandlers =
        EVENT_HANDLERS.getOrDefault(action.getClass(), Collections.emptyList());
    specificHandlers.forEach(handler -> handler.accept(action));
    action.execute();
    Optional.ofNullable(ON_FINISH_MAP.get(action)).ifPresent(handler -> handler.accept(action));
  }

  public static void dispatchCluster(ActionSequence actionSequence) {
    List<Consumer<ActionSequence>> clusterHandlers =
        EVENT_CLUSTER_HANDLERS.getOrDefault(actionSequence, Collections.emptyList());
    clusterHandlers.forEach(handler -> handler.accept(actionSequence));
    actionSequence.getActions().forEach(ActionDispatcher::dispatch);
    Optional.ofNullable(ON_FINISH_MAP.get(actionSequence))
        .ifPresent(handler -> handler.accept(actionSequence));
  }

  public static void registerOnFinish(Object key, Consumer<Object> onFinish) {
    ON_FINISH_MAP.put(key, onFinish);
  }

  public static void registerHandler(Class<? extends Action> eventClass, Consumer<Action> handler) {
    EVENT_HANDLERS.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(handler);
  }

  public static void registerGenericHandler(Consumer<Action> handler) {
    registerHandler(Action.class, handler);
  }

  public static void registerGenericClusterHandler(
          ActionSequence actionSequence, Consumer<ActionSequence> handler) {
    EVENT_CLUSTER_HANDLERS.computeIfAbsent(actionSequence, k -> new ArrayList<>()).add(handler);
  }
}
