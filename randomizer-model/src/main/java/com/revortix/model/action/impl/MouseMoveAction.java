package com.revortix.model.action.impl;

import com.revortix.model.action.Action;
import com.revortix.model.action.ActionKey;
import com.revortix.model.config.keybind.KeyBind;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * The {@code MouseMoveAction} class represents an action that simulates mouse movement. This action
 * randomly moves the mouse pointer within a small range around its current position either
 * horizontally or vertically.
 *
 * <p>This class extends from the {@link Action} class and provides specific implementations for
 * starting and stopping the mouse move action.
 *
 * <p>The action has a default name "Mouse move" and uses a key bind represented by an {@link
 * ActionKey} created from an unbound key.
 */
@Slf4j
public class MouseMoveAction extends Action {

  public MouseMoveAction() {
    super("Mouse move", ActionKey.of(KeyBind.EMPTY_KEY_BIND.getKey()));
  }

  @Override
  protected void performActionStart(int keycode) {
    try {
      Point currentPosition = MouseInfo.getPointerInfo().getLocation();
      int currentX = currentPosition.x;
      int currentY = currentPosition.y;

      boolean moveHorizontally = ThreadLocalRandom.current().nextBoolean();
      Supplier<Integer> randomInt =
          () -> {
            if (moveHorizontally) {
              return ThreadLocalRandom.current().nextInt(currentX - 1, currentX + 510);
            } else {
              return ThreadLocalRandom.current().nextInt(currentY - 1, currentY + 510);
            }
          };

      int x = moveHorizontally ? randomInt.get() : currentX;
      int y = moveHorizontally ? currentY : randomInt.get();

      log.debug("Moving mouse to coordinates: (" + x + ", " + y + ")");
      KNUFFI.mouseMove(x, y);
    } catch (IllegalArgumentException e) {
      log.error("Error during mouse move", e);
    }
  }

  @Override
  protected void performActionEnd(int keycode) {
    // Für MouseMoveAction gibt es keine "End"-Aktion, daher bleibt diese Methode leer.
  }
}
