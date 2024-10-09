package de.metaphoriker.model.cfg.keybind;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "key")
public class KeyBind {

  public static final KeyBind EMPTY_KEYBIND = new KeyBind("<unbound>", "none");

  private String key;
  private String action;
}
