package dev.luzifer.gui.view.models;

import dev.luzifer.gui.view.ViewModel;

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
    
}
