package de.metaphoriker.model.event;

import java.awt.*;
import lombok.Getter;

@Getter
public abstract class Event {

  protected static final Robot robot;
  private static final String EMPTY = "";

  static {
    try {
      robot = new Robot();
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean activated;

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public String name() {
    return getClass().getSimpleName();
  }

  public String description() {
    return EMPTY;
  }

  public abstract void execute();

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Event event = (Event) obj;
    return name().equals(event.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }
}
