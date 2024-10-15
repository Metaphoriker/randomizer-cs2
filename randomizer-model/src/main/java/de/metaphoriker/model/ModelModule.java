package de.metaphoriker.model;

import com.google.inject.AbstractModule;
import de.metaphoriker.model.action.handling.ActionRepository;
import de.metaphoriker.model.action.sequence.ActionSequenceDispatcher;
import de.metaphoriker.model.action.sequence.ActionSequenceExecutorRunnable;
import de.metaphoriker.model.action.sequence.ActionSequenceRepository;
import de.metaphoriker.model.cfg.keybind.KeyBindRepository;
import de.metaphoriker.model.json.ActionJsonDeSerializer;
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

    bind(ActionSequenceExecutorRunnable.class);
    bind(ActionJsonDeSerializer.class);
  }
}
