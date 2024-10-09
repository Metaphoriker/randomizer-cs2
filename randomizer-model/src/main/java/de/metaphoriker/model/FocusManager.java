package de.metaphoriker.model;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FocusManager {

  public static boolean isCs2WindowInFocus() {
    try {
      User32 user32 = User32.INSTANCE;
      HWND hwnd = user32.GetForegroundWindow();

      if (hwnd == null) {
        return false;
      }

      char[] windowText = new char[512];
      user32.GetWindowText(hwnd, windowText, 512);
      String wText = Native.toString(windowText);

      return wText.contains("Counter-Strike 2");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("JNA is not properly set up. Error: " + e.getMessage());
      log.error("JNA is not properly set up", e);
      return false;
    } catch (Exception e) {
      System.err.println("Unexpected error while checking window focus: " + e.getMessage());
      log.error("Error while checking for cs2 focus", e);
      return false;
    }
  }
}
