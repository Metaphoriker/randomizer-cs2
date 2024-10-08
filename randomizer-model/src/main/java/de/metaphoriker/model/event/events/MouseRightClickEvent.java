package de.metaphoriker.model.event.events;

import de.metaphoriker.model.event.Event;
import java.awt.event.InputEvent;

public class MouseRightClickEvent extends Event {

  @Override
  public void execute() {
    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
  }

  @Override
  public String description() {
    return "Presses right mouse button";
  }
}
