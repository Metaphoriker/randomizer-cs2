package de.metaphoriker.model.event.handling;

import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.cluster.EventCluster;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventDispatcher {

  private static final Map<Object, Consumer<Object>> ON_FINISH_MAP = new ConcurrentHashMap<>();
  private static final Map<EventCluster, List<Consumer<EventCluster>>> EVENT_CLUSTER_HANDLERS =
      new ConcurrentHashMap<>();
  private static final Map<Class<? extends Event>, List<Consumer<Event>>> EVENT_HANDLERS =
      new ConcurrentHashMap<>();

  private EventDispatcher() {}

  public static void dispatch(Event event) {
    List<Consumer<Event>> genericHandlers =
        EVENT_HANDLERS.getOrDefault(Event.class, Collections.emptyList());
    genericHandlers.forEach(handler -> handler.accept(event));
    List<Consumer<Event>> specificHandlers =
        EVENT_HANDLERS.getOrDefault(event.getClass(), Collections.emptyList());
    specificHandlers.forEach(handler -> handler.accept(event));
    event.execute();
    Optional.ofNullable(ON_FINISH_MAP.get(event)).ifPresent(handler -> handler.accept(event));
  }

  public static void dispatchCluster(EventCluster eventCluster) {
    List<Consumer<EventCluster>> clusterHandlers =
        EVENT_CLUSTER_HANDLERS.getOrDefault(eventCluster, Collections.emptyList());
    clusterHandlers.forEach(handler -> handler.accept(eventCluster));
    eventCluster.getEvents().forEach(EventDispatcher::dispatch);
    Optional.ofNullable(ON_FINISH_MAP.get(eventCluster))
        .ifPresent(handler -> handler.accept(eventCluster));
  }

  public static void registerOnFinish(Object key, Consumer<Object> onFinish) {
    ON_FINISH_MAP.put(key, onFinish);
  }

  public static void registerHandler(Class<? extends Event> eventClass, Consumer<Event> handler) {
    EVENT_HANDLERS.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(handler);
  }

  public static void registerGenericHandler(Consumer<Event> handler) {
    registerHandler(Event.class, handler);
  }

  public static void registerGenericClusterHandler(
      EventCluster eventCluster, Consumer<EventCluster> handler) {
    EVENT_CLUSTER_HANDLERS.computeIfAbsent(eventCluster, k -> new ArrayList<>()).add(handler);
  }
}
