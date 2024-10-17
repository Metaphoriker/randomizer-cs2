package de.metaphoriker.randomizer.bootstrap;

import com.google.inject.AbstractModule;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.RandomizerViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.SidebarViewModel;
import de.metaphoriker.randomizer.ui.view.views.BuilderView;
import de.metaphoriker.randomizer.ui.view.views.MainWindow;
import de.metaphoriker.randomizer.ui.view.views.RandomizerView;
import de.metaphoriker.randomizer.ui.view.views.SidebarView;

public class RandomizerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(BuilderViewModel.class).asEagerSingleton();
    bind(SidebarViewModel.class).asEagerSingleton();
    bind(RandomizerViewModel.class).asEagerSingleton();

    bind(BuilderView.class);
    bind(MainWindow.class);
    bind(RandomizerView.class);
    bind(SidebarView.class);

    bind(ViewProvider.class).asEagerSingleton();
  }
}
