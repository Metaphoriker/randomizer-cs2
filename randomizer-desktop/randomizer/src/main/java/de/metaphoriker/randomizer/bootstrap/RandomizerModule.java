package de.metaphoriker.randomizer.bootstrap;

import com.google.inject.AbstractModule;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.ControlBarViewModel;

public class RandomizerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ControlBarViewModel.class).asEagerSingleton();
    bind(ViewProvider.class).asEagerSingleton();
  }
}
