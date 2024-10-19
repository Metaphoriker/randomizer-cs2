package de.metaphoriker.model.action.sequence;

import de.metaphoriker.model.action.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ActionSequence {

  @EqualsAndHashCode.Include private final String name;
  boolean active = true;
  @EqualsAndHashCode.Include private final List<Action> actions = new ArrayList<>();
  private String description = "";

  public ActionSequence(String name) {
    this.name = name;
  }

  public void setActions(List<Action> actions) {
    this.actions.clear();
    this.actions.addAll(actions);
  }

  @Override
  public String toString() {
    String eventsString = actions.stream().map(Action::toString).collect(Collectors.joining(", "));
    return "ActionSequence{name='" + name + "', actions=[" + eventsString + "]}";
  }
}
