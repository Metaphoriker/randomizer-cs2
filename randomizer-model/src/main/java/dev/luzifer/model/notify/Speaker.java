package dev.luzifer.model.notify;

import java.util.ArrayList;
import java.util.List;

// Im getting used to static abuse
public class Speaker {

  private static final List<NotificationListener> listeners = new ArrayList<>();

  private Speaker() {}

  public static void addListener(NotificationListener listener) {
    listeners.add(listener);
  }

  public static void notify(Notification notification) {
    listeners.forEach(listener -> listener.onNotification(notification));
  }
}
