package com.chimericdream.athenaeum.config;

import com.chimericdream.athenaeum.AthenaeumMod;
import com.chimericdream.athenaeum.ModInfo;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AthenaeumConfig {
    @SerialEntry
    public double firstEditionChance = Defaults.FIRST_EDITION_CHANCE;
    @SerialEntry
    public double secondEditionChance = Defaults.SECOND_EDITION_CHANCE;
    @SerialEntry
    public double thirdEditionChance = Defaults.THIRD_EDITION_CHANCE;

    public static ConfigClassHandler<AthenaeumConfig> HANDLER = ConfigClassHandler.createBuilder(AthenaeumConfig.class)
        .id(Identifier.of(ModInfo.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("athenaeum.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static void load() {
        HANDLER.load();
        validatePostLoad();
    }

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
            .title(Text.literal("Athenaeum Config"))
            .category(ConfigCategory.createBuilder()
                .name(Text.literal("Athenaeum Config"))
                .option(Option.<Double>createBuilder()
                    .name(Text.literal("First edition chance"))
                    .description(OptionDescription.of(Text.literal("The chance of getting a first-edition book. Default: 0.05 (5%)")))
                    .binding(Defaults.FIRST_EDITION_CHANCE, () -> config.firstEditionChance, newVal -> config.firstEditionChance = newVal)
                    .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                        .range(0.0, 1.0)
                        .step(0.01))
                    .build())
                .option(Option.<Double>createBuilder()
                    .name(Text.literal("Second edition chance"))
                    .description(OptionDescription.of(Text.literal("The chance of getting a second-edition book. Default: 0.2 (20%)")))
                    .binding(Defaults.SECOND_EDITION_CHANCE, () -> config.secondEditionChance, newVal -> config.secondEditionChance = newVal)
                    .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                        .range(0.0, 1.0)
                        .step(0.01))
                    .build())
                .option(Option.<Double>createBuilder()
                    .name(Text.literal("Third edition chance"))
                    .description(OptionDescription.of(Text.literal("The chance of getting a third-edition book. Default: 0.5 (50%)")))
                    .binding(Defaults.THIRD_EDITION_CHANCE, () -> config.thirdEditionChance, newVal -> config.thirdEditionChance = newVal)
                    .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                        .range(0.0, 1.0)
                        .step(0.01))
                    .build())
                .build())
        )).generateScreen(parent);
    }

    public static void validatePostLoad() {
        AthenaeumConfig config = HANDLER.instance();

        if (config.firstEditionChance < 0 || config.firstEditionChance > 1) {
            AthenaeumMod.LOGGER.info("[config] Invalid value found for 'firstEditionChance'! Resetting to default.");
            config.firstEditionChance = Defaults.FIRST_EDITION_CHANCE;
        }

        if (config.secondEditionChance < 0 || config.secondEditionChance > 1) {
            AthenaeumMod.LOGGER.info("[config] Invalid value found for 'secondEditionChance'! Resetting to default.");
            config.secondEditionChance = Defaults.SECOND_EDITION_CHANCE;
        }

        if (config.thirdEditionChance < 0 || config.thirdEditionChance > 1) {
            AthenaeumMod.LOGGER.info("[config] Invalid value found for 'thirdEditionChance'! Resetting to default.");
            config.thirdEditionChance = Defaults.THIRD_EDITION_CHANCE;
        }

        if (config.firstEditionChance + config.secondEditionChance + config.thirdEditionChance > 1) {
            AthenaeumMod.LOGGER.info("[config] The total chance must not exceed 1.0! Resetting to default.");

            config.firstEditionChance = Defaults.FIRST_EDITION_CHANCE;
            config.secondEditionChance = Defaults.SECOND_EDITION_CHANCE;
            config.thirdEditionChance = Defaults.THIRD_EDITION_CHANCE;
        }
    }

    public static class Defaults {
        public static double FIRST_EDITION_CHANCE = 0.05;
        public static double SECOND_EDITION_CHANCE = 0.2;
        public static double THIRD_EDITION_CHANCE = 0.5;
    }
}
