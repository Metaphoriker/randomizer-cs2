package de.metaphoriker.model.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.custom.MouseMoveAction;
import de.metaphoriker.model.action.custom.PauseAction;
import de.metaphoriker.model.action.sequence.ActionSequence;
import java.io.IOException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(Action.class, new ActionJsonDeSerializer())
          .registerTypeAdapter(PauseAction.class, new ActionJsonDeSerializer())
          .registerTypeAdapter(MouseMoveAction.class, new ActionJsonDeSerializer())
          .registerTypeAdapter(ActionSequence.class, new ActionSequenceJsonDeSerializer())
          .create();

  public static String serialize(ActionSequence actionSequence) {
    return GSON.getAdapter(ActionSequence.class).toJson(actionSequence);
  }

  public static ActionSequence deserialize(String json) {
    try {
      return GSON.getAdapter(ActionSequence.class).fromJson(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
