package de.metaphoriker.model.action;

import de.metaphoriker.model.action.mapper.KeyMapper;
import de.metaphoriker.model.action.value.Interval;
import de.metaphoriker.model.config.keybind.KeyBind;
import java.awt.AWTException;
import java.awt.Robot;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * The Action class serves as an abstract base for defining specific actions that can be performed,
 * such as key presses, mouse movements, and delays. Implementations of this class should override
 * the {@link #execute()} method to define the specific behavior of the action.
 *
 * <p>The class provides mechanisms to handle interruptions and delays in an interruptible manner,
 * as well as cloning capabilities to create identical copies of an action.
 *
 * <p>The class also associates each action with a specific {@link KeyBind} and maintains an
 * execution interval.
 */
@Getter
@Slf4j
@ToString
public abstract class Action implements Cloneable {

  private static final int INTERVAL = 50;
  protected static final KeyMapper KEY_MAPPER = new KeyMapper();

  protected static final Robot ROBOT;

  static {
    try {
      ROBOT = new Robot();
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }

  private final transient KeyBind keyBind;
  private final transient ActionType actionType;

  private final String name;
  private final Interval interval = Interval.of(0, 0);

  @Setter(AccessLevel.PROTECTED)
  private transient volatile boolean interrupted = false;

  @Setter(AccessLevel.PROTECTED)
  private transient volatile boolean executing = false;

  @Setter(AccessLevel.PROTECTED)
  private transient volatile Instant expectedEnding = null;

  public Action(KeyBind keyBind) {
    this.keyBind = keyBind;
    this.actionType =
        isMouseEvent()
            ? ActionType.MOUSE
            : isMouseWheelEvent()
                ? ActionType.MOUSE_WHEEL
                : hasKey() ? ActionType.KEYBOARD : ActionType.CUSTOM;
    this.name = keyBind.getAction();
  }

  public void executeDelayed(long delay) {}

  public void setInterval(Interval interval) {
    this.interval.setMax(interval.getMax());
    this.interval.setMin(interval.getMin());
  }

  public void interrupt() {
    interrupted = true;
  }

  protected void performInterruptibleDelay(long delay) {
    if (!getInterval().isEmpty()) {
      expectedEnding = Instant.now().plusMillis(delay);
      interruptibleDelay(delay);
    }
  }

  private void interruptibleDelay(long delayInMillis) {
    int waitedTime = 0;

    if (delayInMillis <= 0) return;

    while (waitedTime < delayInMillis) {
      if (interrupted) {
        log.info("Delay unterbrochen!");
        return;
      }

      try {
        Thread.sleep(INTERVAL);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      waitedTime += INTERVAL;
    }
  }

  private boolean isMouseWheelEvent() {
    return keyBind.getKey().toUpperCase().startsWith("MWHEEL");
  }

  private boolean isMouseEvent() {
    return keyBind.getKey().toUpperCase().startsWith("MOUSE");
  }

  private boolean hasKey() {
    return !keyBind.getKey().equals(KeyBind.EMPTY_KEYBIND.getKey());
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

  public abstract void execute();
}
