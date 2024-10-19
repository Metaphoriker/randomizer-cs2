package de.metaphoriker.model.persistence.de_serializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceJsonDeSerializer
    implements JsonSerializer<ActionSequence>, JsonDeserializer<ActionSequence> {

  private static final String NAME_KEY = "name";
  private static final String DESCRIPTION_KEY = "description";
  private static final String ACTIVE_KEY = "active";
  private static final String ACTIONS_KEY = "actions";

  @Override
  public JsonElement serialize(
      ActionSequence actionSequence, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty(NAME_KEY, actionSequence.getName());
    jsonObject.addProperty(DESCRIPTION_KEY, actionSequence.getDescription());
    jsonObject.addProperty(ACTIVE_KEY, actionSequence.isActive());
    jsonObject.add(ACTIONS_KEY, context.serialize(actionSequence.getActions()));

    return jsonObject;
  }

  @Override
  public ActionSequence deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    for (String key : jsonObject.keySet()) {
      if (!NAME_KEY.equals(key)
          && !DESCRIPTION_KEY.equals(key)
          && !ACTIONS_KEY.equals(key)
          && !ACTIVE_KEY.equals(key)) {
        log.warn("Unbekannter Key gefunden: {}", key);
      }
    }

    String name =
        jsonObject.has(NAME_KEY) && !jsonObject.get(NAME_KEY).isJsonNull()
            ? jsonObject.get(NAME_KEY).getAsString()
            : "<INVALID>";

    String description =
        jsonObject.has(DESCRIPTION_KEY) && !jsonObject.get(DESCRIPTION_KEY).isJsonNull()
            ? jsonObject.get(DESCRIPTION_KEY).getAsString()
            : "";

    List<Action> actions =
        jsonObject.has(ACTIONS_KEY) && !jsonObject.get(ACTIONS_KEY).isJsonNull()
            ? context.deserialize(
                jsonObject.get(ACTIONS_KEY), new TypeToken<List<Action>>() {}.getType())
            : Collections.emptyList();

    boolean active =
        jsonObject.has(ACTIVE_KEY)
            && !jsonObject.get(ACTIVE_KEY).isJsonNull()
            && jsonObject.get(ACTIVE_KEY).getAsBoolean();

    ActionSequence actionSequence = new ActionSequence(name);
    actionSequence.setDescription(description);
    actionSequence.setActive(active);
    actionSequence.setActions(actions);

    return actionSequence;
  }
}
