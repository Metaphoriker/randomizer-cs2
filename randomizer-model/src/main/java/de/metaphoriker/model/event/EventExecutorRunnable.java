package de.metaphoriker.model.event;

import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.event.cluster.EventClusterRepository;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.util.concurrent.ThreadLocalRandom;

public class EventExecutorRunnable implements Runnable {

  private static final Object lock = new Object();
  private static volatile int minWaitTime = 30 * 1000;
  private static volatile int maxWaitTime = 120 * 1000;
  private final EventClusterRepository eventClusterRepository;
  private static volatile boolean waitTimeUpdated = false;

  public EventExecutorRunnable(EventClusterRepository eventClusterRepository) {
    this.eventClusterRepository = eventClusterRepository;
  }

  public static void setMaxWaitTime(int newMaxWaitTime) {
    synchronized (lock) {
      maxWaitTime = newMaxWaitTime;
      waitTimeUpdated = true;
      lock.notifyAll();
    }
  }

  public static void setMinWaitTime(int newMinWaitTime) {
    synchronized (lock) {
      minWaitTime = newMinWaitTime;
      waitTimeUpdated = true;
      lock.notifyAll();
    }
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      if (FocusManager.isCs2WindowInFocus()) {
        handleApplicationState();

        if (ApplicationContext.getApplicationState() == ApplicationState.RUNNING
            && !eventClusterRepository.getClusters().isEmpty()) {

          EventDispatcher.dispatchCluster(
              eventClusterRepository
                  .getClusters()
                  .get(
                      ThreadLocalRandom.current()
                          .nextInt(0, eventClusterRepository.getClusters().size())));
        }
      }

      synchronized (lock) {
        try {
          if (waitTimeUpdated) {
            waitTimeUpdated = false;
          }

          int waitTime = ThreadLocalRandom.current().nextInt(minWaitTime, maxWaitTime);
          lock.wait(waitTime);

        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private void handleApplicationState() {
    if (ApplicationContext.getApplicationState() == ApplicationState.AWAITING) {
      ApplicationContext.setApplicationState(ApplicationState.RUNNING);
    } else if (ApplicationContext.getApplicationState() == ApplicationState.RUNNING
        && !FocusManager.isCs2WindowInFocus()) {
      ApplicationContext.setApplicationState(ApplicationState.AWAITING);
    }
  }
}
