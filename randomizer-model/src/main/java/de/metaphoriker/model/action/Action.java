package de.metaphoriker.model.action;

import de.metaphoriker.model.cfg.keybind.KeyBind;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Action {

  protected static final Robot robot;
  private static final String EMPTY = "";

  private static final Map<String, Integer> stringToKeyCodeMap = new HashMap<>();
  private static final Map<Integer, String> keyCodeToStringMap = new HashMap<>();

  static {
    try {
      robot = new Robot();
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }

    // Letters
    addMapping("A", KeyEvent.VK_A);
    addMapping("B", KeyEvent.VK_B);
    addMapping("C", KeyEvent.VK_C);
    addMapping("D", KeyEvent.VK_D);
    addMapping("E", KeyEvent.VK_E);
    addMapping("F", KeyEvent.VK_F);
    addMapping("G", KeyEvent.VK_G);
    addMapping("H", KeyEvent.VK_H);
    addMapping("I", KeyEvent.VK_I);
    addMapping("J", KeyEvent.VK_J);
    addMapping("K", KeyEvent.VK_K);
    addMapping("L", KeyEvent.VK_L);
    addMapping("M", KeyEvent.VK_M);
    addMapping("N", KeyEvent.VK_N);
    addMapping("O", KeyEvent.VK_O);
    addMapping("P", KeyEvent.VK_P);
    addMapping("Q", KeyEvent.VK_Q);
    addMapping("R", KeyEvent.VK_R);
    addMapping("S", KeyEvent.VK_S);
    addMapping("T", KeyEvent.VK_T);
    addMapping("U", KeyEvent.VK_U);
    addMapping("V", KeyEvent.VK_V);
    addMapping("W", KeyEvent.VK_W);
    addMapping("X", KeyEvent.VK_X);
    addMapping("Y", KeyEvent.VK_Y);
    addMapping("Z", KeyEvent.VK_Z);

    // Numbers
    addMapping("0", KeyEvent.VK_0);
    addMapping("1", KeyEvent.VK_1);
    addMapping("2", KeyEvent.VK_2);
    addMapping("3", KeyEvent.VK_3);
    addMapping("4", KeyEvent.VK_4);
    addMapping("5", KeyEvent.VK_5);
    addMapping("6", KeyEvent.VK_6);
    addMapping("7", KeyEvent.VK_7);
    addMapping("8", KeyEvent.VK_8);
    addMapping("9", KeyEvent.VK_9);

    // Function keys
    addMapping("F1", KeyEvent.VK_F1);
    addMapping("F2", KeyEvent.VK_F2);
    addMapping("F3", KeyEvent.VK_F3);
    addMapping("F4", KeyEvent.VK_F4);
    addMapping("F5", KeyEvent.VK_F5);
    addMapping("F6", KeyEvent.VK_F6);
    addMapping("F7", KeyEvent.VK_F7);
    addMapping("F8", KeyEvent.VK_F8);
    addMapping("F9", KeyEvent.VK_F9);
    addMapping("F10", KeyEvent.VK_F10);
    addMapping("F11", KeyEvent.VK_F11);
    addMapping("F12", KeyEvent.VK_F12);

    // Modifier keys
    addMapping("CTRL", KeyEvent.VK_CONTROL);
    addMapping("SHIFT", KeyEvent.VK_SHIFT);
    addMapping("ALT", KeyEvent.VK_ALT);
    addMapping("ALTGR", KeyEvent.VK_ALT_GRAPH);

    // Navigation keys
    addMapping("UP", KeyEvent.VK_UP);
    addMapping("DOWN", KeyEvent.VK_DOWN);
    addMapping("LEFT", KeyEvent.VK_LEFT);
    addMapping("RIGHT", KeyEvent.VK_RIGHT);
    addMapping("PAGE_UP", KeyEvent.VK_PAGE_UP);
    addMapping("PAGE_DOWN", KeyEvent.VK_PAGE_DOWN);
    addMapping("HOME", KeyEvent.VK_HOME);
    addMapping("END", KeyEvent.VK_END);
    addMapping("INSERT", KeyEvent.VK_INSERT);
    addMapping("DELETE", KeyEvent.VK_DELETE);

    // Control keys
    addMapping("ENTER", KeyEvent.VK_ENTER);
    addMapping("ESCAPE", KeyEvent.VK_ESCAPE);
    addMapping("TAB", KeyEvent.VK_TAB);
    addMapping("BACKSPACE", KeyEvent.VK_BACK_SPACE);
    addMapping("SPACE", KeyEvent.VK_SPACE);
    addMapping("CAPSLOCK", KeyEvent.VK_CAPS_LOCK);
    addMapping("PRINTSCREEN", KeyEvent.VK_PRINTSCREEN);
    addMapping("SCROLL_LOCK", KeyEvent.VK_SCROLL_LOCK);
    addMapping("PAUSE", KeyEvent.VK_PAUSE);

    // Mouse keys
    addMapping("MOUSE1", InputEvent.getMaskForButton(1));
    addMapping("MOUSE2", InputEvent.getMaskForButton(2));
    addMapping("MOUSE3", InputEvent.getMaskForButton(3));
    addMapping("MOUSE4", InputEvent.getMaskForButton(4));
    addMapping("MOUSE5", InputEvent.getMaskForButton(5));
  }

  // Helper method to add mappings in both directions
  private static void addMapping(String key, int keyCode) {
    stringToKeyCodeMap.put(key.toUpperCase(), keyCode);
    keyCodeToStringMap.put(keyCode, key.toUpperCase());
  }

  private static boolean isMouseEvent(String key) {
    return key != null && key.toUpperCase().startsWith("MOUSE");
  }

  public static int getKeyCodeForKey(String key) {
    return stringToKeyCodeMap.getOrDefault(key.toUpperCase(), -1);
  }

  public static String getKeyForKeyCode(int keyCode) {
    return keyCodeToStringMap.getOrDefault(keyCode, EMPTY);
  }

  private final KeyBind keyBind;
  private final Interval interval = Interval.of(0, 0);

  private transient volatile boolean interrupted = false;
  private transient volatile boolean executing = false;
  private transient volatile Instant expectedEnding = null;

  public Action(KeyBind keyBind) {
    this.keyBind = keyBind;
  }

  public String name() {
    return keyBind.getAction();
  }

  public String description() {
    return EMPTY;
  }

  public void interrupt() {
    interrupted = true;
  }

  public void execute() {
    if (interval.isEmpty()) executeDelayed(0);
    else executeDelayed(ThreadLocalRandom.current().nextInt(interval.getMin(), interval.getMax()));
  }

  public void executeDelayed(long delay) {
    int keyCode = getKeyCodeForKey(keyBind.getKey());
    executing = true;
    interrupted = false;

    if (isMouseEvent(keyBind.getKey())) {
      handleMouseEvent(delay, keyCode);
    } else {
      handleKeyEvent(delay, keyCode);
    }

    executing = false;
  }

  private void handleMouseEvent(long delay, int keyCode) {
    robot.mousePress(keyCode);
    performInterruptibleDelay(delay);
    if (!interrupted) {
      robot.mouseRelease(keyCode);
    } else {
      log.info("Action interrupted, skipping mouse release for mouse: {}", keyBind.getKey());
    }
  }

  private void handleKeyEvent(long delay, int keyCode) {
    if (keyCode != -1) {
      robot.keyPress(keyCode);
      performInterruptibleDelay(delay);
      if (!interrupted) {
        robot.keyRelease(keyCode);
      } else {
        log.info("Action interrupted, skipping key release for key: {}", keyBind.getKey());
      }
    } else {
      log.warn("Key code not found for key: {}", keyBind.getKey());
    }
  }

  private void performInterruptibleDelay(long delay) {
    if (!interval.isEmpty()) {
      expectedEnding = Instant.now().plusMillis(delay);
      interruptibleDelay(delay);
    }
  }

  private void interruptibleDelay(long delayInMillis) {
    int interval = 50;
    int waitedTime = 0;

    while (waitedTime < delayInMillis) {
      if (interrupted) {
        log.info("Delay unterbrochen!");
        return;
      }

      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      waitedTime += interval;
    }
  }

  public void setInterval(Interval interval) {
    this.interval.setMax(interval.getMax());
    this.interval.setMin(interval.getMin());
  }
}
