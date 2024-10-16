package de.metaphoriker.model.persistence;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.metaphoriker.model.action.sequence.ActionSequence;

public class JsonUtil {

  private final Gson gson;

  @Inject
  public JsonUtil(Gson gson) {
    this.gson = gson;
  }

  public String serialize(ActionSequence actionSequence) {
    return gson.toJson(actionSequence);
  }

  public ActionSequence deserialize(String json) {
    return gson.fromJson(json, ActionSequence.class);
  }
}
