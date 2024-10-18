package de.metaphoriker.randomizer.bootstrap;

import com.google.inject.AbstractModule;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.RandomizerViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.SequenceBuilderViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.SequencesViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.SidebarViewModel;

public class RandomizerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SidebarViewModel.class).asEagerSingleton();
    bind(RandomizerViewModel.class).asEagerSingleton();
    bind(SequencesViewModel.class).asEagerSingleton();
    bind(SequenceBuilderViewModel.class).asEagerSingleton();
    bind(ViewProvider.class).asEagerSingleton();
  }
}
