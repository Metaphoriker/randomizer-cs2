package de.metaphoriker.model.event.events;

import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.Interval;
import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class CrouchEvent extends Event implements Interval {

  private int min = 1000;
  private int max = 5000;

  @Override
  public void execute() {

    int key = KeyEvent.VK_CONTROL;

    robot.keyPress(key);
    robot.delay(ThreadLocalRandom.current().nextInt(min(), max()));
    robot.keyRelease(key);
  }

  @Override
  public String description() {
    return "Presses CTRL to crouch";
  }

  @Override
  public int min() {
    return min;
  }

  @Override
  public int max() {
    return max;
  }

  @Override
  public void setMin(int min) {
    this.min = min;
  }

  @Override
  public void setMax(int max) {
    this.max = max;
  }
}
