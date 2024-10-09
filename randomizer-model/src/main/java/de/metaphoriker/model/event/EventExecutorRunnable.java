package de.metaphoriker.model.event;

import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.event.cluster.EventClusterRepository;
import de.metaphoriker.model.stuff.WhateverThisFuckerIs;
import java.util.concurrent.ThreadLocalRandom;

public class EventExecutorRunnable implements Runnable {

  private static volatile int minWaitTime = 30 * 1000;
  private static volatile int maxWaitTime = 120 * 1000;
  private final EventClusterRepository eventClusterRepository;

  private static volatile boolean waitTimeUpdated = false;

  public EventExecutorRunnable(EventClusterRepository eventClusterRepository) {
    this.eventClusterRepository = eventClusterRepository;
  }

  public static void setMaxWaitTime(int newMaxWaitTime) {
    maxWaitTime = newMaxWaitTime;
    waitTimeUpdated = true;
    notifyThread();
  }

  public static void setMinWaitTime(int newMinWaitTime) {
    minWaitTime = newMinWaitTime;
    waitTimeUpdated = true;
    notifyThread();
  }

  private static void notifyThread() {
    synchronized (EventExecutorRunnable.class) {
      EventExecutorRunnable.class.notifyAll();
    }
  }

  @Override
  public void run() {
    while (true) {
      if (FocusManager.isCs2WindowInFocus()) {
        if (WhateverThisFuckerIs.getApplicationState() == ApplicationState.AWAITING)
          WhateverThisFuckerIs.setApplicationState(ApplicationState.RUNNING);

        if (WhateverThisFuckerIs.getApplicationState() == ApplicationState.RUNNING
            && !eventClusterRepository.getClusters().isEmpty()) {
          EventDispatcher.dispatchCluster(
              eventClusterRepository
                  .getClusters()
                  .get(
                      ThreadLocalRandom.current()
                          .nextInt(0, eventClusterRepository.getClusters().size())));
        }
      } else {
        if (WhateverThisFuckerIs.getApplicationState() == ApplicationState.RUNNING)
          WhateverThisFuckerIs.setApplicationState(ApplicationState.AWAITING);
      }

      synchronized (EventExecutorRunnable.class) {
        if (waitTimeUpdated) {
          waitTimeUpdated = false;
        }
        int waitTime = ThreadLocalRandom.current().nextInt(minWaitTime, maxWaitTime);
        try {
          EventExecutorRunnable.class.wait(waitTime);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
