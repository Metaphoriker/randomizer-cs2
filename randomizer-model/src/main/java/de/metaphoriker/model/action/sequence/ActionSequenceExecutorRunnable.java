package de.metaphoriker.model.action.sequence;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationContext;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;
import de.metaphoriker.model.util.FocusManager;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;

/**
 * The ActionSequenceExecutorRunnable class is a specialized implementation of the Runnable
 * interface, responsible for executing sequences of actions based on certain conditions and events.
 *
 * <p>The main functionality includes: - Registering native hooks for mouse and keyboard events. -
 * Handling action interruptions when specific keys or mouse buttons are released. - Managing the
 * lifecycle and execution state of action sequences. - Controlling the wait times between action
 * sequences, and dynamically updating wait times. - Ensuring application state consistency in
 * relation to the focus state of the CS2 window.
 *
 * <p>Singleton variables: - minWaitTime: The minimum wait time in milliseconds between the
 * execution of action sequences. - maxWaitTime: The maximum wait time in milliseconds between the
 * execution of action sequences. - waitTimeUpdated: A flag to indicate that the wait times have
 * been updated.
 *
 * <p>Methods include: - setMinWaitTime: Updates the minimum wait time. - setMaxWaitTime: Updates
 * the maximum wait time. - registerNativeHookListenerForEachKeyBind: Registers native hooks for
 * listening to mouse and keyboard events. - processNativeEvent: Processes the native mouse and
 * keyboard events. - isActionSequenceInactive: Checks if the current action sequence is inactive. -
 * getCurrentExecutingAction: Retrieves the currently executing action in the current action
 * sequence. - handleCurrentActionInterruption: Handles interruptions of the current action. - run:
 * The main loop that controls the execution flow of action sequences. - isWaitTimeExceeded: Checks
 * if the wait time between actions has been exceeded. - isApplicationRunning: Checks if the
 * application is in a running state. - processCurrentActionSequence: Processes the current action
 * sequence if any key has been released. - findInterruptedAction: Finds any action that has been
 * interrupted. - executeDelayedActionIfNeeded: Executes any delayed actions if needed. -
 * calculateRemainingWaitTime: Calculates the remaining wait time for the current action. -
 * chooseAndDispatchRandomSequence: Chooses and dispatches a random active action sequence. -
 * resetWaitTimeIfUpdated: Resets the wait time if it has been updated. - updateWaitTime: Updates
 * the wait time for the next action sequence execution. - handleApplicationState: Manages
 * application state changes in relation to the CS2 window focus.
 */
@Slf4j
public class ActionSequenceExecutorRunnable implements Runnable {

  private static final String KEY_RELEASE_LOG = "Interruption erkannt mit {}: {}";

  private static volatile int minWaitTime = 30 * 1000;
  private static volatile int maxWaitTime = 120 * 1000;
  private static volatile boolean waitTimeUpdated = false;

  public static void setMinWaitTime(int minWaitTime) {
    ActionSequenceExecutorRunnable.minWaitTime = minWaitTime;
    waitTimeUpdated = true;
  }

  public static void setMaxWaitTime(int maxWaitTime) {
    ActionSequenceExecutorRunnable.maxWaitTime = maxWaitTime;
    waitTimeUpdated = true;
  }

  @Inject private ActionSequenceRepository actionSequenceRepository;
  @Inject private ApplicationContext applicationContext;
  @Inject private ActionSequenceDispatcher actionSequenceDispatcher;

  private volatile ActionSequence currentActionSequence;
  private volatile long lastCycle;
  private volatile int lastWaitTime;
  private volatile boolean hasReleasedAnyKey = false;

  {
    registerNativeHookListenerForEachKeyBind();
  }

  private void registerNativeHookListenerForEachKeyBind() {
    log.info("Registering native key and mouse listener");

    GlobalScreen.addNativeMouseListener(
        new NativeMouseListener() {
          @Override
          public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
            processNativeEvent(null, nativeEvent.getButton(), null);
          }
        });

