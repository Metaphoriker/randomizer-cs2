package de.metaphoriker.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.Interval;
import de.metaphoriker.model.cfg.keybind.KeyBind;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionJsonDeSerializer implements JsonSerializer<Action>, JsonDeserializer<Action> {

  private static final String CLASS_META_KEY = "CLASS_META_KEY";
  private static final String KEYBIND_KEY = "keybind";
  private static final String INTERVAL_KEY = "interval";

  @Override
  public JsonElement serialize(Action action, Type type, JsonSerializationContext context) {

    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty(CLASS_META_KEY, action.getClass().getCanonicalName());

    jsonObject.add(KEYBIND_KEY, context.serialize(action.getKeyBind()));

    if (action.getInterval() != null) {
      jsonObject.add(INTERVAL_KEY, context.serialize(action.getInterval()));
    }

    return jsonObject;
  }

  @Override
  public Action deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    String className = jsonObject.get(CLASS_META_KEY).getAsString();

    try {
      Class<?> clazz = Class.forName(className);

      Constructor<?> constructor = clazz.getDeclaredConstructor(KeyBind.class);
      KeyBind keyBind = context.deserialize(jsonObject.get(KEYBIND_KEY), KeyBind.class);
      if (keyBind == null) {
        log.warn("No keybind found for action: {}", className);
        keyBind = KeyBind.EMPTY_KEYBIND;
      }

      Action action = (Action) constructor.newInstance(keyBind);

      if (jsonObject.has(INTERVAL_KEY)) {
        Interval interval = context.deserialize(jsonObject.get(INTERVAL_KEY), Interval.class);
        action.setInterval(interval);
      }

      return action;

    } catch (Exception e) {
      log.error("Failed to deserialize Action: " + className, e);
      throw new JsonParseException("Failed to deserialize Action: " + className, e);
    }
  }
}
