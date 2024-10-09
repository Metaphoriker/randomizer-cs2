package de.metaphoriker.model.event;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public class FocusManager {

  public static boolean isCs2WindowInFocus() {
    User32 user32 = User32.INSTANCE;
    HWND hwnd = user32.GetForegroundWindow();
    if (hwnd == null) {
      return false;
    }

    char[] windowText = new char[512];
    user32.GetWindowText(hwnd, windowText, 512);
    String wText = Native.toString(windowText);

    return wText.contains("Counter-Strike 2");
  }
}
