package de.metaphoriker.model.event;

import java.util.HashSet;
import java.util.Set;

public class EventRegistry {

  private final Set<Event> events = new HashSet<>();

  public void register(Event event) {
    events.add(event);
  }

  public void unregister(Event event) {
    events.remove(event);
  }

  public Set<Event> getEvents() {
    return new HashSet<>(events);
  }

  public Event getByName(String eventName) {
    return events.stream().filter(event -> eventName.equals(event.name())).findFirst().orElse(null);
  }
}
