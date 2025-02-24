package com.revortix.model.action.impl;

import com.revortix.model.action.Action;
import com.revortix.model.action.ActionKey;
import com.revortix.model.config.keybind.KeyBind;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MouseMoveAction extends Action {

  private static final int SMOOTH_STEPS = 20; // Anzahl der Schritte für die Bewegung
  // Entferne SMOOTH_DELAY, da wir jetzt KNUFFI verwenden
  private static final int MAX_MOVE_DISTANCE = 100; //  Maximale Distanz in Pixeln

  public MouseMoveAction() {
    super("Mouse move", ActionKey.of(KeyBind.EMPTY_KEY_BIND.getKey()));
  }

  @Override
  protected void performActionStart(int keycode) {
    try {
      Point startPosition = MouseInfo.getPointerInfo().getLocation();
      int startX = startPosition.x;
      int startY = startPosition.y;

      // Zufällige Zielkoordinaten innerhalb des MAX_MOVE_DISTANCE Radius
      int deltaX = ThreadLocalRandom.current().nextInt(-MAX_MOVE_DISTANCE, MAX_MOVE_DISTANCE + 1);
      int deltaY = ThreadLocalRandom.current().nextInt(-MAX_MOVE_DISTANCE, MAX_MOVE_DISTANCE + 1);

      int endX = startX + deltaX;
      int endY = startY + deltaY;

      // Stelle sicher, dass die neuen Koordinaten im Bildschirmbereich liegen
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      endX = Math.max(0, Math.min(endX, screenSize.width - 1));
      endY = Math.max(0, Math.min(endY, screenSize.height - 1));

      log.debug("Moving mouse smoothly from ({}, {}) to ({}, {})", startX, startY, endX, endY);
      smoothMove(startX, startY, endX, endY);

    } catch (Exception e) { // Fange *alle* Exceptions, da KNUFFI unbekannte Exceptions werfen könnte.
      log.error("Error during smooth mouse move", e);
    }
  }

  @Override
  protected void performActionEnd(int keycode) {
    // Keine Aktion erforderlich
  }

  private void smoothMove(int startX, int startY, int endX, int endY) {
    double dx = (endX - startX) / (double) SMOOTH_STEPS;
    double dy = (endY - startY) / (double) SMOOTH_STEPS;

    for (int step = 1; step <= SMOOTH_STEPS; step++) {
      int x = (int) Math.round(startX + dx * step);
      int y = (int) Math.round(startY + dy * step);
      KNUFFI.mouseMove(x, y); // Verwende KNUFFI für die Mausbewegung
      // Kein Thread.sleep() mehr!
    }
    KNUFFI.mouseMove(endX, endY); // Gehe zur Sicherheit nochmal zur endgültigen Position.
  }
}