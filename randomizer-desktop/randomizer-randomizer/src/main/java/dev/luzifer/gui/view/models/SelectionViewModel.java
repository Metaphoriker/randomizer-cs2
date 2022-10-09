package dev.luzifer.gui.view.models;

import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.view.ViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;

public class SelectionViewModel implements ViewModel {
    
    private final ObjectProperty<CSSUtil.Theme> themeProperty = new SimpleObjectProperty<>(CSSUtil.Theme.MISTER_SILVER);
    
    private final Runnable randomizerCallback;
    private final Runnable builderCallback;
    private final Consumer<CSSUtil.Theme> switchThemeCallback;
    
    public SelectionViewModel(Runnable randomizerCallback, Runnable builderCallback, Consumer<CSSUtil.Theme> switchThemeCallback) {
        
        this.randomizerCallback = randomizerCallback;
        this.builderCallback = builderCallback;
        this.switchThemeCallback = switchThemeCallback;
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
    
    public void switchTheme() {
        if(switchThemeCallback != null)
            switchThemeCallback.accept(themeProperty.get());
    }
    
    public ObjectProperty<CSSUtil.Theme> getThemeProperty() {
        return themeProperty;
    }
    
}
