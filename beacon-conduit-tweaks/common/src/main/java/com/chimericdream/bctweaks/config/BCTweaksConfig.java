package com.chimericdream.bctweaks.config;

import com.chimericdream.bctweaks.BeaconConduitTweaksMod;
import com.chimericdream.bctweaks.ModInfo;
import com.chimericdream.lib.config.YaclConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BCTweaksConfig {
    @SerialEntry
    public double beaconBaseRange = Defaults.BEACON_BASE_RANGE;
    @SerialEntry
    public double beaconRangePerLevel = Defaults.BEACON_RANGE_PER_LEVEL;
    @SerialEntry
    public boolean conduitAddVanillaRange = Defaults.CONDUIT_ADD_VANILLA_RANGE;
    @SerialEntry
    public Map<String, Double> beaconRangePerBlock = Defaults.BEACON_RANGE_PER_BLOCK;
    @SerialEntry
    public Map<String, Double> conduitRangePerBlock = Defaults.CONDUIT_RANGE_PER_BLOCK;

    public static final YaclConfig<BCTweaksConfig> CONFIG = YaclConfig.builder(BCTweaksConfig.class, ModInfo.MOD_ID)
        .fileName("beacon-conduit-tweaks.json5")
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.bctweaks.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.bctweaks.title"))
                .option(Option.<Double>createBuilder()
                    .name(Component.translatable("text.config.bctweaks.option.beaconBaseRange"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.BEACON_BASE_RANGE, () -> config.beaconBaseRange, newVal -> config.beaconBaseRange = newVal)
                    .controller(opt -> DoubleFieldControllerBuilder.create(opt).min(10.0))
                    .build())
                .option(Option.<Double>createBuilder()
                    .name(Component.translatable("text.config.bctweaks.option.beaconRangePerLevel"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.BEACON_RANGE_PER_LEVEL, () -> config.beaconRangePerLevel, newVal -> config.beaconRangePerLevel = newVal)
                    .controller(opt -> DoubleFieldControllerBuilder.create(opt).min(10.0))
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.bctweaks.option.conduitAddVanillaRange"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.CONDUIT_ADD_VANILLA_RANGE, () -> config.conduitAddVanillaRange, newVal -> config.conduitAddVanillaRange = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .group(rangeList(
                    "beaconRangePerBlock",
                    Defaults.BEACON_RANGE_PER_BLOCK,
                    () -> config.beaconRangePerBlock,
                    newVal -> config.beaconRangePerBlock = newVal
                ))
                .group(rangeList(
                    "conduitRangePerBlock",
                    Defaults.CONDUIT_RANGE_PER_BLOCK,
                    () -> config.conduitRangePerBlock,
                    newVal -> config.conduitRangePerBlock = newVal
                ))
                .build())
        )
        .build();

    /**
     * YACL has no map control, so a per-block map is edited as a list of {@code block_id=range} lines.
     * Lines that don't parse are dropped when the screen is saved.
     */
    private static ListOption<String> rangeList(
        String field,
        Map<String, Double> defaults,
        Supplier<Map<String, Double>> getter,
        Consumer<Map<String, Double>> setter
    ) {
        return ListOption.<String>createBuilder()
            .name(Component.translatable("text.config.bctweaks.option." + field))
            .description(OptionDescription.of(Component.translatable("text.config.bctweaks.option." + field + ".description")))
            .binding(
                RangeEntries.toLines(defaults),
                () -> RangeEntries.toLines(getter.get()),
                lines -> setter.accept(RangeEntries.fromLines(lines, BCTweaksConfig::isKeepable))
            )
            .controller(StringControllerBuilder::create)
            .initial("minecraft:stone=0.0")
            .collapsed(true)
            .build();
    }

    /**
     * A vanilla id has to name a real block, but another mod's block may not be registered yet when the screen
     * is saved (or the mod may be missing for now), so those ids are kept.
     */
    private static boolean isKeepable(String id) {
        Identifier identifier = Identifier.tryParse(id);
        if (identifier == null) {
            return false;
        }

        return !identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) || BuiltInRegistries.BLOCK.containsKey(identifier);
    }

    /** Logs a warning for each configured block that is not registered. Those entries are ignored. */
    public static void warnAboutUnknownBlocks(BCTweaksConfig config) {
        warnAboutUnknownBlocks("Beacon Range / Block", config.beaconRangePerBlock);
        warnAboutUnknownBlocks("Conduit Range / Block", config.conduitRangePerBlock);
    }

    private static void warnAboutUnknownBlocks(String option, Map<String, Double> map) {
        for (String id : map.keySet()) {
            Identifier identifier = Identifier.tryParse(id);
            if (identifier == null || !BuiltInRegistries.BLOCK.containsKey(identifier)) {
                BeaconConduitTweaksMod.LOGGER.warn("{}: ignoring '{}' because no block with that id exists.", option, id);
            }
        }
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
