package de.metaphoriker.model.event.custom;

import de.metaphoriker.model.cfg.keybind.KeyBind;
import de.metaphoriker.model.event.Event;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class MouseMoveEvent extends Event {

  public MouseMoveEvent(KeyBind keyBind) {
    super(keyBind);
  }

  @Override
  public String name() {
    return "Mouse Move";
  }

  @Override
  public String description() {
    return "Moves the mouse to a random location on the screen.";
  }

  @Override
  public void execute() {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

    int middleX = dimension.width / 2;
    int middleY = dimension.height / 2;

    int x = ThreadLocalRandom.current().nextInt(middleX - 5, middleX + 5);
    int y = ThreadLocalRandom.current().nextInt(middleY - 5, middleY + 5);

    robot.mouseMove(x, y);
  }
}
