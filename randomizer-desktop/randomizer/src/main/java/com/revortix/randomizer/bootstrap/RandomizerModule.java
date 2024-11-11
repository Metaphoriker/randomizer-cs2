package com.revortix.randomizer.bootstrap;

import com.google.inject.AbstractModule;
import com.revortix.randomizer.config.RandomizerConfig;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.viewmodel.ActionSettingsViewModel;
import com.revortix.randomizer.ui.view.viewmodel.BuilderViewModel;
import com.revortix.randomizer.ui.view.viewmodel.NavigationBarViewModel;
import com.revortix.randomizer.ui.view.viewmodel.RandomizerViewModel;

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
