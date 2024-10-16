package de.metaphoriker.model.persistence;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.handling.ActionRepository;
import de.metaphoriker.model.action.value.Interval;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionJsonDeSerializer implements JsonSerializer<Action>, JsonDeserializer<Action> {

  private static final String NAME_KEY = "name";
  private static final String INTERVAL_KEY = "interval";

  @Inject private ActionRepository actionRepository;

  @Override
  public JsonElement serialize(Action action, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty(NAME_KEY, action.getName());
    jsonObject.add(INTERVAL_KEY, context.serialize(action.getInterval()));
    return jsonObject;
  }

  @Override
  public Action deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    String name = jsonObject.get(NAME_KEY).getAsString();

    try {
      Action action = actionRepository.getByName(name);
      if (action == null) {
        throw new JsonParseException("No Action found with the name: " + name);
      }

      Interval interval = context.deserialize(jsonObject.get(INTERVAL_KEY), Interval.class);
      action.setInterval(interval);

      return action;
    } catch (Exception e) {
      log.error("Failed to deserialize Action: {}", name, e);
      throw new JsonParseException("Failed to deserialize Action: " + name, e);
    }
  }
}
