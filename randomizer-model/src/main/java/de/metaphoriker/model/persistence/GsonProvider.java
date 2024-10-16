package de.metaphoriker.model.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;

public class GsonProvider implements Provider<Gson> {

  private final ActionJsonDeSerializer actionJsonDeSerializer;
  private final ActionSequenceJsonDeSerializer actionSequenceJsonDeSerializer;

  @Inject
  public GsonProvider(
      ActionJsonDeSerializer actionJsonDeSerializer,
      ActionSequenceJsonDeSerializer actionSequenceJsonDeSerializer) {
    this.actionJsonDeSerializer = actionJsonDeSerializer;
    this.actionSequenceJsonDeSerializer = actionSequenceJsonDeSerializer;
  }

  @Override
  public Gson get() {
    return new GsonBuilder()
        .registerTypeAdapter(Action.class, actionJsonDeSerializer)
        .registerTypeAdapter(ActionSequence.class, actionSequenceJsonDeSerializer)
        .create();
  }
}
