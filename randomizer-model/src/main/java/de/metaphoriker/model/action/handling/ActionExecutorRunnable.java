package de.metaphoriker.model.action.handling;

import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.FocusManager;
import de.metaphoriker.model.action.sequence.ActionSequenceRepository;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
      log.debug("Setze max. Wartezeit auf: {} ms", newMaxWaitTime);
      maxWaitTime = newMaxWaitTime;
      waitTimeUpdated = true;
      lock.notifyAll();
    }
  }

  public static void setMinWaitTime(int newMinWaitTime) {
    synchronized (lock) {
      log.debug("Setze min. Wartezeit auf: {} ms", newMinWaitTime);
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

          log.debug("Dispatching random ActionSequence");
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
          log.debug("Wartezeit vor der nächsten Aktion: {} ms", waitTime);
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
    log.debug("Überprüfe den ApplicationState: {}", currentState);

    if (currentState == ApplicationState.AWAITING && FocusManager.isCs2WindowInFocus()) {
      applicationContext.setApplicationState(ApplicationState.RUNNING);
      log.info("ApplicationState geändert zu: RUNNING");
    } else if (currentState == ApplicationState.RUNNING && !FocusManager.isCs2WindowInFocus()) {
      applicationContext.setApplicationState(ApplicationState.AWAITING);
      log.info("ApplicationState geändert zu: AWAITING");
    }
  }
}