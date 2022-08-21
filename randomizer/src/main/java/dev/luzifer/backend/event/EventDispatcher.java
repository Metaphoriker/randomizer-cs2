package dev.luzifer.backend.event;

import dev.luzifer.backend.event.events.DropWeaponEvent;
import dev.luzifer.backend.event.events.MouseLeftClickEvent;
import dev.luzifer.backend.event.events.MouseMoveEvent;
import dev.luzifer.backend.event.events.CrouchEvent;
import dev.luzifer.backend.event.events.MoveEvent;
import dev.luzifer.backend.event.events.ReloadEvent;
import dev.luzifer.backend.event.events.ShiftEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {
    
    private static final EventCluster[] EVENT_CLUSTERS = new EventCluster[] {
            new EventCluster(new MouseLeftClickEvent(), new ReloadEvent()),
            new EventCluster(new DropWeaponEvent(), new MoveEvent()),
            new EventCluster(new DropWeaponEvent(), new CrouchEvent()),
            new EventCluster(new MouseMoveEvent(), new MouseMoveEvent(), new MouseMoveEvent()),
            new EventCluster(new MoveEvent(), new MouseLeftClickEvent()),
            new EventCluster(new MoveEvent(), new MouseLeftClickEvent(), new ReloadEvent()),
            new EventCluster(new MouseMoveEvent(), new MouseLeftClickEvent()),
            new EventCluster(new ShiftEvent(), new CrouchEvent()),
            new EventCluster(new MouseMoveEvent(), new MouseMoveEvent(), new MouseMoveEvent(), new MouseMoveEvent())
    };
    
    private static final Map<Event, Boolean> EVENT_REGISTRY = new HashMap<>();
    private static final Map<Class<? extends Event>, List<Consumer<Event>>> EVENT_HANDLERS = new HashMap<>();
    
    public static void registerEvent(Event event) {
        EVENT_REGISTRY.put(event, true);
    }
    
    public static void enableEvent(Event event) {
        EVENT_REGISTRY.put(event, true);
    }
    
    public static void disableEvent(Event event) {
        EVENT_REGISTRY.put(event, false);
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
    
    public static void dispatch(Event event) {
        
        List<Consumer<Event>> genericHandlers = EVENT_HANDLERS.get(Event.class);
        if(genericHandlers != null)
            genericHandlers.forEach(handler -> handler.accept(event));
        
        List<Consumer<Event>> specificHandlers = EVENT_HANDLERS.get(event.getClass());
        if(specificHandlers != null)
            specificHandlers.forEach(handler -> handler.accept(event));
        
        event.execute();
    }
    
    public static boolean isEnabled(Event event) {
        return EVENT_REGISTRY.containsKey(event) && EVENT_REGISTRY.get(event);
    }
    
    public static boolean isDisabled(Event event) {
        return !isEnabled(event);
    }
    
    public static List<Event> getRegisteredEvents() {
        return sortedEvents();
    }
    
    public static EventCluster[] getEventClusters() {
        return EVENT_CLUSTERS;
    }
    
    private static List<Event> sortedEvents() {
        
        List<Event> list = new ArrayList<>(EVENT_REGISTRY.keySet());
        list.sort(Comparator.comparing(o -> o.getClass().getSimpleName().length()));
        
        return new ArrayList<>(EVENT_REGISTRY.keySet());
    }
    
    private EventDispatcher() {
    }
}
