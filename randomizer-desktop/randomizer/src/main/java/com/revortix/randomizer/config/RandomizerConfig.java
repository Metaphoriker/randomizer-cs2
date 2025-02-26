package com.revortix.randomizer.config;

import de.godcipher.gutil.config.BaseConfiguration;
import de.godcipher.gutil.config.annotation.ConfigValue;
import de.godcipher.gutil.config.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration(fileName = "config.yml") // currently we have to use yml cause of the delimiter of gutil
public class RandomizerConfig extends BaseConfiguration {

    @ConfigValue(name = "min.interval")
    private int minInterval = 30;

    @ConfigValue(name = "max.interval")
    private int maxInterval = 120;

    @ConfigValue(name = "autoupdate.enabled")
    private boolean autoupdateEnabled = false;

    @ConfigValue(name = "show.intro")
    private boolean showIntro = true;
}
