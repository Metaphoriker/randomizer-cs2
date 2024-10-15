package de.metaphoriker.model.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class Interval {

  private int min;
  private int max;

  public boolean isEmpty() {
    return min == 0 && max == 0;
  }
}
