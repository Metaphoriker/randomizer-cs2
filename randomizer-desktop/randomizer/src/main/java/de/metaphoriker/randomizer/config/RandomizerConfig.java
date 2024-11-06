package de.metaphoriker.randomizer.config;

import de.godcipher.gutil.config.BaseConfiguration;
import de.godcipher.gutil.config.annotation.ConfigValue;
import de.godcipher.gutil.config.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration(fileName = "config.properties")
public class RandomizerConfig extends BaseConfiguration {

    @ConfigValue(name = "min.interval")
    private int minInterval = 30;

    @ConfigValue(name = "max.interval")
    private int maxInterval = 120;

    @ConfigValue(name = "autoupdate.enabled")
    private boolean autoupdateEnabled = true;
}
