package dev.luzifer.gui.swing.game.model.api;

import java.awt.event.KeyEvent;

public interface Controllable {

    void onKeyPressed(KeyEvent keyEvent);

    void onKeyReleased(KeyEvent keyEvent);

}
