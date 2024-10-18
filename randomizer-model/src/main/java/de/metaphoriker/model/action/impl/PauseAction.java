package de.metaphoriker.model.action.impl;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.ActionKey;
import de.metaphoriker.model.config.keybind.Keybind;
import java.util.concurrent.ThreadLocalRandom;

public class PauseAction extends Action {

  public PauseAction() {
    super("Pause", ActionKey.of(Keybind.EMPTY_KEYBIND.getKey()));
  }

  @Override
  public void execute() {
    if (getInterval().isEmpty()) return;

    int min = getInterval().getMin();
    int max = getInterval().getMax();
    try {
      Thread.sleep(ThreadLocalRandom.current().nextInt(min, max));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
