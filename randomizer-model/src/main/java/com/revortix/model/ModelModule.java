package com.revortix.model;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.revortix.model.action.repository.ActionRepository;
import com.revortix.model.action.repository.ActionSequenceRepository;
import com.revortix.model.action.sequence.ActionSequenceDispatcher;
import com.revortix.model.action.sequence.ActionSequenceExecutorRunnable;
import com.revortix.model.config.keybind.KeyBindNameTypeMapper;
import com.revortix.model.config.keybind.KeyBindRepository;
import com.revortix.model.persistence.GsonProvider;
import com.revortix.model.persistence.JsonUtil;
import com.revortix.model.persistence.dao.ActionSequenceDao;
import com.revortix.model.persistence.de_serializer.ActionJsonDeSerializer;
import com.revortix.model.persistence.de_serializer.ActionSequenceJsonDeSerializer;
import com.revortix.model.watcher.FileSystemWatcher;

public class ModelModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApplicationContext.class).asEagerSingleton();
        bind(ActionRepository.class).asEagerSingleton();
        bind(ActionSequenceRepository.class).asEagerSingleton();
        bind(KeyBindRepository.class).asEagerSingleton();
        bind(KeyBindNameTypeMapper.class).asEagerSingleton();
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
