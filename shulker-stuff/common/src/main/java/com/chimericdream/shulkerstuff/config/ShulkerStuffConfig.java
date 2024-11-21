package com.chimericdream.shulkerstuff.config;

import com.chimericdream.shulkerstuff.ModInfo;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ShulkerStuffConfig {
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

    public static ConfigClassHandler<ShulkerStuffConfig> HANDLER = ConfigClassHandler.createBuilder(ShulkerStuffConfig.class)
        .id(Identifier.of(ModInfo.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("shulkerstuff.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
            .title(Text.translatable("text.config.shulkerstuff.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("text.config.shulkerstuff.title"))
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.ancientShellChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.ANCIENT_SHELL_CHANCE, () -> config.ancientShellChance, newVal -> config.ancientShellChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.bookOfRitualsChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.BOOK_OF_RITUALS_CHANCE, () -> config.bookOfRitualsChance, newVal -> config.bookOfRitualsChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.buddingCactusChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.BUDDING_CACTUS_CHANCE, () -> config.buddingCactusChance, newVal -> config.buddingCactusChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.crystalPhialChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.CRYSTAL_PHIAL_CHANCE, () -> config.crystalPhialChance, newVal -> config.crystalPhialChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.cultivatedSaplingChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.CULTIVATED_SAPLING_CHANCE, () -> config.cultivatedSaplingChance, newVal -> config.cultivatedSaplingChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.drenchedScoreSheetChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.DRENCHED_SCORE_SHEET_CHANCE, () -> config.drenchedScoreSheetChance, newVal -> config.drenchedScoreSheetChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.enchantedRedDeliciousChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.ENCHANTED_RED_DELICIOUS_CHANCE, () -> config.enchantedRedDeliciousChance, newVal -> config.enchantedRedDeliciousChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.endlessBookshelfChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.ENDLESS_BOOKSHELF_CHANCE, () -> config.endlessBookshelfChance, newVal -> config.endlessBookshelfChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.fineThreadChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.FINE_THREAD_CHANCE, () -> config.fineThreadChance, newVal -> config.fineThreadChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.forgottenScrapMetalChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.FORGOTTEN_SCRAP_METAL_CHANCE, () -> config.forgottenScrapMetalChance, newVal -> config.forgottenScrapMetalChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.fragrantFlowerChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.FRAGRANT_FLOWER_CHANCE, () -> config.fragrantFlowerChance, newVal -> config.fragrantFlowerChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.galileanSpyglassChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.GALILEAN_SPYGLASS_CHANCE, () -> config.galileanSpyglassChance, newVal -> config.galileanSpyglassChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.mastercraftedIronChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.MASTERCRAFTED_IRON_CHANCE, () -> config.mastercraftedIronChance, newVal -> config.mastercraftedIronChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.mixologyStationChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.MIXOLOGY_STATION_CHANCE, () -> config.mixologyStationChance, newVal -> config.mixologyStationChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.overgrownCarrotChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.OVERGROWN_CARROT_CHANCE, () -> config.overgrownCarrotChance, newVal -> config.overgrownCarrotChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.prismaticHoneycombChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.PRISMATIC_HONEYCOMB_CHANCE, () -> config.prismaticHoneycombChance, newVal -> config.prismaticHoneycombChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.pureGoldChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.PURE_GOLD_CHANCE, () -> config.pureGoldChance, newVal -> config.pureGoldChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.radiatingRedstoneChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.RADIATING_REDSTONE_CHANCE, () -> config.radiatingRedstoneChance, newVal -> config.radiatingRedstoneChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.rottingRecyclingBinChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.ROTTING_RECYCLING_BIN_CHANCE, () -> config.rottingRecyclingBinChance, newVal -> config.rottingRecyclingBinChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.sculptingClayChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.SCULPTING_CLAY_CHANCE, () -> config.sculptingClayChance, newVal -> config.sculptingClayChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.shimmeringWheatChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.SHIMMERING_WHEAT_CHANCE, () -> config.shimmeringWheatChance, newVal -> config.shimmeringWheatChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.soakedVillagerPlushieChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.SOAKED_VILLAGER_PLUSHIE_CHANCE, () -> config.soakedVillagerPlushieChance, newVal -> config.soakedVillagerPlushieChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.sparklingBlazePowderChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.SPARKLING_BLAZE_POWDER_CHANCE, () -> config.sparklingBlazePowderChance, newVal -> config.sparklingBlazePowderChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.stabilizedExplosionChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.STABILIZED_EXPLOSION_CHANCE, () -> config.stabilizedExplosionChance, newVal -> config.stabilizedExplosionChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.unusuallyDenseRockChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.UNUSUALLY_DENSE_ROCK_CHANCE, () -> config.unusuallyDenseRockChance, newVal -> config.unusuallyDenseRockChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.shulkerstuff.option.wagyuBeefChance"))
                    .description(OptionDescription.of(Text.literal("")))
                    .binding(Defaults.WAGYU_BEEF_CHANCE, () -> config.wagyuBeefChance, newVal -> config.wagyuBeefChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .build())
        )).generateScreen(parent);
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
