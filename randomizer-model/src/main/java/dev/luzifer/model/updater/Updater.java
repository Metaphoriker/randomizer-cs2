package dev.luzifer.model.updater;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Updater {
    
    /**
     * Compares the current version with the latest version from the given URL.
     * @return a List of all files that need to be updated
     *
     * TODO: WIP
     */
    public static List<? extends ZipEntry> compareFiles(File current, File online) {
        
        try (ZipFile currentZip = new ZipFile(current);
             ZipFile onlineZip = new ZipFile(online)) {
            
            return onlineZip.stream()
                    .filter(onlineEntry -> {
                        ZipEntry currentEntry = currentZip.getEntry(onlineEntry.getName());
                        return currentEntry == null || currentEntry.getCrc() != onlineEntry.getCrc();
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not compare files", e);
        }
    }
    
    public static void update(File target, String versionUrl, String downloadUrl) {
    
        if(!target.exists()) {
            update(target, downloadUrl);
            return;
        }
        
        if(isEmpty(target)) {
            update(target, downloadUrl);
            return;
        }
        
        String currentVersion = null;
        try {
        
            try (ZipFile zipFile = new ZipFile(target)) {
    
                ZipEntry entry = zipFile.getEntry("version.txt");
                if(entry == null) {
                    update(target, downloadUrl);
                    return;
                }
                
                try (InputStream inputStream = zipFile.getInputStream(entry)) {
                    currentVersion = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).readLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    
        if (currentVersion == null) {
            return;
        }
    
        UpdateChecker updateChecker = new UpdateChecker(versionUrl);
        updateChecker.checkUpdate(currentVersion);
    
        if (updateChecker.isUpdateAvailable())
            update(target, downloadUrl);
    }
    
    private static boolean isEmpty(File file) {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(file), StandardCharsets.UTF_8))) {
            return bufferedReader.readLine() == null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void update(File target, String downloadUrl) {
        try {
            URL downloadFrom = new URL(downloadUrl);
            FileUtils.copyURLToFile(downloadFrom, target);
        } catch (IOException ignored) {
        }
    }
    
}
