package dev.luzifer.model.config;

import java.util.HashSet;
import java.util.Set;

public class ConfigRepository {
    
    private final Set<Config> configs = new HashSet<>();
    
    public void saveConfig(Config config) {
        // TODO: Dao
    }
    
    public void deleteConfig(Config config) {
        // TODO: Dao
    }
    
    public void addConfig(Config config) {
        configs.add(config);
    }
    
    public void removeConfig(Config config) {
        configs.remove(config);
    }
    
    public Set<Config> loadConfigs() {
        // TODO: Dao
        return null;
    }
    
    public Set<Config> getConfigs() {
        return configs;
    }
    
    public Config getConfig(String name) {
        return configs.stream()
                .filter(config -> config.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
}
