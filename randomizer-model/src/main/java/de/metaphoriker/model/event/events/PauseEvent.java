package de.metaphoriker.model.event.events;

import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.Interval;

public class PauseEvent extends Event implements Interval {

  private int min = 250;
  private int max = 250;

  @Override
  public void execute() {
    try {
      Thread.sleep(min());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String description() {
    return "A pause of 250ms";
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
