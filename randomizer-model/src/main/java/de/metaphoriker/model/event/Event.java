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
public class Event {

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

  public Event(KeyBind keyBind) {
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
    switch (key.toUpperCase()) {
      // Letters
      case "A":
        return KeyEvent.VK_A;
      case "B":
        return KeyEvent.VK_B;
      case "C":
        return KeyEvent.VK_C;
      case "D":
        return KeyEvent.VK_D;
      case "E":
        return KeyEvent.VK_E;
      case "F":
        return KeyEvent.VK_F;
      case "G":
        return KeyEvent.VK_G;
      case "H":
        return KeyEvent.VK_H;
      case "I":
        return KeyEvent.VK_I;
      case "J":
        return KeyEvent.VK_J;
      case "K":
        return KeyEvent.VK_K;
      case "L":
        return KeyEvent.VK_L;
      case "M":
        return KeyEvent.VK_M;
      case "N":
        return KeyEvent.VK_N;
      case "O":
        return KeyEvent.VK_O;
      case "P":
        return KeyEvent.VK_P;
      case "Q":
        return KeyEvent.VK_Q;
      case "R":
        return KeyEvent.VK_R;
      case "S":
        return KeyEvent.VK_S;
      case "T":
        return KeyEvent.VK_T;
      case "U":
        return KeyEvent.VK_U;
      case "V":
        return KeyEvent.VK_V;
      case "W":
        return KeyEvent.VK_W;
      case "X":
        return KeyEvent.VK_X;
      case "Y":
        return KeyEvent.VK_Y;
      case "Z":
        return KeyEvent.VK_Z;

      // Numbers
      case "0":
        return KeyEvent.VK_0;
      case "1":
        return KeyEvent.VK_1;
      case "2":
        return KeyEvent.VK_2;
      case "3":
        return KeyEvent.VK_3;
      case "4":
        return KeyEvent.VK_4;
      case "5":
        return KeyEvent.VK_5;
      case "6":
        return KeyEvent.VK_6;
      case "7":
        return KeyEvent.VK_7;
      case "8":
        return KeyEvent.VK_8;
      case "9":
        return KeyEvent.VK_9;

      // Function keys
      case "F1":
        return KeyEvent.VK_F1;
      case "F2":
        return KeyEvent.VK_F2;
      case "F3":
        return KeyEvent.VK_F3;
      case "F4":
        return KeyEvent.VK_F4;
      case "F5":
        return KeyEvent.VK_F5;
      case "F6":
        return KeyEvent.VK_F6;
      case "F7":
        return KeyEvent.VK_F7;
      case "F8":
        return KeyEvent.VK_F8;
      case "F9":
        return KeyEvent.VK_F9;
      case "F10":
        return KeyEvent.VK_F10;
      case "F11":
        return KeyEvent.VK_F11;
      case "F12":
        return KeyEvent.VK_F12;

      // Modifier keys
      case "CTRL":
        return KeyEvent.VK_CONTROL;
      case "SHIFT":
        return KeyEvent.VK_SHIFT;
      case "ALT":
        return KeyEvent.VK_ALT;
      case "ALTGR":
        return KeyEvent.VK_ALT_GRAPH;

      // Navigation keys
      case "UP":
        return KeyEvent.VK_UP;
      case "DOWN":
        return KeyEvent.VK_DOWN;
      case "LEFT":
        return KeyEvent.VK_LEFT;
      case "RIGHT":
        return KeyEvent.VK_RIGHT;
      case "PAGE_UP":
        return KeyEvent.VK_PAGE_UP;
      case "PAGE_DOWN":
        return KeyEvent.VK_PAGE_DOWN;
      case "HOME":
        return KeyEvent.VK_HOME;
      case "END":
        return KeyEvent.VK_END;
      case "INSERT":
        return KeyEvent.VK_INSERT;
      case "DELETE":
        return KeyEvent.VK_DELETE;

      // Control keys
      case "ENTER":
        return KeyEvent.VK_ENTER;
      case "ESCAPE":
        return KeyEvent.VK_ESCAPE;
      case "TAB":
        return KeyEvent.VK_TAB;
      case "BACKSPACE":
        return KeyEvent.VK_BACK_SPACE;
      case "SPACE":
        return KeyEvent.VK_SPACE;
      case "CAPSLOCK":
        return KeyEvent.VK_CAPS_LOCK;
      case "PRINTSCREEN":
        return KeyEvent.VK_PRINTSCREEN;
      case "SCROLL_LOCK":
        return KeyEvent.VK_SCROLL_LOCK;
      case "PAUSE":
        return KeyEvent.VK_PAUSE;

      // Numpad keys
      case "NUM0":
        return KeyEvent.VK_NUMPAD0;
      case "NUM1":
        return KeyEvent.VK_NUMPAD1;
      case "NUM2":
        return KeyEvent.VK_NUMPAD2;
      case "NUM3":
        return KeyEvent.VK_NUMPAD3;
      case "NUM4":
        return KeyEvent.VK_NUMPAD4;
      case "NUM5":
        return KeyEvent.VK_NUMPAD5;
      case "NUM6":
        return KeyEvent.VK_NUMPAD6;
      case "NUM7":
        return KeyEvent.VK_NUMPAD7;
      case "NUM8":
        return KeyEvent.VK_NUMPAD8;
      case "NUM9":
        return KeyEvent.VK_NUMPAD9;
      case "NUMLOCK":
        return KeyEvent.VK_NUM_LOCK;
      case "DIVIDE":
        return KeyEvent.VK_DIVIDE;
      case "MULTIPLY":
        return KeyEvent.VK_MULTIPLY;
      case "SUBTRACT":
        return KeyEvent.VK_SUBTRACT;
      case "ADD":
        return KeyEvent.VK_ADD;
      case "DECIMAL":
        return KeyEvent.VK_DECIMAL;

      // Symbols
      case "COMMA":
        return KeyEvent.VK_COMMA;
      case "PERIOD":
        return KeyEvent.VK_PERIOD;
      case "SLASH":
        return KeyEvent.VK_SLASH;
      case "SEMICOLON":
        return KeyEvent.VK_SEMICOLON;
      case "QUOTE":
        return KeyEvent.VK_QUOTE;
      case "OPEN_BRACKET":
        return KeyEvent.VK_OPEN_BRACKET;
      case "CLOSE_BRACKET":
        return KeyEvent.VK_CLOSE_BRACKET;
      case "BACKSLASH":
        return KeyEvent.VK_BACK_SLASH;
      case "MINUS":
        return KeyEvent.VK_MINUS;
      case "EQUALS":
        return KeyEvent.VK_EQUALS;

      // Mouse keys
      case "MOUSE1":
        return InputEvent.BUTTON1_MASK;
      case "MOUSE2":
        return InputEvent.BUTTON2_MASK;
      case "MOUSE3":
        return InputEvent.BUTTON3_MASK;
      /* TODO: not supported yet
      case "MOUSE4":
        return InputEvent.BUTTON4_MASK;
      case "MOUSE5":
        return InputEvent.BUTTON5_MASK;
       */

      default:
        return -1;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Event event = (Event) obj;
    return name().equals(event.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }
}
