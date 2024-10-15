package de.metaphoriker.model.action.sequence;

import de.metaphoriker.model.action.Action;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceDispatcher {

  private static final String ACTION_DISPATCHED = "Action erfolgreich dispatched: {}";
  private static final String SEQUENCE_DISPATCHED = "ActionSequence erfolgreich dispatched: {}";

  private final Map<Object, Consumer<Object>> onFinishMap = new ConcurrentHashMap<>();
  private final Map<Class<? extends ActionSequence>, List<Consumer<ActionSequence>>>
      actionSequenceHandlers = new ConcurrentHashMap<>();
  private final Map<Class<? extends Action>, List<Consumer<Action>>> actionHandlers =
      new ConcurrentHashMap<>();

  public ActionSequenceDispatcher() {}

  private void dispatch(Action action) {
    dispatchToRelevantHandlers(action, actionHandlers);
    action.execute();
    handleOnFinish(action);
    log.info(ACTION_DISPATCHED, action);
  }

  public void dispatchSequence(ActionSequence actionSequence) {
    dispatchToRelevantHandlers(actionSequence, actionSequenceHandlers);
    actionSequence.getActions().forEach(this::dispatch);
    handleOnFinish(actionSequence);
    log.info(SEQUENCE_DISPATCHED, actionSequence);
  }

  private <T> void dispatchToRelevantHandlers(
      T item, Map<Class<? extends T>, List<Consumer<T>>> handlersMap) {
    dispatchToHandlers(item, handlersMap.getOrDefault(item.getClass(), Collections.emptyList()));
    dispatchToHandlers(
        item,
        handlersMap.getOrDefault(
            (Class<? extends T>) item.getClass().getSuperclass(), Collections.emptyList()));
  }

  private <T> void handleOnFinish(T item) {
    Optional.ofNullable(onFinishMap.get(item)).ifPresent(handler -> handler.accept(item));
  }

  public void registerOnFinish(Object key, Consumer<Object> onFinish) {
    onFinishMap.put(key, onFinish);
  }

  public void registerActionHandler(Class<? extends Action> actionClass, Consumer<Action> handler) {
    actionHandlers.computeIfAbsent(actionClass, _ -> new ArrayList<>()).add(handler);
  }

  public void registerGenericActionHandler(Consumer<Action> handler) {
    registerActionHandler(Action.class, handler);
  }

  public void registerSequenceHandler(
      Class<? extends ActionSequence> actionSequenceClass, Consumer<ActionSequence> handler) {
    actionSequenceHandlers
        .computeIfAbsent(actionSequenceClass, _ -> new ArrayList<>())
        .add(handler);
  }

  public void registerGenericSequenceHandler(Consumer<ActionSequence> handler) {
    registerSequenceHandler(ActionSequence.class, handler);
  }

  private <T> void dispatchToHandlers(T item, List<Consumer<T>> handlers) {
    for (Consumer<T> handler : handlers) {
      try {
        handler.accept(item);
      } catch (Exception e) {
        log.error("Error while handling item: {}", item, e);
      }
    }
  }
}
