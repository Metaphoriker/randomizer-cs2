package dev.luzifer.event;

import dev.luzifer.event.events.CrouchEvent;
import dev.luzifer.event.events.DropWeaponEvent;
import dev.luzifer.event.events.EscapeEvent;
import dev.luzifer.event.events.JumpEvent;
import dev.luzifer.event.events.MouseLeftClickEvent;
import dev.luzifer.event.events.MouseMoveEvent;
import dev.luzifer.event.events.MouseRightClickEvent;
import dev.luzifer.event.events.MoveEvent;
import dev.luzifer.event.events.ReloadEvent;
import dev.luzifer.event.events.ShiftEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {
    
    private static final Event[] EVENTS = new Event[]{
            new DropWeaponEvent(),
            new JumpEvent(),
            new MouseLeftClickEvent(),
            new MouseRightClickEvent(),
            new MouseMoveEvent(),
            new MoveEvent(),
            new ShiftEvent(),
            new CrouchEvent(),
            new EscapeEvent(),
            new ReloadEvent()
    };
    
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
    
    private static final Map<Class<? extends Event>, List<Consumer<Event>>> EVENT_HANDLERS = new HashMap<>(EVENTS.length);
    
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
    
    public static Event[] getEvents() {
        return EVENTS;
    }
    
    public static EventCluster[] getEventClusters() {
        return EVENT_CLUSTERS;
    }
}
