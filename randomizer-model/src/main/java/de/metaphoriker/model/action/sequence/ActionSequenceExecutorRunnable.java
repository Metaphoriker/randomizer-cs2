package de.metaphoriker.model.action.sequence;

import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.FocusManager;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceExecutorRunnable implements Runnable {

  private static final Object lock = new Object();
  private static volatile int minWaitTime = 30 * 1000;
  private static volatile int maxWaitTime = 120 * 1000;
  private static volatile boolean waitTimeUpdated = false;

  private final ActionSequenceRepository actionSequenceRepository;
  private final ApplicationContext applicationContext;

  @Inject
  public ActionSequenceExecutorRunnable(
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

          ActionSequenceDispatcher.dispatchSequence(
              actionSequenceRepository.getActionSequences().stream()
                  .filter(ActionSequence::isActive)
                  .toList()
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
          log.info("Thread wurde unterbrochen, beende..");
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private void handleApplicationState() {
    ApplicationState currentState = applicationContext.getApplicationState();

    if (currentState == ApplicationState.AWAITING && FocusManager.isCs2WindowInFocus()) {
      applicationContext.setApplicationState(ApplicationState.RUNNING);
      log.info("ApplicationState geändert zu: RUNNING");
    } else if (currentState == ApplicationState.RUNNING && !FocusManager.isCs2WindowInFocus()) {
      applicationContext.setApplicationState(ApplicationState.AWAITING);
      log.info("ApplicationState geändert zu: AWAITING");
    }
  }
}
