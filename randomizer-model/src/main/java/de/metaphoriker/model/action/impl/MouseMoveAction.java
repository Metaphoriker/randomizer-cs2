package de.metaphoriker.model.action.impl;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.ActionKey;
import de.metaphoriker.model.config.keybind.KeyBind;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * The {@code MouseMoveAction} class represents an action that simulates mouse movement. This action randomly moves the
 * mouse pointer within a small range around its current position either horizontally or vertically.
 *
 * <p>This class extends from the {@link Action} class and provides specific implementations for
 * starting and stopping the mouse move action.
 *
 * <p>The action has a default name "Mouse move" and uses a key bind represented by an {@link
 * ActionKey} created from an unbound key.
 */
public class MouseMoveAction extends Action {

    public MouseMoveAction() {
        super("Mouse move", ActionKey.of(KeyBind.EMPTY_KEY_BIND.getKey()));
    }

    @Override
    protected void performActionStart(int keycode) {
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

        KNUFFI.mouseMove(x, y);
    }

    @Override
    protected void performActionEnd(int keycode) {
        // Für MouseMoveAction gibt es keine "End"-Aktion, daher bleibt diese Methode leer.
    }
}
