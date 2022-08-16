package dev.luzifer.ui.view.viewmodel;

import dev.luzifer.ui.view.ViewModel;

public class RandomizerViewModel implements ViewModel {
    
    private final Runnable callback;
    
    public RandomizerViewModel(Runnable callback) {
        this.callback = callback;
    }
    
    public void callback() {
        callback.run();
    }
    
}
