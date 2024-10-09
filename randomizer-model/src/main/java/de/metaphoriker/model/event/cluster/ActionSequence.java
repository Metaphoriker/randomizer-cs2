package de.metaphoriker.model.event.cluster;

import de.metaphoriker.model.event.Action;
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
public class ActionSequence {

  private final String name;
  private final List<Action> actions;

  public ActionSequence(String name, List<Action> actions) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Cluster name cannot be null or empty.");
    }
    if (actions == null) {
      throw new IllegalArgumentException("Events list cannot be null.");
    }

    this.name = name;
    this.actions = Collections.unmodifiableList(new ArrayList<>(actions));
  }

  public static ActionSequence formatEventCluster(String name, String content) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Cluster name cannot be null or empty.");
    }
    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be null or empty.");
    }

    List<Action> actionList = new ArrayList<>();

    String[] events = content.split(";");
    for (String eventString : events) {
      try {
        Action action = JsonUtil.deserialize(eventString);
        actionList.add(action);
      } catch (Exception e) {
        log.error("Failed to deserialize event: {} - {}", eventString, e.getMessage());
      }
    }

    return new ActionSequence(name, actionList);
  }

  @Override
  public String toString() {
    String eventsString = actions.stream().map(Action::toString).collect(Collectors.joining(", "));
    return "EventCluster{name='" + name + "', events=[" + eventsString + "]}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ActionSequence that = (ActionSequence) o;
    return Objects.equals(name, that.name) && Objects.equals(actions, that.actions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, actions);
  }
}
