package de.metaphoriker.randomizer.ui.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViewProvider {

  private final Map<Class<?>, ViewWrapper<?>> viewMap = new ConcurrentHashMap<>();
  private final Map<Class<?>, Consumer<?>> listenerMap = new ConcurrentHashMap<>();
  private final ViewLoader viewLoader = new ViewLoader();

  public <T> void registerViewChangeListener(Class<T> viewClass, Consumer<T> listener) {
    listenerMap.put(viewClass, listener);
  }

  @SuppressWarnings("unchecked")
  public <T> ViewWrapper<T> requestView(Class<T> viewClass) {
    checkForViewAnnotation(viewClass);
    return (ViewWrapper<T>)
        viewMap.computeIfAbsent(
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

  @SuppressWarnings("unchecked")
  public <T> void triggerViewChange(Class<T> viewClass) {
    ViewWrapper<T> viewWrapper = (ViewWrapper<T>) viewMap.get(viewClass);
    if (viewWrapper != null) {
      Consumer<T> listener = (Consumer<T>) listenerMap.get(viewClass);
      if (listener != null) {
        listener.accept(viewWrapper.getController());
      }
    }
  }

  private void checkForViewAnnotation(Class<?> viewClass) {
    if (!viewClass.isAnnotationPresent(View.class)) {
      throw new IllegalArgumentException("Class " + viewClass.getName() + " is not a view.");
    }
  }
}
