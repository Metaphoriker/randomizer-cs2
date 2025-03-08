package com.revortix.randomizer.bootstrap;

import com.google.inject.AbstractModule;
import com.revortix.randomizer.config.RandomizerConfig;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.viewmodel.settings.ActionSettingsViewModel;
import com.revortix.randomizer.ui.view.viewmodel.builder.BuilderActionsViewModel;
import com.revortix.randomizer.ui.view.viewmodel.builder.BuilderEditorViewModel;
import com.revortix.randomizer.ui.view.viewmodel.builder.BuilderViewModel;
import com.revortix.randomizer.ui.view.viewmodel.HomeViewModel;
import com.revortix.randomizer.ui.view.viewmodel.NavigationBarViewModel;
import com.revortix.randomizer.ui.view.viewmodel.RandomizerViewModel;
import com.revortix.randomizer.ui.view.viewmodel.settings.GeneralSettingsViewModel;
import com.revortix.randomizer.ui.view.viewmodel.settings.UpdateSettingsViewModel;

public class RandomizerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(NavigationBarViewModel.class).asEagerSingleton();
        bind(BuilderViewModel.class).asEagerSingleton();
        bind(BuilderEditorViewModel.class).asEagerSingleton();
        bind(BuilderActionsViewModel.class).asEagerSingleton();
        bind(RandomizerViewModel.class).asEagerSingleton();
        bind(ActionSettingsViewModel.class).asEagerSingleton();
        bind(UpdateSettingsViewModel.class).asEagerSingleton();
        bind(GeneralSettingsViewModel.class).asEagerSingleton();
        bind(HomeViewModel.class).asEagerSingleton();
        bind(ViewProvider.class).asEagerSingleton();
        bind(RandomizerConfig.class).asEagerSingleton();
        bind(RandomizerUpdater.class).asEagerSingleton();
        bind(RandomizerConfigLoader.class).asEagerSingleton();
    }
}
