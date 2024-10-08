package de.metaphoriker.model.event.events;

import de.metaphoriker.model.event.Event;
import java.awt.event.KeyEvent;

public class InteractEvent extends Event {

  @Override
  public void execute() {

    int key = KeyEvent.VK_E;

    robot.keyPress(key);
    robot.keyRelease(key);
  }

  @Override
  public String description() {
    return "touch it";
  }
}
