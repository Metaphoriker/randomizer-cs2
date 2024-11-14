package com.revortix.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import com.revortix.randomizer.bootstrap.RandomizerUpdater;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class HomeViewModel {

    private static final String GITHUB_LINK = "https://github.com/revortix/randomizer-cs2";
    private static final String DISCORD_LINK = "https://discord.gg/782s5ExhFy";

    private final RandomizerUpdater randomizerUpdater;

    @Inject
    public HomeViewModel(RandomizerUpdater randomizerUpdater) {
        this.randomizerUpdater = randomizerUpdater;
    }

    public void openGitHub() throws IOException {
        Desktop.getDesktop().browse(URI.create(GITHUB_LINK));
    }

    public void openDiscord() throws IOException {
        Desktop.getDesktop().browse(URI.create(DISCORD_LINK));
    }

    public void updateRandomizer() {
        randomizerUpdater.runUpdaterIfNeeded();
    }

    public boolean isUpdateAvailable() {
        return randomizerUpdater.isRandomizerUpdateAvailable();
    }
}
