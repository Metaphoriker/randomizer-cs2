package de.metaphoriker.model.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import java.io.IOException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(Action.class, new ActionJsonDeSerializer())
          .registerTypeAdapter(ActionSequence.class, new ActionSequenceJsonDeSerializer())
          .create();

  public static String serialize(Action action) {
    return GSON.getAdapter(Action.class).toJson(action);
  }

  public static Action deserializeAction(String json) {
    try {
      return GSON.getAdapter(Action.class).fromJson(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String serialize(ActionSequence actionSequence) {
    return GSON.getAdapter(ActionSequence.class).toJson(actionSequence);
  }

  public static ActionSequence deserializeActionSequence(String json) {
    try {
      return GSON.getAdapter(ActionSequence.class).fromJson(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
