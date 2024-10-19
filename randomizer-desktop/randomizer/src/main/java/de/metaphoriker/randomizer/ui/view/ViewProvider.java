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

  /**
   * Registers a listener that responds to changes in the specified view class.
   *
   * @param <T> the type of the view class
   * @param viewClass the class of the view to listen for changes on
   * @param listener the consumer that performs actions when the view changes
   */
  public <T> void registerViewChangeListener(Class<T> viewClass, Consumer<T> listener) {
    listenerMap.put(viewClass, listener);
  }

  /**
   * Requests a view of the specified class, loading it if necessary.
   *
   * @param viewClass the class of the view to request
   * @return a {@link ViewWrapper} containing the requested view and its controller
   * @throws IllegalStateException if the view could not be instantiated
   */
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

  /**
   * Triggers a view change by notifying the registered listener for the specified view class.
   *
   * @param viewClass the class of the view whose change listener should be triggered
   */
  @SuppressWarnings("unchecked")
  public <T> void triggerViewChange(Class<T> viewClass) {
    ViewWrapper<T> viewWrapper = (ViewWrapper<T>) viewMap.get(viewClass);
    if (viewWrapper != null) {
      Consumer<T> listener = (Consumer<T>) listenerMap.get(viewClass);
      if (listener != null) {
        listener.accept(viewWrapper.controller());
      }
    }
  }

  private void checkForViewAnnotation(Class<?> viewClass) {
    if (!viewClass.isAnnotationPresent(View.class)) {
      throw new IllegalArgumentException("Class " + viewClass.getName() + " is not a view.");
    }
  }
}
