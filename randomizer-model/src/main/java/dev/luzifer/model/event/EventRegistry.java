package dev.luzifer.model.event;

import java.util.HashSet;
import java.util.Set;

// static abuse, but idc
public class EventRegistry {

  private static final Set<Event> events = new HashSet<>();

  public static void register(Event event) {
    events.add(event);
  }

  public static void unregister(Event event) {
    events.remove(event);
  }

  public static Event getByName(String eventName) {
    return events.stream().filter(event -> eventName.equals(event.name())).findFirst().orElse(null);
  }

  public static Set<Event> getEvents() {
    return events;
  }
}
