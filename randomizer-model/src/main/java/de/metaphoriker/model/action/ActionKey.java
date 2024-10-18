package de.metaphoriker.model.action;

import lombok.Value;

@Value(staticConstructor = "of")
public class ActionKey {

  String key;
}
