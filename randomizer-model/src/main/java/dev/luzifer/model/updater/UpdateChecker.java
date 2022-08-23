package dev.luzifer.model.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {
    
    private static final String VERSION_URL = "https://raw.githubusercontent.com/Luziferium/randomizer-csgo/master/randomizer-model/src/main/resources/version.txt";
    
    private boolean updateAvailable;
    
    public void checkUpdate() {

        HttpURLConnection connection = null;
        try {

            connection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
            connection.connect();

            String latestVersion = readLineFromInputStream(connection.getInputStream());
            updateAvailable = !latestVersion.equals(fetchCurrentVersion());

        } catch (Exception ignored) {
            // TODO: Handle exception
        } finally {
            if(connection != null)
                connection.disconnect();
        }
    }
    
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
    
    private String fetchCurrentVersion() {
        InputStream inputStream = getInputStream("version.txt");
        return readLineFromInputStream(inputStream);
    }
    
    private InputStream getInputStream(String fileName) {
        
        InputStream resource = UpdateChecker.class.getResourceAsStream("/" + fileName);
        
        if (resource == null)
            throw new IllegalStateException("Probably corrupted JAR file, missing " + fileName);
        
        return resource;
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
