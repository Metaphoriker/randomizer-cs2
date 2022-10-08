package dev.luzifer.gui.util;

import dev.luzifer.gui.view.views.BuilderView;
import dev.luzifer.gui.view.views.RandomizerView;
import javafx.scene.Parent;

import java.net.URL;
import java.text.MessageFormat;

public class CSSUtil {
    
    private static final String STYLING_PATH = "styling/";
    private static final String STYLING_THEMES_PATH = STYLING_PATH + "themes/";
    private static final String STYLING_EXTENSION = ".css";

    // TODO: Maybe make this work with Scene for overall styling
    public static void applyBasicStyle(Parent node) {
        applyStyle(node, RandomizerView.class);
    }

    public static void applyDarkTheme(Parent node) {
        applyTheme(node, Theme.DARK);
    }

    public static void applyLightTheme(Parent node) {
        applyTheme(node, Theme.LIGHT);
    }

    public static void applyNightTheme(Parent node) {
        applyTheme(node, Theme.NIGHT);
    }

    /**
     * Applies the CSS file with the same name as the class to the given node.
     * @param node The node to apply the CSS to.
     *             The node must have a class with the same name as the CSS file.
     *             The CSS file must be located in the {@link #STYLING_PATH}.
     */
    private static void applyStyle(Parent node, Class<?> clazz) {

        URL resource = clazz.getResource(getStylePath(clazz));

        if(resource != null)
            node.getStylesheets().add(resource.toExternalForm());
        else
            throw new IllegalStateException(MessageFormat.format("CSS file could not get loaded for class: {0}", clazz));
    }

    /**
     * Applies the CSS file of the given theme to the given node.
     * @param node The node to apply the CSS to.
     */
    private static void applyTheme(Parent node, Theme theme) {

        URL resource = BuilderView.class.getResource(getThemePath(theme));

        if(resource != null)
            node.getStylesheets().add(resource.toExternalForm());
        else
            throw new IllegalStateException(MessageFormat.format("Theme file could not get loaded for theme: {0}", theme));
    }

    private static String getStylePath(Class<?> clazz) {
        return STYLING_PATH + clazz.getSimpleName() + STYLING_EXTENSION;
    }

    private static String getThemePath(Theme theme) {
        return STYLING_THEMES_PATH + theme.fileName + STYLING_EXTENSION;
    }

    private enum Theme {

        DARK("Dark", "DarkTheme"),
        LIGHT("LIGHT", "LightTheme"),
        NIGHT("Night", "NightTheme");

        private final String name;
        private final String fileName;

        Theme(String name, String fileName) {
            this.name = name;
            this.fileName = fileName;
        }

        public String getName() {
            return name;
        }

        public String getFileName() {
            return fileName;
        }
    }

    private CSSUtil() {
    }
}
