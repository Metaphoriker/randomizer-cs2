package de.metaphoriker.model;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import de.metaphoriker.model.action.repository.ActionRepository;
import de.metaphoriker.model.persistence.dao.ActionSequenceDao;
import de.metaphoriker.model.action.sequence.ActionSequenceDispatcher;
import de.metaphoriker.model.action.sequence.ActionSequenceExecutorRunnable;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;
import de.metaphoriker.model.config.keybind.KeyBindRepository;
import de.metaphoriker.model.persistence.de_serializer.ActionJsonDeSerializer;
import de.metaphoriker.model.persistence.de_serializer.ActionSequenceJsonDeSerializer;
import de.metaphoriker.model.persistence.GsonProvider;
import de.metaphoriker.model.persistence.JsonUtil;
import de.metaphoriker.model.stuff.ApplicationContext;
import de.metaphoriker.model.watcher.FileSystemWatcher;

public class ModelModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ApplicationContext.class).asEagerSingleton();
    bind(ActionRepository.class).asEagerSingleton();
    bind(ActionSequenceRepository.class).asEagerSingleton();
    bind(KeyBindRepository.class).asEagerSingleton();
    bind(ActionSequenceDispatcher.class).asEagerSingleton();
    bind(FileSystemWatcher.class).asEagerSingleton();
    bind(JsonUtil.class).asEagerSingleton();
    bind(ActionSequenceDao.class).asEagerSingleton();

    bind(ActionSequenceExecutorRunnable.class);
    bind(ActionJsonDeSerializer.class);
    bind(ActionSequenceJsonDeSerializer.class);

    bind(Gson.class).toProvider(GsonProvider.class);
  }
}
