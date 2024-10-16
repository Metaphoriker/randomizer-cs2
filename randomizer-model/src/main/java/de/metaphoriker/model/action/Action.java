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

@Getter
@Slf4j
@ToString
public abstract class Action implements Cloneable {

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
    int interval = 50;
    int waitedTime = 0;

    if (delayInMillis <= 0) return;

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
