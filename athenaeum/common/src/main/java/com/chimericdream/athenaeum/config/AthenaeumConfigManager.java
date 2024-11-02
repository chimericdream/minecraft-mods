package com.chimericdream.athenaeum.config;

import java.util.function.Consumer;

public class AthenaeumConfigManager {
    public static final Consumer<AthenaeumConfig> DEFAULT = (config) -> {
        config.firstEditionChance = AthenaeumConfig.Defaults.FIRST_EDITION_CHANCE;
        config.secondEditionChance = AthenaeumConfig.Defaults.SECOND_EDITION_CHANCE;
        config.thirdEditionChance = AthenaeumConfig.Defaults.THIRD_EDITION_CHANCE;
    };
}
