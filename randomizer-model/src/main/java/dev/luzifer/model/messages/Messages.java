package dev.luzifer.model.messages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Messages {

    private static final Map<String, String> KEY_TEXT_MAP = new HashMap<>();

    public static void cache() {

        try(InputStream inputStream = Messages.class.getClassLoader().getResourceAsStream("messages/messages.properties")) {

            if(inputStream == null)
                return;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            bufferedReader.lines().forEach(line -> {
                String[] lineSplit = line.split("=", 2);
                KEY_TEXT_MAP.put(lineSplit[0], lineSplit[1]);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMessage(String key) {
        return KEY_TEXT_MAP.get(key);
    }

}
