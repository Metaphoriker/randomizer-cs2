package de.metaphoriker.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.Parent;

public class ViewProvider {

  private static final ViewProvider INSTANCE = new ViewProvider();

  private final Map<Class<?>, Parent> viewMap = new ConcurrentHashMap<>();
  private final Map<Class<?>, Runnable> listenerMap = new ConcurrentHashMap<>();
  private final ViewLoader viewLoader = new ViewLoader();

  private ViewProvider() {}

  public static ViewProvider getInstance() {
    return INSTANCE;
  }

  public void registerViewChangeListener(Class<?> viewClass, Runnable listener) {
    listenerMap.put(viewClass, listener);
  }

  public Parent requestView(Class<?> viewClass) {
    checkForViewAnnotation(viewClass);
    return viewMap.computeIfAbsent(
        viewClass,
        vc -> {
          try {
            return viewLoader.loadView(vc);
          } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalStateException("Could not instantiate view: " + vc.getName(), e);
          }
        });
  }

  public void triggerViewChange(Class<?> viewClass) {
    Runnable listener = listenerMap.get(viewClass);
    if (listener != null) {
      listener.run();
    }
  }

  private void checkForViewAnnotation(Class<?> viewClass) {
    if (!viewClass.isAnnotationPresent(View.class)) {
      throw new IllegalArgumentException("Class " + viewClass.getName() + " is not a view.");
    }
  }
}
