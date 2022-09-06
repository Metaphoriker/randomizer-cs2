package dev.luzifer.model.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.luzifer.model.event.Event;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class JsonUtil {
    
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Event.class, new JsonDeSerializer())
            .create();
    
    public static String serialize(Event event) {
        return GSON.getAdapter(Event.class).toJson(event);
    }
    
    public static Event deserialize(String json) {
        try {
            return GSON.getAdapter(Event.class).fromJson(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
