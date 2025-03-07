package com.revortix.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import com.revortix.randomizer.bootstrap.RandomizerUpdater;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class NavigationBarViewModel {

  private final RandomizerUpdater randomizerUpdater;

  private final ObjectProperty<Class<?>> selectedView = new SimpleObjectProperty<>();

  @Inject
  public NavigationBarViewModel(RandomizerUpdater randomizerUpdater) {
    this.randomizerUpdater = randomizerUpdater;
  }

  public void setSelectedView(Class<?> viewClass) {
    this.selectedView.set(viewClass);
  }

  public boolean isUpdateAvailable() {
    boolean isUpdateAvailable = randomizerUpdater.isRandomizerUpdateAvailable();
    log.info("Update available: {}", isUpdateAvailable ? "YES" : "NO");
    return isUpdateAvailable;
  }

  public void runUpdater() {
    randomizerUpdater.runUpdaterIfNeeded();
  }
}
