package com.revortix.model.action.sequence;

import com.revortix.model.action.Action;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
public class ActionSequenceDispatcher {

    private static final String ACTION_DISPATCHED = "Action successfully dispatched: {}";
    private static final String SEQUENCE_DISPATCHED = "ActionSequence successfully dispatched: {}";

    private final List<Consumer<ActionSequence>> sequenceHandlers = new CopyOnWriteArrayList<>();
    private final List<Consumer<Action>> actionHandlers = new CopyOnWriteArrayList<>();
    private final List<Consumer<Action>> actionFinishHandlers = new CopyOnWriteArrayList<>();
    private final List<Consumer<ActionSequence>> actionSequenceFinishHandlers =
            new CopyOnWriteArrayList<>();

    public ActionSequenceDispatcher() {
    }

    /**
     * Dispatches a single action to all registered handlers and logs its completion.
     *
     * @param action the Action to be dispatched
     */
    private void dispatch(Action action) {
        dispatchToHandlers(action, actionHandlers);
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
        dispatchToHandlers(actionSequence, sequenceHandlers);
        actionSequence.getActions().forEach(this::dispatch);
        finishSequenceProcessing(actionSequence);
        log.info(SEQUENCE_DISPATCHED, actionSequence);
    }

    private void finishActionProcessing(Action action) {
        actionFinishHandlers.forEach(handler -> safeAccept(handler, action));
    }

    private void finishSequenceProcessing(ActionSequence actionSequence) {
        actionSequenceFinishHandlers.forEach(handler -> safeAccept(handler, actionSequence));
    }

    /**
     * Registers a handler to process any finished action.
     *
     * @param handler the Consumer to handle finished actions
     */
    public void registerActionFinishHandler(Consumer<Action> handler) {
        actionFinishHandlers.add(handler);
    }

    /**
     * Registers a handler to process any finished action sequence.
     *
     * @param handler the Consumer to handle finished action sequences
     */
    public void registerSequenceFinishHandler(Consumer<ActionSequence> handler) {
        actionSequenceFinishHandlers.add(handler);
    }

    /**
     * Registers a generic handler for Action events.
     *
     * @param handler the Consumer to process Action events
     */
    public void registerActionHandler(Consumer<Action> handler) {
        actionHandlers.add(handler);
    }

    /**
     * Registers a generic handler for ActionSequence events.
     *
     * @param handler the Consumer to process ActionSequence events
     */
    public void registerSequenceHandler(Consumer<ActionSequence> handler) {
        sequenceHandlers.add(handler);
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
