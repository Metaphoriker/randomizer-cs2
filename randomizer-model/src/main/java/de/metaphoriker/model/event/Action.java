package de.metaphoriker.model.event;

import de.metaphoriker.model.cfg.keybind.KeyBind;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Action {

  protected static final Robot robot;
  private static final String EMPTY = "";

  static {
    try {
      robot = new Robot();
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }

  private final KeyBind keyBind;

  @Setter private boolean activated;
  @Setter private Interval interval;

  public Action(KeyBind keyBind) {
    this.keyBind = keyBind;
  }

  public String name() {
    return keyBind.getAction();
  }

  public String description() {
    return EMPTY;
  }

  public void execute() {
    int keyCode = mapKeyToKeyCode(keyBind.getKey());

    if (keyCode != -1) {
      robot.keyPress(keyCode);
      if (interval != null) {
        robot.delay(ThreadLocalRandom.current().nextInt(interval.getMin(), interval.getMax()));
      }
      robot.keyRelease(keyCode);
    } else {
      log.warn("Key code not found for key: {}", keyBind.getKey());
    }
  }

  private int mapKeyToKeyCode(String key) {
    return switch (key.toUpperCase()) {
      // Letters
      case "A" -> KeyEvent.VK_A;
      case "B" -> KeyEvent.VK_B;
      case "C" -> KeyEvent.VK_C;
      case "D" -> KeyEvent.VK_D;
      case "E" -> KeyEvent.VK_E;
      case "F" -> KeyEvent.VK_F;
      case "G" -> KeyEvent.VK_G;
      case "H" -> KeyEvent.VK_H;
      case "I" -> KeyEvent.VK_I;
      case "J" -> KeyEvent.VK_J;
      case "K" -> KeyEvent.VK_K;
      case "L" -> KeyEvent.VK_L;
      case "M" -> KeyEvent.VK_M;
      case "N" -> KeyEvent.VK_N;
      case "O" -> KeyEvent.VK_O;
      case "P" -> KeyEvent.VK_P;
      case "Q" -> KeyEvent.VK_Q;
      case "R" -> KeyEvent.VK_R;
      case "S" -> KeyEvent.VK_S;
      case "T" -> KeyEvent.VK_T;
      case "U" -> KeyEvent.VK_U;
      case "V" -> KeyEvent.VK_V;
      case "W" -> KeyEvent.VK_W;
      case "X" -> KeyEvent.VK_X;
      case "Y" -> KeyEvent.VK_Y;
      case "Z" -> KeyEvent.VK_Z;

      // Numbers
      case "0" -> KeyEvent.VK_0;
      case "1" -> KeyEvent.VK_1;
      case "2" -> KeyEvent.VK_2;
      case "3" -> KeyEvent.VK_3;
      case "4" -> KeyEvent.VK_4;
      case "5" -> KeyEvent.VK_5;
      case "6" -> KeyEvent.VK_6;
      case "7" -> KeyEvent.VK_7;
      case "8" -> KeyEvent.VK_8;
      case "9" -> KeyEvent.VK_9;

      // Function keys
      case "F1" -> KeyEvent.VK_F1;
      case "F2" -> KeyEvent.VK_F2;
      case "F3" -> KeyEvent.VK_F3;
      case "F4" -> KeyEvent.VK_F4;
      case "F5" -> KeyEvent.VK_F5;
      case "F6" -> KeyEvent.VK_F6;
      case "F7" -> KeyEvent.VK_F7;
      case "F8" -> KeyEvent.VK_F8;
      case "F9" -> KeyEvent.VK_F9;
      case "F10" -> KeyEvent.VK_F10;
      case "F11" -> KeyEvent.VK_F11;
      case "F12" -> KeyEvent.VK_F12;

      // Modifier keys
      case "CTRL" -> KeyEvent.VK_CONTROL;
      case "SHIFT" -> KeyEvent.VK_SHIFT;
      case "ALT" -> KeyEvent.VK_ALT;
      case "ALTGR" -> KeyEvent.VK_ALT_GRAPH;

      // Navigation keys
      case "UP" -> KeyEvent.VK_UP;
      case "DOWN" -> KeyEvent.VK_DOWN;
      case "LEFT" -> KeyEvent.VK_LEFT;
      case "RIGHT" -> KeyEvent.VK_RIGHT;
      case "PAGE_UP" -> KeyEvent.VK_PAGE_UP;
      case "PAGE_DOWN" -> KeyEvent.VK_PAGE_DOWN;
      case "HOME" -> KeyEvent.VK_HOME;
      case "END" -> KeyEvent.VK_END;
      case "INSERT" -> KeyEvent.VK_INSERT;
      case "DELETE" -> KeyEvent.VK_DELETE;

      // Control keys
      case "ENTER" -> KeyEvent.VK_ENTER;
      case "ESCAPE" -> KeyEvent.VK_ESCAPE;
      case "TAB" -> KeyEvent.VK_TAB;
      case "BACKSPACE" -> KeyEvent.VK_BACK_SPACE;
      case "SPACE" -> KeyEvent.VK_SPACE;
      case "CAPSLOCK" -> KeyEvent.VK_CAPS_LOCK;
      case "PRINTSCREEN" -> KeyEvent.VK_PRINTSCREEN;
      case "SCROLL_LOCK" -> KeyEvent.VK_SCROLL_LOCK;
      case "PAUSE" -> KeyEvent.VK_PAUSE;

      // Numpad keys
      case "NUM0" -> KeyEvent.VK_NUMPAD0;
      case "NUM1" -> KeyEvent.VK_NUMPAD1;
      case "NUM2" -> KeyEvent.VK_NUMPAD2;
      case "NUM3" -> KeyEvent.VK_NUMPAD3;
      case "NUM4" -> KeyEvent.VK_NUMPAD4;
      case "NUM5" -> KeyEvent.VK_NUMPAD5;
      case "NUM6" -> KeyEvent.VK_NUMPAD6;
      case "NUM7" -> KeyEvent.VK_NUMPAD7;
      case "NUM8" -> KeyEvent.VK_NUMPAD8;
      case "NUM9" -> KeyEvent.VK_NUMPAD9;
      case "NUMLOCK" -> KeyEvent.VK_NUM_LOCK;
      case "DIVIDE" -> KeyEvent.VK_DIVIDE;
      case "MULTIPLY" -> KeyEvent.VK_MULTIPLY;
      case "SUBTRACT" -> KeyEvent.VK_SUBTRACT;
      case "ADD" -> KeyEvent.VK_ADD;
      case "DECIMAL" -> KeyEvent.VK_DECIMAL;

      // Symbols
      case "COMMA" -> KeyEvent.VK_COMMA;
      case "PERIOD" -> KeyEvent.VK_PERIOD;
      case "SLASH" -> KeyEvent.VK_SLASH;
      case "SEMICOLON" -> KeyEvent.VK_SEMICOLON;
      case "QUOTE" -> KeyEvent.VK_QUOTE;
      case "OPEN_BRACKET" -> KeyEvent.VK_OPEN_BRACKET;
      case "CLOSE_BRACKET" -> KeyEvent.VK_CLOSE_BRACKET;
      case "BACKSLASH" -> KeyEvent.VK_BACK_SLASH;
      case "MINUS" -> KeyEvent.VK_MINUS;
      case "EQUALS" -> KeyEvent.VK_EQUALS;

      // Mouse keys
      case "MOUSE1" -> InputEvent.BUTTON1_MASK;
      case "MOUSE2" -> InputEvent.BUTTON2_MASK;
      case "MOUSE3" -> InputEvent.BUTTON3_MASK;
      /* TODO: not supported yet
      case "MOUSE4":
        return InputEvent.BUTTON4_MASK;
      case "MOUSE5":
        return InputEvent.BUTTON5_MASK;
       */

      default -> -1;
    };
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Action action = (Action) obj;
    return name().equals(action.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }
}
