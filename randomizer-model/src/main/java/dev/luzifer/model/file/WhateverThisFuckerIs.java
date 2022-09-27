package dev.luzifer.model.file;

import java.io.File;

// TODO: Rename this class
public class WhateverThisFuckerIs {
    
    // This class only exists because I don't know where to put this folder
    private static final File APPDATA_FOLDER = new File(System.getenv("APPDATA") + File.separator + "randomizer");
    
    public static File getAppdataFolder() {
        return APPDATA_FOLDER;
    }
    
    private WhateverThisFuckerIs() {
    }
}
