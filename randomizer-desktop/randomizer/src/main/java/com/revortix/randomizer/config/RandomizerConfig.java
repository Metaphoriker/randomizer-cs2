package com.revortix.randomizer.config;

import de.metaphoriker.jshepherd.BaseConfiguration;
import de.metaphoriker.jshepherd.ConfigurationType;
import de.metaphoriker.jshepherd.annotation.Configuration;
import de.metaphoriker.jshepherd.annotation.Key;
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

  @Key("show.intro")
  private boolean showIntro = true;
}
