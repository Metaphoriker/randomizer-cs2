package de.metaphoriker.model.config.keybind;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "key")
@ToString
public class KeyBind {

  public static final KeyBind EMPTY_KEYBIND = new KeyBind("<unbound>", "none");

  private String key;
  private String action;
}
