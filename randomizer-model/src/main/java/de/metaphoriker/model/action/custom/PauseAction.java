package de.metaphoriker.model.action.custom;

import de.metaphoriker.model.cfg.keybind.KeyBind;
import de.metaphoriker.model.action.Action;
import java.util.concurrent.ThreadLocalRandom;

public class PauseAction extends Action {

  public PauseAction(KeyBind keyBind) {
    super(keyBind);
  }

  @Override
  public String name() {
    return "Pause";
  }

  @Override
  public String description() {
    return "Pauses the execution for a random amount of time.";
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
