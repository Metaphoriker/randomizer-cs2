package de.metaphoriker.model.action.handling;

import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.FocusManager;
import de.metaphoriker.model.action.sequence.ActionSequenceRepository;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.util.concurrent.ThreadLocalRandom;

public class ActionExecutorRunnable implements Runnable {

  private static final Object lock = new Object();
  private static volatile int minWaitTime = 30 * 1000;
  private static volatile int maxWaitTime = 120 * 1000;
  private static volatile boolean waitTimeUpdated = false;

  private final ActionSequenceRepository actionSequenceRepository;
  private final ApplicationContext applicationContext;

  @Inject
  public ActionExecutorRunnable(
      ActionSequenceRepository actionSequenceRepository, ApplicationContext applicationContext) {
    this.actionSequenceRepository = actionSequenceRepository;
    this.applicationContext = applicationContext;
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

        if (applicationContext.getApplicationState() == ApplicationState.RUNNING
            && !actionSequenceRepository.getActionSequences().isEmpty()) {

          ActionDispatcher.dispatchCluster(
              actionSequenceRepository
                  .getActionSequences()
                  .get(
                      ThreadLocalRandom.current()
                          .nextInt(0, actionSequenceRepository.getActionSequences().size())));
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
    if (applicationContext.getApplicationState() == ApplicationState.AWAITING) {
      applicationContext.setApplicationState(ApplicationState.RUNNING);
    } else if (applicationContext.getApplicationState() == ApplicationState.RUNNING
        && !FocusManager.isCs2WindowInFocus()) {
      applicationContext.setApplicationState(ApplicationState.AWAITING);
    }
  }
}
