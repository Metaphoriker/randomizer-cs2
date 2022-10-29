package dev.luzifer.gui.swing.game.model.api;

import dev.luzifer.gui.swing.game.model.location.Facing;

public interface Moveable {

    void moveUp();

    void moveDown();

    void moveRight();

    void moveLeft();

    void moveTowardsFacing(Facing facing);

    void moveAwayFromFacing(Facing facing);

}
