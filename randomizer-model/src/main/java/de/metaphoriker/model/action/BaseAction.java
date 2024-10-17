package de.metaphoriker.model.action;

import de.metaphoriker.model.config.keybind.KeyBind;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class BaseAction extends Action {

  public BaseAction(KeyBind keyBind) {
    super(keyBind);
  }

  @Override
  public void execute() {
    if (getInterval().isEmpty()) {
      executeDelayed(0);
    } else {
      executeDelayed(
          ThreadLocalRandom.current().nextInt(getInterval().getMin(), getInterval().getMax()));
    }
  }

  @Override
  public void executeDelayed(long delay) {
    int keyCode = KEY_MAPPER.getKeyCodeForKey(getKeyBind().getKey());
    setExecuting(true);
    setInterrupted(false);

    switch (getActionType()) {
      case MOUSE -> handleMouseEvent(delay, keyCode);
      case MOUSE_WHEEL -> handleMouseWheelEvent(keyCode);
      default -> handleKeyEvent(delay, keyCode);
    }

    setExecuting(false);
  }

  private void handleMouseWheelEvent(int keyCode) {
    ROBOT.mouseWheel(keyCode);
  }

  private void handleMouseEvent(long delay, int keyCode) {
    ROBOT.mousePress(keyCode);
    performInterruptibleDelay(delay);
    if (!isInterrupted()) {
      ROBOT.mouseRelease(keyCode);
    } else {
      log.info("Action interrupted, skipping mouse release for mouse: {}", getKeyBind().getKey());
    }
  }

  private void handleKeyEvent(long delay, int keyCode) {
    if (keyCode != -1) {
      ROBOT.keyPress(keyCode);
      performInterruptibleDelay(delay);
      if (!isInterrupted()) {
        ROBOT.keyRelease(keyCode);
      } else {
        log.info("Action interrupted, skipping key release for key: {}", getKeyBind().getKey());
      }
    } else {
      log.warn("Key code not found for key: {}", getKeyBind().getKey());
    }
  }
}
