package de.metaphoriker.model.persistence.de_serializer;

import com.google.gson.*;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionSequenceJsonDeSerializer
    implements JsonSerializer<ActionSequence>, JsonDeserializer<ActionSequence> {

  private static final String NAME_KEY = "name";
  private static final String ACTIONS_KEY = "actions";
  private static final String ACTIVE_KEY = "active";

  @Override
  public JsonElement serialize(
      ActionSequence actionSequence, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty(NAME_KEY, actionSequence.getName());
    jsonObject.add(ACTIONS_KEY, context.serialize(actionSequence.getActions()));
    jsonObject.addProperty(ACTIVE_KEY, actionSequence.isActive());

    return jsonObject;
  }

  @Override
  public ActionSequence deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    String name = jsonObject.get(NAME_KEY).getAsString();

    Type listOfActionType = new TypeToken<List<Action>>() {}.getType();
    List<Action> actions = context.deserialize(jsonObject.get(ACTIONS_KEY), listOfActionType);

    boolean active = jsonObject.get(ACTIVE_KEY).getAsBoolean();

    ActionSequence actionSequence = new ActionSequence(name);
    actionSequence.setActions(actions);
    actionSequence.setActive(active);

    return actionSequence;
  }
}
