package dev.luzifer.update;

import dev.luzifer.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    
    private static final String VERSION_URL = "https://raw.githubusercontent.com/Luziferium/randomizer-csgo/master/randomizer/src/main/resources/version.txt";
    
    public boolean hasUpdate() {
        
        HttpURLConnection connection;
        String latestVersion;
    
        try {
        
            connection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
            connection.connect();
        
            latestVersion = readLineFromInputStream(connection.getInputStream());
        
        } catch (Exception e) {
            return false;
        }
        
        return !latestVersion.equals(fetchCurrentVersion());
    }
    
    private String fetchCurrentVersion() {
        InputStream inputStream = getInputStream("version.txt");
        return readLineFromInputStream(inputStream);
    }
    
    public InputStream getInputStream(String fileName) {
        
        InputStream resource = Main.class.getResourceAsStream("/" + fileName);
        
        if (resource == null)
            throw new IllegalStateException("Probably corrupted JAR file, missing " + fileName);
        
        return resource;
    }
    
    private String readLineFromInputStream(InputStream inputStream) {
        
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "INVALID";
    }
}
