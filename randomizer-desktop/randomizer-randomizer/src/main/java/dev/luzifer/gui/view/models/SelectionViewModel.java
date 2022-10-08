package dev.luzifer.gui.view.models;

import dev.luzifer.gui.view.ViewModel;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class SelectionViewModel implements ViewModel {
    
    private final Runnable randomizerCallback;
    private final Runnable builderCallback;
    
    public SelectionViewModel(Runnable randomizerCallback, Runnable builderCallback) {
        this.randomizerCallback = randomizerCallback;
        this.builderCallback = builderCallback;
    }
    
    public void openRandomizer() {
        randomizerCallback.run();
    }
    
    public void openBuilder() {
        builderCallback.run();
    }
    
    public void openConfig() {
        try {
            Desktop.getDesktop().browse(new URL("https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstley").toURI());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
}
