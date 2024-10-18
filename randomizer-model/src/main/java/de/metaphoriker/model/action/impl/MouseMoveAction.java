package de.metaphoriker.model.action.impl;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.ActionKey;
import de.metaphoriker.model.config.keybind.Keybind;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class MouseMoveAction extends Action {

  public MouseMoveAction() {
    super("Mouse move", ActionKey.of(Keybind.EMPTY_KEYBIND.getKey()));
  }

  @Override
  public void execute() {
    Point currentPosition = MouseInfo.getPointerInfo().getLocation();

    int currentX = currentPosition.x;
    int currentY = currentPosition.y;

    boolean moveHorizontally = ThreadLocalRandom.current().nextBoolean();

    Supplier<Integer> randomInt =
        () ->
            moveHorizontally
                ? ThreadLocalRandom.current().nextInt(currentX - 5, currentX + 6)
                : ThreadLocalRandom.current().nextInt(currentY - 5, currentY + 6);

    int x = moveHorizontally ? randomInt.get() : currentX;
    int y = moveHorizontally ? currentY : randomInt.get();

    ROBOT.mouseMove(x, y);
  }
}
