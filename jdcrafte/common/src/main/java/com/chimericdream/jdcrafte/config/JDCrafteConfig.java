package com.chimericdream.jdcrafte.config;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.lib.config.YaclConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class JDCrafteConfig {
    public static final int MIN_HORIZONTAL_RADIUS = 1;
    public static final int MAX_HORIZONTAL_RADIUS = 16;
    public static final int MIN_VERTICAL_RADIUS = 0;
    public static final int MAX_VERTICAL_RADIUS = 8;
    public static final int MIN_INTERVAL_SECONDS = 1;
    public static final int MIN_ANIMAL_CAP = 2;

    @SerialEntry
    public boolean troughFeedingEnabled = Defaults.TROUGH_FEEDING_ENABLED;
    @SerialEntry
    public int troughHorizontalRadius = Defaults.TROUGH_HORIZONTAL_RADIUS;
    @SerialEntry
    public int troughVerticalRadius = Defaults.TROUGH_VERTICAL_RADIUS;
    @SerialEntry
    public int troughIntervalSeconds = Defaults.TROUGH_INTERVAL_SECONDS;
    @SerialEntry
    public int troughAnimalCap = Defaults.TROUGH_ANIMAL_CAP;

    public static final YaclConfig<JDCrafteConfig> CONFIG = YaclConfig.builder(JDCrafteConfig.class, JDCrafteMod.MOD_ID)
        .fileName("jdcrafte.json5")
        .onLoad(JDCrafteConfig::clamp)
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.jdcrafte.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.jdcrafte.title"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.jdcrafte.option.troughFeedingEnabled"))
                    .description(OptionDescription.of(Component.translatable("text.config.jdcrafte.option.troughFeedingEnabled.description")))
                    .binding(Defaults.TROUGH_FEEDING_ENABLED, () -> config.troughFeedingEnabled, newVal -> config.troughFeedingEnabled = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.jdcrafte.option.troughHorizontalRadius"))
                    .description(OptionDescription.of(Component.translatable("text.config.jdcrafte.option.troughHorizontalRadius.description")))
                    .binding(Defaults.TROUGH_HORIZONTAL_RADIUS, () -> config.troughHorizontalRadius, newVal -> config.troughHorizontalRadius = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(MIN_HORIZONTAL_RADIUS, MAX_HORIZONTAL_RADIUS))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.jdcrafte.option.troughVerticalRadius"))
                    .description(OptionDescription.of(Component.translatable("text.config.jdcrafte.option.troughVerticalRadius.description")))
                    .binding(Defaults.TROUGH_VERTICAL_RADIUS, () -> config.troughVerticalRadius, newVal -> config.troughVerticalRadius = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(MIN_VERTICAL_RADIUS, MAX_VERTICAL_RADIUS))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.jdcrafte.option.troughIntervalSeconds"))
                    .description(OptionDescription.of(Component.translatable("text.config.jdcrafte.option.troughIntervalSeconds.description")))
                    .binding(Defaults.TROUGH_INTERVAL_SECONDS, () -> config.troughIntervalSeconds, newVal -> config.troughIntervalSeconds = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(MIN_INTERVAL_SECONDS))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.jdcrafte.option.troughAnimalCap"))
                    .description(OptionDescription.of(Component.translatable("text.config.jdcrafte.option.troughAnimalCap.description")))
                    .binding(Defaults.TROUGH_ANIMAL_CAP, () -> config.troughAnimalCap, newVal -> config.troughAnimalCap = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(MIN_ANIMAL_CAP))
                    .build())
                .build())
        )
        .build();

    /**
     * Pulls hand-edited out-of-range values back inside the limits. The config screen enforces the
     * same limits, but the JSON file can be edited directly.
     */
    public void clamp() {
        troughHorizontalRadius = Math.clamp(troughHorizontalRadius, MIN_HORIZONTAL_RADIUS, MAX_HORIZONTAL_RADIUS);
        troughVerticalRadius = Math.clamp(troughVerticalRadius, MIN_VERTICAL_RADIUS, MAX_VERTICAL_RADIUS);
        troughIntervalSeconds = Math.max(troughIntervalSeconds, MIN_INTERVAL_SECONDS);
        troughAnimalCap = Math.max(troughAnimalCap, MIN_ANIMAL_CAP);
    }

    public static class Defaults {
        public static boolean TROUGH_FEEDING_ENABLED = true;
        public static int TROUGH_HORIZONTAL_RADIUS = 5;
        public static int TROUGH_VERTICAL_RADIUS = 2;
        public static int TROUGH_INTERVAL_SECONDS = 10;
        public static int TROUGH_ANIMAL_CAP = 24;
    }
}
