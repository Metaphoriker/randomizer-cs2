package de.metaphoriker.model.action.sequence;

import de.metaphoriker.model.action.Action;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceDispatcher {

  private final Map<Object, Consumer<Object>> onFinishMap = new ConcurrentHashMap<>();
  private final Map<Class<? extends ActionSequence>, List<Consumer<ActionSequence>>>
      actionSequenceHandlers = new ConcurrentHashMap<>();
  private final Map<Class<? extends Action>, List<Consumer<Action>>> actionHandlers =
      new ConcurrentHashMap<>();

  public ActionSequenceDispatcher() {}

  private void dispatch(Action action) {
    dispatchActionToHandlers(
        action, actionHandlers.getOrDefault(Action.class, Collections.emptyList()));
    dispatchActionToHandlers(
        action, actionHandlers.getOrDefault(action.getClass(), Collections.emptyList()));
    action.execute();
    Optional.ofNullable(onFinishMap.get(action)).ifPresent(handler -> handler.accept(action));
    log.info("Action erfolgreich dispatched: {}", action);
  }

  public void dispatchSequence(ActionSequence actionSequence) {
    dispatchActionSequenceToHandlers(
        actionSequence,
        actionSequenceHandlers.getOrDefault(actionSequence.getClass(), Collections.emptyList()));
    dispatchActionSequenceToHandlers(
        actionSequence,
        actionSequenceHandlers.getOrDefault(ActionSequence.class, Collections.emptyList()));
    actionSequence.getActions().forEach(this::dispatch);
    Optional.ofNullable(onFinishMap.get(actionSequence))
        .ifPresent(handler -> handler.accept(actionSequence));
    log.info("ActionSequence erfolgreich dispatched: {}", actionSequence);
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

  private void dispatchActionToHandlers(Action action, List<Consumer<Action>> handlers) {
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

  private void dispatchActionSequenceToHandlers(
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
