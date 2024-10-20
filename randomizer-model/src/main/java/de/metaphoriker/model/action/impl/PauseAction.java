package de.metaphoriker.model.action.impl;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.ActionKey;
import de.metaphoriker.model.config.keybind.KeyBind;
import java.util.concurrent.ThreadLocalRandom;

public class PauseAction extends Action {

  public PauseAction() {
    super("Pause", ActionKey.of(KeyBind.EMPTY_KEY_BIND.getKey()));
  }

  @Override
  protected void performActionStart(int keycode) {
    if (getInterval().isEmpty()) return;

    int min = getInterval().getMin();
    int max = getInterval().getMax();
    int delay = ThreadLocalRandom.current().nextInt(min, max);

    performInterruptibleDelay(delay);
  }

  @Override
  protected void performActionEnd(int keycode) {
    // Da bei einer Pause keine spezifische Aktion am Ende ausgeführt werden muss,
    // bleibt diese Methode leer.
  }
}