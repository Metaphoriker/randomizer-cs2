package dev.luzifer.model.event;

import dev.luzifer.model.event.cluster.EventCluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {
    
    private static final Map<Object, Consumer<Object>> ON_FINISH_MAP = new HashMap<>();
    
    private static final Map<EventCluster, List<Consumer<EventCluster>>> EVENT_CLUSTER_HANDLERS = new HashMap<>();
    private static final Map<Class<? extends Event>, List<Consumer<Event>>> EVENT_HANDLERS = new HashMap<>();
    
    public static void dispatch(Event event) {
        
        List<Consumer<Event>> genericHandlers = EVENT_HANDLERS.get(Event.class);
        if(genericHandlers != null)
            genericHandlers.forEach(handler -> handler.accept(event));
        
        List<Consumer<Event>> specificHandlers = EVENT_HANDLERS.get(event.getClass());
        if(specificHandlers != null)
            specificHandlers.forEach(handler -> handler.accept(event));
        
        event.execute();
        
        Consumer<Object> onFinish = ON_FINISH_MAP.get(event);
        if(onFinish != null)
            onFinish.accept(event);
    }
    
    public static void dispatchCluster(EventCluster eventCluster) {
    
        List<Consumer<EventCluster>> handlers = EVENT_CLUSTER_HANDLERS.get(eventCluster);
        if(handlers != null)
            handlers.forEach(handler -> handler.accept(eventCluster));
        
        for(Event event : eventCluster.getEvents())
            dispatch(event);
        
        Consumer<Object> onFinish = ON_FINISH_MAP.get(eventCluster);
        if(onFinish != null)
            onFinish.accept(eventCluster);
    }
    
    public static void registerOnFinish(Object key, Consumer<Object> onFinish) {
        ON_FINISH_MAP.put(key, onFinish);
    }
    
    public static void registerHandler(Class<? extends Event> eventClass, Consumer<Event> handler) {
        if (!EVENT_HANDLERS.containsKey(eventClass)) {
            EVENT_HANDLERS.put(eventClass, new ArrayList<>(Collections.singletonList(handler)));
        } else {
            EVENT_HANDLERS.get(eventClass).add(handler);
        }
    }
    
    public static void registerGenericHandler(Consumer<Event> handler) {
        registerHandler(Event.class, handler);
    }
    
    public static void registerGenericClusterHandler(EventCluster eventCluster, Consumer<EventCluster> handler) {
        if (!EVENT_CLUSTER_HANDLERS.containsKey(eventCluster)) {
            EVENT_CLUSTER_HANDLERS.put(eventCluster, new ArrayList<>(Collections.singletonList(handler)));
        } else {
            EVENT_CLUSTER_HANDLERS.get(eventCluster).add(handler);
        }
    }
    
    private EventDispatcher() {
    }
}
