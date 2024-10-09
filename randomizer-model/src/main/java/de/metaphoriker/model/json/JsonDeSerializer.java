package de.metaphoriker.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.metaphoriker.model.cfg.keybind.KeyBind;
import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.Interval;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonDeSerializer implements JsonSerializer<Event>, JsonDeserializer<Event> {

  private static final String CLASS_META_KEY = "CLASS_META_KEY";
  private static final String KEYBIND_KEY = "keyBind";
  private static final String INTERVAL_KEY = "interval";
  private static final String ACTIVATED_KEY = "activated";

  @Override
  public JsonElement serialize(Event event, Type type, JsonSerializationContext context) {

    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty(CLASS_META_KEY, event.getClass().getCanonicalName());

    jsonObject.add(KEYBIND_KEY, context.serialize(event.getKeyBind()));

    if (event.getInterval() != null) {
      jsonObject.add(INTERVAL_KEY, context.serialize(event.getInterval()));
    }

    jsonObject.addProperty(ACTIVATED_KEY, event.isActivated());

    return jsonObject;
  }

  @Override
  public Event deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    String className = jsonObject.get(CLASS_META_KEY).getAsString();

    try {
      Class<?> clazz = Class.forName(className);

      Constructor<?> constructor = clazz.getDeclaredConstructor(KeyBind.class);
      KeyBind keyBind = context.deserialize(jsonObject.get(KEYBIND_KEY), KeyBind.class);
      if (keyBind == null) {
        log.warn("No keyBind found for event: {}", className);
        keyBind = KeyBind.EMPTY_KEYBIND;
      }

      Event event = (Event) constructor.newInstance(keyBind);

      if (jsonObject.has(INTERVAL_KEY)) {
        Interval interval = context.deserialize(jsonObject.get(INTERVAL_KEY), Interval.class);
        event.setInterval(interval);
      }

      event.setActivated(jsonObject.get(ACTIVATED_KEY).getAsBoolean());

      return event;

    } catch (Exception e) {
      log.error("Failed to deserialize Event: " + className, e);
      throw new JsonParseException("Failed to deserialize Event: " + className, e);
    }
  }
}
