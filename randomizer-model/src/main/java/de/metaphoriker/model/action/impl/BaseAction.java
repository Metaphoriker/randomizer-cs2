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
      case MOUSE -> ROBOT.mousePress(keyCode);
      case MOUSE_WHEEL -> ROBOT.mouseWheel(keyCode);
      default -> ROBOT.keyPress(keyCode);
    }
  }

  @Override
  protected void performActionEnd(int keyCode) {
    switch (getActionType()) {
      case MOUSE -> ROBOT.mouseRelease(keyCode);
      default -> ROBOT.keyRelease(keyCode);
    }
  }
}
