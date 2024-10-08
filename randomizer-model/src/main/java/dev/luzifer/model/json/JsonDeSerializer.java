package dev.luzifer.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.luzifer.model.event.Interval;
import java.lang.reflect.Type;

public class JsonDeSerializer implements JsonSerializer<Object>, JsonDeserializer<Object> {

  private static final String CLASS_META_KEY = "CLASS_META_KEY";
  private static final String MIN_KEY = "min";
  private static final String MAX_KEY = "max";

  @Override
  public JsonElement serialize(
      Object object, Type type, JsonSerializationContext jsonSerializationContext) {

    JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
    jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName());

    return jsonEle;
  }

  @Override
  public Object deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {

    JsonObject jsonObj = jsonElement.getAsJsonObject();
    String className = jsonObj.get(CLASS_META_KEY).getAsString();

    try {
      Class<?> clazz = Class.forName(className);
      Object object = jsonDeserializationContext.deserialize(jsonElement, clazz);

      if (object instanceof Interval) {

        Interval interval = (Interval) object;

        interval.setMin(jsonObj.get(MIN_KEY).getAsInt());
        interval.setMax(jsonObj.get(MAX_KEY).getAsInt());

        return interval;
      }

      return object;
    } catch (ClassNotFoundException e) {
      throw new JsonParseException(e);
    }
  }
}
