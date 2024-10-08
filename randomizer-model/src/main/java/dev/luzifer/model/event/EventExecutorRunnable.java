package dev.luzifer.model.event;

import dev.luzifer.model.ApplicationState;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.model.stuff.WhateverThisFuckerIs;
import java.util.concurrent.ThreadLocalRandom;

public class EventExecutorRunnable implements Runnable {

  private static volatile int minWaitTime = 30 * 1000;
  private static volatile int maxWaitTime = 120 * 1000;
  private final EventClusterRepository eventClusterRepository;

  public EventExecutorRunnable(EventClusterRepository eventClusterRepository) {
    this.eventClusterRepository = eventClusterRepository;
  }

  public static void setMaxWaitTime(int maxWaitTime) {
    EventExecutorRunnable.maxWaitTime = maxWaitTime;
  }

  public static void setMinWaitTime(int minWaitTime) {
    EventExecutorRunnable.minWaitTime = minWaitTime;
  }

  @Override
  public void run() {

    while (true) {

      if (WhateverThisFuckerIs.getApplicationState() == ApplicationState.RUNNING
          && !eventClusterRepository.getClusters().isEmpty())
        EventDispatcher.dispatchCluster(
            eventClusterRepository
                .getClusters()
                .get(
                    ThreadLocalRandom.current()
                        .nextInt(0, eventClusterRepository.getClusters().size())));

      try {
        Thread.sleep(ThreadLocalRandom.current().nextInt(minWaitTime, maxWaitTime));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
