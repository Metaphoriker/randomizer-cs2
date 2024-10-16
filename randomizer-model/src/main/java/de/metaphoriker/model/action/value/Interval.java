package de.metaphoriker.model.action.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@ToString
public class Interval {

  private int min;
  private int max;

  public boolean isEmpty() {
    return min == 0 && max == 0;
  }
}
