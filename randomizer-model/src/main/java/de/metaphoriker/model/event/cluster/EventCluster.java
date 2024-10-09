package de.metaphoriker.model.event.cluster;

import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.json.JsonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class EventCluster {

  private final String name;
  private final List<Event> events;

  public EventCluster(String name, List<Event> events) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Cluster name cannot be null or empty.");
    }
    if (events == null) {
      throw new IllegalArgumentException("Events list cannot be null.");
    }

    this.name = name;
    this.events = Collections.unmodifiableList(new ArrayList<>(events));
  }

  public static EventCluster formatEventCluster(String name, String content) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Cluster name cannot be null or empty.");
    }
    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be null or empty.");
    }

    List<Event> eventList = new ArrayList<>();

    String[] events = content.split(";");
    for (String eventString : events) {
      try {
        Event event = JsonUtil.deserialize(eventString);
        eventList.add(event);
      } catch (Exception e) {
        log.error("Failed to deserialize event: {} - {}", eventString, e.getMessage());
      }
    }

    return new EventCluster(name, eventList);
  }

  @Override
  public String toString() {
    String eventsString = events.stream().map(Event::toString).collect(Collectors.joining(", "));
    return "EventCluster{name='" + name + "', events=[" + eventsString + "]}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventCluster that = (EventCluster) o;
    return Objects.equals(name, that.name) && Objects.equals(events, that.events);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, events);
  }
}
