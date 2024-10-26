package de.metaphoriker.model.action.sequence;

import de.metaphoriker.model.action.Action;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceDispatcher {

  private static final String ACTION_DISPATCHED = "Action successfully dispatched: {}";
  private static final String SEQUENCE_DISPATCHED = "ActionSequence successfully dispatched: {}";

  private final List<Consumer<ActionSequence>> genericActionSequenceHandlers =
      new CopyOnWriteArrayList<>();
  private final List<Consumer<Action>> genericActionHandlers = new CopyOnWriteArrayList<>();
  private final List<Consumer<Action>> onActionFinishHandlers = new CopyOnWriteArrayList<>();
  private final List<Consumer<ActionSequence>> onActionSequenceFinishHandlers =
      new CopyOnWriteArrayList<>();

  public ActionSequenceDispatcher() {}

  /**
   * Dispatches a single action to all registered handlers and logs its completion.
   *
   * @param action the Action to be dispatched
   */
  private void dispatch(Action action) {
    dispatchToHandlers(action, genericActionHandlers);
    action.execute();
    finishActionProcessing(action);
    log.info(ACTION_DISPATCHED, action);
  }

  /**
   * Dispatches an ActionSequence to registered handlers and processes each contained Action.
   *
   * @param actionSequence the ActionSequence to be dispatched
   */
  public void dispatchSequence(ActionSequence actionSequence) {
    dispatchToHandlers(actionSequence, genericActionSequenceHandlers);
    actionSequence.getActions().forEach(this::dispatch);
    finishSequenceProcessing(actionSequence);
    log.info(SEQUENCE_DISPATCHED, actionSequence);
  }

  private void finishActionProcessing(Action action) {
    onActionFinishHandlers.forEach(handler -> safeAccept(handler, action));
  }

  private void finishSequenceProcessing(ActionSequence actionSequence) {
    onActionSequenceFinishHandlers.forEach(handler -> safeAccept(handler, actionSequence));
  }

  /**
   * Registers a handler to process any finished action.
   *
   * @param handler the Consumer to handle finished actions
   */
  public void registerGenericActionFinishHandler(Consumer<Action> handler) {
    onActionFinishHandlers.add(handler);
  }

  /**
   * Registers a handler to process any finished action sequence.
   *
   * @param handler the Consumer to handle finished action sequences
   */
  public void registerGenericActionSequenceFinishHandler(Consumer<ActionSequence> handler) {
    onActionSequenceFinishHandlers.add(handler);
  }

  /**
   * Registers a generic handler for Action events.
   *
   * @param handler the Consumer to process Action events
   */
  public void registerGenericActionHandler(Consumer<Action> handler) {
    genericActionHandlers.add(handler);
  }

  /**
   * Registers a generic handler for ActionSequence events.
   *
   * @param handler the Consumer to process ActionSequence events
   */
  public void registerGenericSequenceHandler(Consumer<ActionSequence> handler) {
    genericActionSequenceHandlers.add(handler);
  }

  private <T> void dispatchToHandlers(T item, List<Consumer<T>> handlers) {
    handlers.forEach(handler -> safeAccept(handler, item));
  }

  private <T> void safeAccept(Consumer<T> handler, T item) {
    try {
      handler.accept(item);
    } catch (Exception e) {
      log.error("Error while handling item: {}", item, e);
    }
  }
}
