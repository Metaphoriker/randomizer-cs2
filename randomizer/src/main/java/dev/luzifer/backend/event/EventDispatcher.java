package dev.luzifer.backend.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {
    
    private static final Map<Class<? extends Event>, List<Consumer<Event>>> EVENT_HANDLERS = new HashMap<>();
    
    public static void dispatch(Event event) {
        
        List<Consumer<Event>> genericHandlers = EVENT_HANDLERS.get(Event.class);
        if(genericHandlers != null)
            genericHandlers.forEach(handler -> handler.accept(event));
        
        List<Consumer<Event>> specificHandlers = EVENT_HANDLERS.get(event.getClass());
        if(specificHandlers != null)
            specificHandlers.forEach(handler -> handler.accept(event));
        
        event.execute();
    }
    
    public static void registerHandler(Class<? extends Event> eventClass, Consumer<Event> handler) {
        if (!EVENT_HANDLERS.containsKey(eventClass)) {
            EVENT_HANDLERS.put(eventClass, List.of(handler));
        } else {
            EVENT_HANDLERS.get(eventClass).add(handler);
        }
    }
    
    public static void registerGenericHandler(Consumer<Event> handler) {
        registerHandler(Event.class, handler);
    }
    
    private EventDispatcher() {
    }
}
