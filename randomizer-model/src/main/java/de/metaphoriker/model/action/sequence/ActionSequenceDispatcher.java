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

  /**
   * Dispatches an action sequence to the appropriate handlers, processes each action, and performs
   * any final handling or logging necessary upon completion.
   *
   * @param actionSequence the sequence of actions to be dispatched and processed
   */
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

  /**
   * Registers an on-finish handler for a specific key.
   *
   * @param key the key associated with the on-finish handler
   * @param onFinish the handler to be executed when the action associated with the key finishes
   */
  public void registerOnFinish(Object key, Consumer<Object> onFinish) {
    onFinishMap.put(key, onFinish);
  }

  /**
   * Registers an action handler for a specific type of action.
   *
   * @param actionClass The class of the action to handle.
   * @param handler The handler to process the action.
   */
  public void registerActionHandler(Class<? extends Action> actionClass, Consumer<Action> handler) {
    actionHandlers.computeIfAbsent(actionClass, _ -> new ArrayList<>()).add(handler);
  }

  /**
   * Registers a generic action handler that processes all types of actions.
   *
   * @param handler the handler that will process actions of any type.
   */
  public void registerGenericActionHandler(Consumer<Action> handler) {
    registerActionHandler(Action.class, handler);
  }

  /**
   * Registers a handler for a specific type of ActionSequence.
   *
   * <p>The handler will be invoked whenever an ActionSequence of the specified type is dispatched.
   *
   * @param actionSequenceClass the class representing the specific type of ActionSequence to be
   *     handled
   * @param handler a Consumer that defines the handling logic for the specified type of
   *     ActionSequence
   */
  public void registerSequenceHandler(
      Class<? extends ActionSequence> actionSequenceClass, Consumer<ActionSequence> handler) {
    actionSequenceHandlers
        .computeIfAbsent(actionSequenceClass, _ -> new ArrayList<>())
        .add(handler);
  }

  /**
   * Registers a generic handler for ActionSequence events.
   *
   * @param handler The Consumer to process ActionSequence objects.
   */
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