    GlobalScreen.addNativeKeyListener(
        new NativeKeyListener() {
          @Override
          public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            processNativeEvent(
                NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()), null, nativeEvent);
          }
        });
  }

  private void processNativeEvent(
      String keyText, Integer mouseButton, NativeKeyEvent nativeKeyEvent) {
    if (isActionSequenceInactive()) {
      return;
    }

    Action currentAction = getCurrentExecutingAction();
    if (currentAction != null) {
      handleCurrentActionInterruption(keyText, mouseButton, nativeKeyEvent, currentAction);
    }
  }

  private boolean isActionSequenceInactive() {
    return currentActionSequence == null || !currentActionSequence.isActive();
  }

  private Action getCurrentExecutingAction() {
    return currentActionSequence.getActions().stream()
        .filter(Action::isExecuting)
        .findFirst()
        .orElse(null);
  }

  private void handleCurrentActionInterruption(
      String keyText, Integer mouseButton, NativeKeyEvent nativeKeyEvent, Action currentAction) {
    String actionKey = currentAction.getActionKey().getKey();
    boolean isKeyBindMatched =
        actionKey.equalsIgnoreCase(keyText)
            || (mouseButton != null && actionKey.equals(String.valueOf(mouseButton)));

    if (isKeyBindMatched) {
      hasReleasedAnyKey = true;
      currentAction.interrupt();
      log.info(KEY_RELEASE_LOG, nativeKeyEvent != null ? "Key" : "Mousebutton", actionKey);
    }
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      if (!hasReleasedAnyKey && !waitTimeUpdated && !isWaitTimeExceeded()) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        continue;
      }
      if (FocusManager.isCs2WindowInFocus()) {
        handleApplicationState();
        if (isApplicationRunning() && !actionSequenceRepository.getActionSequences().isEmpty()) {
          if (processCurrentActionSequence()) continue;
          chooseAndDispatchRandomSequence();
        }
      }
      resetWaitTimeIfUpdated();
      updateWaitTime();
    }
  }

  private boolean isWaitTimeExceeded() {
    return Instant.now().toEpochMilli() - lastCycle >= lastWaitTime;
  }

  private boolean isApplicationRunning() {
    return applicationContext.getApplicationState() == ApplicationState.RUNNING;
  }

  private boolean processCurrentActionSequence() {
    if (hasReleasedAnyKey) {
      hasReleasedAnyKey = false;
      if (currentActionSequence == null || !currentActionSequence.isActive()) return true;
      Action currentAction = findInterruptedAction();
      if (currentAction != null && executeDelayedActionIfNeeded(currentAction)) return true;
      int remainingWaitTime = calculateRemainingWaitTime();
      if (remainingWaitTime > 0) {
        lastCycle = Instant.now().toEpochMilli();
        return true;
      }
    }
    return false;
  }

  private Action findInterruptedAction() {
    return currentActionSequence.getActions().stream()
        .filter(Action::isInterrupted)
        .findFirst()
        .orElse(null);
  }

  private boolean executeDelayedActionIfNeeded(Action currentAction) {
    if (currentAction.getInterval().isEmpty()) return false;
    Instant now = Instant.now();
    Instant delayedAt = currentAction.getExpectedEnding();
    if (delayedAt != null) {
      long remainingTimeMs = delayedAt.toEpochMilli() - now.toEpochMilli();
      if (remainingTimeMs > 0) {
        log.debug("Führe verzögerte Aktion aus für {} ms", remainingTimeMs);
        currentAction.executeWithDelay(remainingTimeMs);
        return true;
      }
    }
    return false;
  }

  private int calculateRemainingWaitTime() {
    return Math.max(0, lastWaitTime - (int) (Instant.now().toEpochMilli() - lastCycle));
  }

  private void chooseAndDispatchRandomSequence() {
    List<ActionSequence> sequences =
        actionSequenceRepository.getActionSequences().stream()
            .filter(ActionSequence::isActive)
            .toList();
    if (!sequences.isEmpty()) {
      currentActionSequence =
          sequences.get(ThreadLocalRandom.current().nextInt(0, sequences.size()));
      actionSequenceDispatcher.dispatchSequence(currentActionSequence);
    } else {
      log.warn("Keine aktiven ActionSequences gefunden.");
    }
  }

  private void resetWaitTimeIfUpdated() {
    if (waitTimeUpdated) {
      waitTimeUpdated = false;
    }
  }

  private void updateWaitTime() {
    int waitTime = ThreadLocalRandom.current().nextInt(minWaitTime, maxWaitTime);
    lastCycle = Instant.now().toEpochMilli();
    lastWaitTime = waitTime;
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
