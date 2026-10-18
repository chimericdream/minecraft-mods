package com.chimericdream.pannotiacompanion.config;

import com.chimericdream.lib.config.YaclConfig;
import com.chimericdream.pannotiacompanion.ModInfo;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PannotiaCompanionConfig {
    @SerialEntry
    public int maxHomesPerDimension = Defaults.MAX_HOMES_PER_DIMENSION;
    @SerialEntry
    public int homeDelayTicks = Defaults.HOME_DELAY_TICKS;
    @SerialEntry
    public int homeCooldownSeconds = Defaults.HOME_COOLDOWN_SECONDS;

    @SerialEntry
    public int spawnDelayTicks = Defaults.SPAWN_DELAY_TICKS;
    @SerialEntry
    public int spawnCooldownSeconds = Defaults.SPAWN_COOLDOWN_SECONDS;

    @SerialEntry
    public int backDelayTicks = Defaults.BACK_DELAY_TICKS;
    @SerialEntry
    public int backCooldownSeconds = Defaults.BACK_COOLDOWN_SECONDS;
    @SerialEntry
    public boolean saveDeathLocation = Defaults.SAVE_DEATH_LOCATION;

    public static final YaclConfig<PannotiaCompanionConfig> CONFIG = YaclConfig.builder(PannotiaCompanionConfig.class, ModInfo.MOD_ID)
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("text.pannotiacompanion.config.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.pannotiacompanion.config.section.homes"))
                .option(intOption("maxHomesPerDimension", Defaults.MAX_HOMES_PER_DIMENSION, () -> config.maxHomesPerDimension, v -> config.maxHomesPerDimension = v))
                .option(intOption("homeDelayTicks", Defaults.HOME_DELAY_TICKS, () -> config.homeDelayTicks, v -> config.homeDelayTicks = v))
                .option(intOption("homeCooldownSeconds", Defaults.HOME_COOLDOWN_SECONDS, () -> config.homeCooldownSeconds, v -> config.homeCooldownSeconds = v))
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.pannotiacompanion.config.section.spawn"))
                .option(intOption("spawnDelayTicks", Defaults.SPAWN_DELAY_TICKS, () -> config.spawnDelayTicks, v -> config.spawnDelayTicks = v))
                .option(intOption("spawnCooldownSeconds", Defaults.SPAWN_COOLDOWN_SECONDS, () -> config.spawnCooldownSeconds, v -> config.spawnCooldownSeconds = v))
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.pannotiacompanion.config.section.back"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.pannotiacompanion.config.option.saveDeathLocation"))
                    .description(OptionDescription.of(Component.translatable("text.pannotiacompanion.config.option.saveDeathLocation.desc")))
                    .binding(Defaults.SAVE_DEATH_LOCATION, () -> config.saveDeathLocation, v -> config.saveDeathLocation = v)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(intOption("backDelayTicks", Defaults.BACK_DELAY_TICKS, () -> config.backDelayTicks, v -> config.backDelayTicks = v))
                .option(intOption("backCooldownSeconds", Defaults.BACK_COOLDOWN_SECONDS, () -> config.backCooldownSeconds, v -> config.backCooldownSeconds = v))
                .build()))
        .build();

    private static Option<Integer> intOption(String key, int defaultValue, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
            .name(Component.translatable("text.pannotiacompanion.config.option." + key))
            .description(OptionDescription.of(Component.translatable("text.pannotiacompanion.config.option." + key + ".desc")))
            .binding(defaultValue, getter, setter)
            .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0))
            .build();
    }

    public static PannotiaCompanionConfig get() {
        return CONFIG.instance();
    }

    public static class Defaults {
        public static int MAX_HOMES_PER_DIMENSION = 1;
        public static int HOME_DELAY_TICKS = 0;
        public static int HOME_COOLDOWN_SECONDS = 0;
        public static int SPAWN_DELAY_TICKS = 0;
        public static int SPAWN_COOLDOWN_SECONDS = 0;
        public static int BACK_DELAY_TICKS = 0;
        public static int BACK_COOLDOWN_SECONDS = 0;
        public static boolean SAVE_DEATH_LOCATION = false;
    }
}
