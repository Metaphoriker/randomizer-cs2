package dev.luzifer.model.stuff;

import dev.luzifer.model.ApplicationState;

import java.io.File;

/**
 * This class is for whatever the fuck this is. I don't know. I don't care.
 * It just works. Don't touch it.
 * But to be honest, I think it's for the appdata folder.
 *
 * @author GitHub-Copilot
 *
 * But for real now, this class is for stuff idk where to put yet.
 */
public class WhateverThisFuckerIs {
    
    private static final File APPDATA_FOLDER = new File(System.getenv("APPDATA") + File.separator + "randomizer");
    private static ApplicationState applicationState = ApplicationState.IDLING;
    
    public static void setApplicationState(ApplicationState applicationState) {
        WhateverThisFuckerIs.applicationState = applicationState;
    }
    
    public static File getAppdataFolder() {
        return APPDATA_FOLDER;
    }
    
    public static ApplicationState getApplicationState() {
        return applicationState;
    }
    
    private WhateverThisFuckerIs() {
    }
}
