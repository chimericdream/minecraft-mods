package com.chimericdream.bctweaks.config;

import com.chimericdream.bctweaks.ModInfo;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BCTweaksConfig {
    @SerialEntry
    public double beaconBaseRange = Defaults.BEACON_BASE_RANGE;
    @SerialEntry
    public double beaconRangePerLevel = Defaults.BEACON_RANGE_PER_LEVEL;
    @SerialEntry
    public boolean conduitAddVanillaRange = Defaults.CONDUIT_ADD_VANILLA_RANGE;
    @SerialEntry
    public transient Map<String, Double> beaconRangePerBlock = Defaults.BEACON_RANGE_PER_BLOCK;
    @SerialEntry
    public transient Map<String, Double> conduitRangePerBlock = Defaults.CONDUIT_RANGE_PER_BLOCK;

    public static ConfigClassHandler<BCTweaksConfig> HANDLER = ConfigClassHandler.createBuilder(BCTweaksConfig.class)
        .id(Identifier.of(ModInfo.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("beacon-conduit-tweaks.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static void load() {
        HANDLER.load();
    }

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
            .title(Text.translatable("text.config.bctweaks.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("text.config.bctweaks.title"))
                .option(Option.<Double>createBuilder()
                    .name(Text.translatable("text.config.bctweaks.option.beaconBaseRange"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.BEACON_BASE_RANGE, () -> config.beaconBaseRange, newVal -> config.beaconBaseRange = newVal)
                    .controller(opt -> DoubleFieldControllerBuilder.create(opt).min(10.0))
                    .build())
                .option(Option.<Double>createBuilder()
                    .name(Text.translatable("text.config.bctweaks.option.beaconRangePerLevel"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.BEACON_RANGE_PER_LEVEL, () -> config.beaconRangePerLevel, newVal -> config.beaconRangePerLevel = newVal)
                    .controller(opt -> DoubleFieldControllerBuilder.create(opt).min(10.0))
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.bctweaks.option.conduitAddVanillaRange"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.CONDUIT_ADD_VANILLA_RANGE, () -> config.conduitAddVanillaRange, newVal -> config.conduitAddVanillaRange = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
        )).generateScreen(parent);
    }

    public static class Defaults {
        public static double BEACON_BASE_RANGE = 10.0;
        public static double BEACON_RANGE_PER_LEVEL = 10.0;
        public static boolean CONDUIT_ADD_VANILLA_RANGE = true;
        public static Map<String, Double> BEACON_RANGE_PER_BLOCK = Map.of(
            "minecraft:iron_block", 0.0,
            "minecraft:gold_block", 0.0,
            "minecraft:diamond_block", 0.5,
            "minecraft:emerald_block", 0.5,
            "minecraft:netherite_block", 2.0
        );
        public static Map<String, Double> CONDUIT_RANGE_PER_BLOCK = Map.of();
    }
}
