package dev.luzifer.gui.swing.game.model.util;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageHelper {

    private static final String EXTENSION = ".png";
    private static final String PATH = "/images/swing";
    private static final Map<String, Icon> iconCache = new HashMap<>();

    static {
        iconCache.put("/fallback", new ImageIcon(ImageHelper.class.getResource(PATH + "/fallback" + EXTENSION)));
    }

    public static synchronized Icon getIcon(String path) {
        return iconCache.computeIfAbsent(path, ImageHelper::loadIcon);
    }

    private static Icon loadIcon(String path) {

        URL url = ImageHelper.class.getResource(PATH + path + EXTENSION);
        if (url == null)
            return iconCache.get("/fallback");

        return new ImageIcon(url);
    }

    private ImageHelper() {
        throw new UnsupportedOperationException("This class is not meant to be instantiated!");
    }

}
