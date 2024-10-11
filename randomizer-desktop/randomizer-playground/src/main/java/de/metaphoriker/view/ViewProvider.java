package de.metaphoriker.view;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.Parent;

public class ViewProvider {

  private static final ViewProvider INSTANCE = new ViewProvider();

  private final Map<Class<?>, Parent> viewMap = new ConcurrentHashMap<>();
  private final ViewLoader viewLoader = new ViewLoader();

  private ViewProvider() {}

  public static ViewProvider getInstance() {
    return INSTANCE;
  }

  public Parent requestView(Class<?> viewClass) {
    checkForViewAnnotation(viewClass);
    return viewMap.computeIfAbsent(
        viewClass,
        vc -> {
          try {
            return viewLoader.loadView(
                vc,
                _ -> {
                  try {
                    return vc.getDeclaredConstructor().newInstance();
                  } catch (InstantiationException
                      | IllegalAccessException
                      | InvocationTargetException
                      | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                  }
                });
          } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate view: " + vc.getName(), e);
          }
        });
  }

  private void checkForViewAnnotation(Class<?> viewClass) {
    if (!viewClass.isAnnotationPresent(View.class)) {
      throw new IllegalArgumentException("Class " + viewClass.getName() + " is not a view.");
    }
  }
}
