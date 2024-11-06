package de.metaphoriker.randomizer.bootstrap;

import com.google.inject.AbstractModule;
import de.metaphoriker.randomizer.config.RandomizerConfig;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.ActionSettingsViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.NavigationBarViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.RandomizerViewModel;

public class RandomizerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(NavigationBarViewModel.class).asEagerSingleton();
        bind(BuilderViewModel.class).asEagerSingleton();
        bind(RandomizerViewModel.class).asEagerSingleton();
        bind(ActionSettingsViewModel.class).asEagerSingleton();
        bind(ViewProvider.class).asEagerSingleton();
        bind(RandomizerConfig.class).asEagerSingleton();
    }
}
