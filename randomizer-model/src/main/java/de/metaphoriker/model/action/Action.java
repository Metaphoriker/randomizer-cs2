package de.metaphoriker.model.action;

import com.google.inject.Inject;
import de.metaphoriker.model.cfg.keybind.KeyBind;
import java.awt.AWTException;
import java.awt.Robot;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@ToString
public class Action implements Cloneable {

  protected static final Robot robot;
  private static final String EMPTY = "";

  static {
    try {
      robot = new Robot();
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean isMouseWheelEvent(String key) {
    return key != null && key.toUpperCase().startsWith("MWHEEL");
  }

  private static boolean isMouseEvent(String key) {
    return key != null && key.toUpperCase().startsWith("MOUSE");
  }

  @Inject private KeyMapper keyMapper;

  private final transient KeyBind keyBind;
  private final Interval interval = Interval.of(0, 0);

  private transient volatile boolean interrupted = false;
  private transient volatile boolean executing = false;
  private transient volatile Instant expectedEnding = null;

  public Action(KeyBind keyBind) {
    this.keyBind = keyBind;
  }

  public String name() {
    return keyBind.getAction();
  }

  public String description() {
    return EMPTY;
  }

  public void interrupt() {
    interrupted = true;
  }

  public void execute() {
    if (interval.isEmpty()) executeDelayed(0);
    else executeDelayed(ThreadLocalRandom.current().nextInt(interval.getMin(), interval.getMax()));
  }

  public void executeDelayed(long delay) {
    int keyCode = keyMapper.getKeyCodeForKey(keyBind.getKey());
    executing = true;
    interrupted = false;

    if (isMouseWheelEvent(keyBind.getKey())) {
      handleMouseWheelEvent(keyCode);
    } else if (isMouseEvent(keyBind.getKey())) {
      handleMouseEvent(delay, keyCode);
    } else {
      handleKeyEvent(delay, keyCode);
    }

    executing = false;
  }

  private void handleMouseWheelEvent(int keyCode) {
    robot.mouseWheel(keyCode);
  }

  private void handleMouseEvent(long delay, int keyCode) {
    robot.mousePress(keyCode);
    performInterruptibleDelay(delay);
    if (!interrupted) {
      robot.mouseRelease(keyCode);
    } else {
      log.info("Action interrupted, skipping mouse release for mouse: {}", keyBind.getKey());
    }
  }

  private void handleKeyEvent(long delay, int keyCode) {
    if (keyCode != -1) {
      robot.keyPress(keyCode);
      performInterruptibleDelay(delay);
      if (!interrupted) {
        robot.keyRelease(keyCode);
      } else {
        log.info("Action interrupted, skipping key release for key: {}", keyBind.getKey());
      }
    } else {
      log.warn("Key code not found for key: {}", keyBind.getKey());
    }
  }

  private void performInterruptibleDelay(long delay) {
    if (!interval.isEmpty()) {
      expectedEnding = Instant.now().plusMillis(delay);
      interruptibleDelay(delay);
    }
  }

  private void interruptibleDelay(long delayInMillis) {
    int interval = 50;
    int waitedTime = 0;

    while (waitedTime < delayInMillis) {
      if (interrupted) {
        log.info("Delay unterbrochen!");
        return;
      }

      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      waitedTime += interval;
    }
  }

  public void setInterval(Interval interval) {
    this.interval.setMax(interval.getMax());
    this.interval.setMin(interval.getMin());
  }

  @Override
  public Action clone() throws CloneNotSupportedException {
    try {
      Action cloned = (Action) super.clone();
      cloned.setInterval(Interval.of(this.interval.getMin(), this.interval.getMax()));
      return cloned;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
