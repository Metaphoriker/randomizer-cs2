package de.metaphoriker.model.action.impl;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.ActionKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseAction extends Action {

  public BaseAction(String name, ActionKey actionKey) {
    super(name, actionKey);
  }

  @Override
  protected void performActionStart(int keyCode) {
    switch (getActionType()) {
      case MOUSE -> KNUFFI.mousePress(keyCode);
      case MOUSE_WHEEL -> KNUFFI.mouseWheel(keyCode);
      default -> KNUFFI.keyPress(keyCode);
    }
  }

  @Override
  protected void performActionEnd(int keyCode) {
    switch (getActionType()) {
      case MOUSE -> KNUFFI.mouseRelease(keyCode);
      default -> KNUFFI.keyRelease(keyCode);
    }
  }
}
