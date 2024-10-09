package de.metaphoriker.model.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.metaphoriker.model.action.Action;
import java.io.IOException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

  private static final Gson GSON =
      new GsonBuilder().registerTypeAdapter(Action.class, new JsonDeSerializer()).create();

  public static String serialize(Action action) {
    return GSON.getAdapter(Action.class).toJson(action);
  }

  public static Action deserialize(String json) {
    try {
      return GSON.getAdapter(Action.class).fromJson(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
