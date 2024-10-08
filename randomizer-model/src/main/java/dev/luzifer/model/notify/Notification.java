package dev.luzifer.model.notify;

public class Notification {

  private final Class<?> notifier;
  private final String key;

  public Notification(Class<?> notifier, String key) {
    this.notifier = notifier;
    this.key = key;
  }

  public Class<?> getNotifier() {
    return notifier;
  }

  public String getKey() {
    return key;
  }
}
