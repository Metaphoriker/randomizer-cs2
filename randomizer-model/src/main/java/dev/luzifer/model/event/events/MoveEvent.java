package dev.luzifer.model.event.events;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.Interval;
import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class MoveEvent extends Event implements Interval {

  private int min = 40;
  private int max = 120;

  @Override
  public void execute() {

    int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D};
    int key = keys[ThreadLocalRandom.current().nextInt(0, keys.length)];

    int amount = ThreadLocalRandom.current().nextInt(1, 5);
    int index = 0;

    while (index++ < amount) {

      robot.keyPress(key);
      robot.delay(ThreadLocalRandom.current().nextInt(min(), max()));
      robot.keyRelease(key);
    }
  }

  @Override
  public String description() {
    return "Presses either W-A-S-D 1~5 times to move";
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
