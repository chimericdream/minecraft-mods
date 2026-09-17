package com.chimericdream.miniblockmerchants.config;

import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.lib.config.YaclConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MiniblockMerchantsConfig {
    @SerialEntry
    public int ancientShellChance = Defaults.ANCIENT_SHELL_CHANCE;
    @SerialEntry
    public int bookOfRitualsChance = Defaults.BOOK_OF_RITUALS_CHANCE;
    @SerialEntry
    public int buddingCactusChance = Defaults.BUDDING_CACTUS_CHANCE;
    @SerialEntry
    public int crystalPhialChance = Defaults.CRYSTAL_PHIAL_CHANCE;
    @SerialEntry
    public int cultivatedSaplingChance = Defaults.CULTIVATED_SAPLING_CHANCE;
    @SerialEntry
    public int drenchedScoreSheetChance = Defaults.DRENCHED_SCORE_SHEET_CHANCE;
    @SerialEntry
    public int enchantedRedDeliciousChance = Defaults.ENCHANTED_RED_DELICIOUS_CHANCE;
    @SerialEntry
    public int endlessBookshelfChance = Defaults.ENDLESS_BOOKSHELF_CHANCE;
    @SerialEntry
    public int fineThreadChance = Defaults.FINE_THREAD_CHANCE;
    @SerialEntry
    public int forgottenScrapMetalChance = Defaults.FORGOTTEN_SCRAP_METAL_CHANCE;
    @SerialEntry
    public int fragrantFlowerChance = Defaults.FRAGRANT_FLOWER_CHANCE;
    @SerialEntry
    public int galileanSpyglassChance = Defaults.GALILEAN_SPYGLASS_CHANCE;
    @SerialEntry
    public int mastercraftedIronChance = Defaults.MASTERCRAFTED_IRON_CHANCE;
    @SerialEntry
    public int mixologyStationChance = Defaults.MIXOLOGY_STATION_CHANCE;
    @SerialEntry
    public int overgrownCarrotChance = Defaults.OVERGROWN_CARROT_CHANCE;
    @SerialEntry
    public int prismaticHoneycombChance = Defaults.PRISMATIC_HONEYCOMB_CHANCE;
    @SerialEntry
    public int pureGoldChance = Defaults.PURE_GOLD_CHANCE;
    @SerialEntry
    public int radiatingRedstoneChance = Defaults.RADIATING_REDSTONE_CHANCE;
    @SerialEntry
    public int rottingRecyclingBinChance = Defaults.ROTTING_RECYCLING_BIN_CHANCE;
    @SerialEntry
    public int sculptingClayChance = Defaults.SCULPTING_CLAY_CHANCE;
    @SerialEntry
    public int shimmeringWheatChance = Defaults.SHIMMERING_WHEAT_CHANCE;
    @SerialEntry
    public int soakedVillagerPlushieChance = Defaults.SOAKED_VILLAGER_PLUSHIE_CHANCE;
    @SerialEntry
    public int sparklingBlazePowderChance = Defaults.SPARKLING_BLAZE_POWDER_CHANCE;
    @SerialEntry
    public int stabilizedExplosionChance = Defaults.STABILIZED_EXPLOSION_CHANCE;
    @SerialEntry
    public int unusuallyDenseRockChance = Defaults.UNUSUALLY_DENSE_ROCK_CHANCE;
    @SerialEntry
    public int wagyuBeefChance = Defaults.WAGYU_BEEF_CHANCE;

    public static final YaclConfig<MiniblockMerchantsConfig> CONFIG = YaclConfig.builder(MiniblockMerchantsConfig.class, ModInfo.MOD_ID)
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.miniblockmerchants.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.miniblockmerchants.title"))
                .option(chanceOption("ancientShellChance", Defaults.ANCIENT_SHELL_CHANCE, () -> config.ancientShellChance, v -> config.ancientShellChance = v))
                .option(chanceOption("bookOfRitualsChance", Defaults.BOOK_OF_RITUALS_CHANCE, () -> config.bookOfRitualsChance, v -> config.bookOfRitualsChance = v))
                .option(chanceOption("buddingCactusChance", Defaults.BUDDING_CACTUS_CHANCE, () -> config.buddingCactusChance, v -> config.buddingCactusChance = v))
                .option(chanceOption("crystalPhialChance", Defaults.CRYSTAL_PHIAL_CHANCE, () -> config.crystalPhialChance, v -> config.crystalPhialChance = v))
                .option(chanceOption("cultivatedSaplingChance", Defaults.CULTIVATED_SAPLING_CHANCE, () -> config.cultivatedSaplingChance, v -> config.cultivatedSaplingChance = v))
                .option(chanceOption("drenchedScoreSheetChance", Defaults.DRENCHED_SCORE_SHEET_CHANCE, () -> config.drenchedScoreSheetChance, v -> config.drenchedScoreSheetChance = v))
                .option(chanceOption("enchantedRedDeliciousChance", Defaults.ENCHANTED_RED_DELICIOUS_CHANCE, () -> config.enchantedRedDeliciousChance, v -> config.enchantedRedDeliciousChance = v))
                .option(chanceOption("endlessBookshelfChance", Defaults.ENDLESS_BOOKSHELF_CHANCE, () -> config.endlessBookshelfChance, v -> config.endlessBookshelfChance = v))
                .option(chanceOption("fineThreadChance", Defaults.FINE_THREAD_CHANCE, () -> config.fineThreadChance, v -> config.fineThreadChance = v))
                .option(chanceOption("forgottenScrapMetalChance", Defaults.FORGOTTEN_SCRAP_METAL_CHANCE, () -> config.forgottenScrapMetalChance, v -> config.forgottenScrapMetalChance = v))
                .option(chanceOption("fragrantFlowerChance", Defaults.FRAGRANT_FLOWER_CHANCE, () -> config.fragrantFlowerChance, v -> config.fragrantFlowerChance = v))
                .option(chanceOption("galileanSpyglassChance", Defaults.GALILEAN_SPYGLASS_CHANCE, () -> config.galileanSpyglassChance, v -> config.galileanSpyglassChance = v))
                .option(chanceOption("mastercraftedIronChance", Defaults.MASTERCRAFTED_IRON_CHANCE, () -> config.mastercraftedIronChance, v -> config.mastercraftedIronChance = v))
                .option(chanceOption("mixologyStationChance", Defaults.MIXOLOGY_STATION_CHANCE, () -> config.mixologyStationChance, v -> config.mixologyStationChance = v))
                .option(chanceOption("overgrownCarrotChance", Defaults.OVERGROWN_CARROT_CHANCE, () -> config.overgrownCarrotChance, v -> config.overgrownCarrotChance = v))
                .option(chanceOption("prismaticHoneycombChance", Defaults.PRISMATIC_HONEYCOMB_CHANCE, () -> config.prismaticHoneycombChance, v -> config.prismaticHoneycombChance = v))
                .option(chanceOption("pureGoldChance", Defaults.PURE_GOLD_CHANCE, () -> config.pureGoldChance, v -> config.pureGoldChance = v))
                .option(chanceOption("radiatingRedstoneChance", Defaults.RADIATING_REDSTONE_CHANCE, () -> config.radiatingRedstoneChance, v -> config.radiatingRedstoneChance = v))
                .option(chanceOption("rottingRecyclingBinChance", Defaults.ROTTING_RECYCLING_BIN_CHANCE, () -> config.rottingRecyclingBinChance, v -> config.rottingRecyclingBinChance = v))
                .option(chanceOption("sculptingClayChance", Defaults.SCULPTING_CLAY_CHANCE, () -> config.sculptingClayChance, v -> config.sculptingClayChance = v))
                .option(chanceOption("shimmeringWheatChance", Defaults.SHIMMERING_WHEAT_CHANCE, () -> config.shimmeringWheatChance, v -> config.shimmeringWheatChance = v))
                .option(chanceOption("soakedVillagerPlushieChance", Defaults.SOAKED_VILLAGER_PLUSHIE_CHANCE, () -> config.soakedVillagerPlushieChance, v -> config.soakedVillagerPlushieChance = v))
                .option(chanceOption("sparklingBlazePowderChance", Defaults.SPARKLING_BLAZE_POWDER_CHANCE, () -> config.sparklingBlazePowderChance, v -> config.sparklingBlazePowderChance = v))
                .option(chanceOption("stabilizedExplosionChance", Defaults.STABILIZED_EXPLOSION_CHANCE, () -> config.stabilizedExplosionChance, v -> config.stabilizedExplosionChance = v))
                .option(chanceOption("unusuallyDenseRockChance", Defaults.UNUSUALLY_DENSE_ROCK_CHANCE, () -> config.unusuallyDenseRockChance, v -> config.unusuallyDenseRockChance = v))
                .option(chanceOption("wagyuBeefChance", Defaults.WAGYU_BEEF_CHANCE, () -> config.wagyuBeefChance, v -> config.wagyuBeefChance = v))
                .build())
        )
        .build();

    private static Option<Integer> chanceOption(String field, int defaultValue, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
            .name(Component.translatable("text.config.miniblockmerchants.option." + field))
            .description(OptionDescription.of(Component.literal("")))
            .binding(defaultValue, getter, setter)
            .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
            .build();
    }

    public static class Defaults {
        public static int ANCIENT_SHELL_CHANCE = 2;
        public static int BOOK_OF_RITUALS_CHANCE = 2;
        public static int BUDDING_CACTUS_CHANCE = 12;
        public static int CRYSTAL_PHIAL_CHANCE = 24;
        public static int CULTIVATED_SAPLING_CHANCE = 4096;
        public static int DRENCHED_SCORE_SHEET_CHANCE = 24;
        public static int ENCHANTED_RED_DELICIOUS_CHANCE = 4096;
        public static int ENDLESS_BOOKSHELF_CHANCE = 2;
        public static int FINE_THREAD_CHANCE = 2;
        public static int FORGOTTEN_SCRAP_METAL_CHANCE = 24;
        public static int FRAGRANT_FLOWER_CHANCE = 2;
        public static int GALILEAN_SPYGLASS_CHANCE = 1;
        public static int MASTERCRAFTED_IRON_CHANCE = 2;
        public static int MIXOLOGY_STATION_CHANCE = 24;
        public static int OVERGROWN_CARROT_CHANCE = 512;
        public static int PRISMATIC_HONEYCOMB_CHANCE = 1;
        public static int PURE_GOLD_CHANCE = 4;
        public static int RADIATING_REDSTONE_CHANCE = 256;
        public static int ROTTING_RECYCLING_BIN_CHANCE = 24;
        public static int SCULPTING_CLAY_CHANCE = 256;
        public static int SHIMMERING_WHEAT_CHANCE = 512;
        public static int SOAKED_VILLAGER_PLUSHIE_CHANCE = 24;
        public static int SPARKLING_BLAZE_POWDER_CHANCE = 4;
        public static int STABILIZED_EXPLOSION_CHANCE = 160;
        public static int UNUSUALLY_DENSE_ROCK_CHANCE = 12;
        public static int WAGYU_BEEF_CHANCE = 2;
    }
}
