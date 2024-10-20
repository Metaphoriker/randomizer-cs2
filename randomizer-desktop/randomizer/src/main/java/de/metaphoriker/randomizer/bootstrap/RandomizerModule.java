package de.metaphoriker.randomizer.bootstrap;

import com.google.inject.AbstractModule;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.NavigationBarViewModel;

public class RandomizerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(NavigationBarViewModel.class).asEagerSingleton();
    bind(BuilderViewModel.class).asEagerSingleton();
    bind(ViewProvider.class).asEagerSingleton();
  }
}
