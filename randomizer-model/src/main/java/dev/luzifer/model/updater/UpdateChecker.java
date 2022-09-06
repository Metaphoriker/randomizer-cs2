package dev.luzifer.model.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {
    
    private final String versionUrl;
    
    private boolean updateAvailable;
    
    public UpdateChecker(String versionUrl) {
        this.versionUrl = versionUrl;
    }
    
    public void checkUpdate(String version) {
        
        HttpURLConnection connection = null;
        try {
            
            connection = (HttpURLConnection) new URL(versionUrl).openConnection();
            connection.connect();
            
            String latestVersion = readLineFromInputStream(connection.getInputStream());
            updateAvailable = !latestVersion.equals(version);
            
        } catch (Exception ignored) {
            // TODO: Handle exception f.e. no internet connection, GitHub down, whatever
        } finally {
            if(connection != null)
                connection.disconnect();
        }
    }
    
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
    
    private String readLineFromInputStream(InputStream inputStream) {
        
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "INVALID";
    }
}