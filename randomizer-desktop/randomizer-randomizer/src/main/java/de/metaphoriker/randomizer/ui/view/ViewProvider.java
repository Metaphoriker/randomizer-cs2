package de.metaphoriker.randomizer.ui.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViewProvider {

  private final Map<Class<?>, Parent> viewMap = new ConcurrentHashMap<>();
  private final Map<Class<?>, Runnable> listenerMap = new ConcurrentHashMap<>();
  private final ViewLoader viewLoader = new ViewLoader();

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
            log.error("Fehler beim Laden des Fensters: {}", vc.getName(), e);
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
