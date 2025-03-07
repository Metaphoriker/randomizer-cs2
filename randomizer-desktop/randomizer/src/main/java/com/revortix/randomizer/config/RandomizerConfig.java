package com.revortix.randomizer.config;

import com.revortix.model.config.keybind.KeyBindType;
import de.metaphoriker.jshepherd.BaseConfiguration;
import de.metaphoriker.jshepherd.ConfigurationType;
import de.metaphoriker.jshepherd.annotation.Configuration;
import de.metaphoriker.jshepherd.annotation.Key;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration(fileName = "config", type = ConfigurationType.YAML)
public class RandomizerConfig extends BaseConfiguration {

  @Key("first.start")
  private boolean firstStart = true;

  @Key("min.interval")
  private int minInterval = 15;

  @Key("max.interval")
  private int maxInterval = 70;

  @Key("autoupdate.enabled")
  private boolean autoupdateEnabled = false;

  @Key("update.notifier")
  private boolean updateNotifier = true;

  @Key("show.intro")
  private boolean showIntro = true;

  @Key("builder.filters.activated")
  private List<String> builderFiltersActivated = new ArrayList<>(KeyBindType.values().length);
}
