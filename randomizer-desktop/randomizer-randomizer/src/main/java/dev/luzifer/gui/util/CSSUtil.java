package dev.luzifer.gui.util;

import dev.luzifer.gui.view.views.BuilderView;
import javafx.scene.Parent;

import java.io.File;
import java.net.URL;

public class CSSUtil {
    
    private static final String STYLING_PATH = "styling" + File.separator;
    
    public static void applyBasicStyle(Parent node) {
    
        /*
         * Using BuilderView since it remains in the same folder as the styling folder
         */
        URL resource = BuilderView.class.getResource(STYLING_PATH + "BaseDesign.css");
        
        if(resource != null)
            node.getStylesheets().add(resource.toExternalForm());
        else
            throw new IllegalStateException("Could not find BaseDesign.css");
    }
    
    private CSSUtil() {
    }
}
