package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;
import java.awt.event.KeyEvent;

public class JumpEvent extends Event {

  @Override
  public void execute() {

    int key = KeyEvent.VK_SPACE;

    robot.keyPress(key);
    robot.keyRelease(key);
  }

  @Override
  public String description() {
    return "Presses Space to jump";
  }
}
