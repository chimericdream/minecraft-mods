package com.chimericdream.miniblockmerchants.loot;

import com.chimericdream.miniblockmerchants.config.MiniblockMerchantsConfig;
import com.chimericdream.miniblockmerchants.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.miniblockmerchants.loot.MobHeadLootTables.getVillagerHeadLootTable;
import static com.chimericdream.miniblockmerchants.loot.MobHeadLootTables.getZombieVillagerHeadLootTable;

public class MMLootTables {
    private static final Identifier CARROT_LOOT_TABLE_ID = Blocks.CARROTS.getLootTableKey().get().getValue();
    private static final Identifier CLAY_LOOT_TABLE_ID = Blocks.CLAY.getLootTableKey().get().getValue();
    private static final Identifier CREEPER_LOOT_TABLE_ID = EntityType.CREEPER.getLootTableKey().get().getValue();
    private static final Identifier OAK_LEAVES_TABLE_ID = Blocks.OAK_LEAVES.getLootTableKey().get().getValue();
    private static final Identifier REDSTONE_ORE_TABLE_ID = Blocks.REDSTONE_ORE.getLootTableKey().get().getValue();
    private static final Identifier SPRUCE_LEAVES_TABLE_ID = Blocks.SPRUCE_LEAVES.getLootTableKey().get().getValue();
    private static final Identifier VILLAGER_LOOT_TABLE_ID = EntityType.VILLAGER.getLootTableKey().get().getValue();
    private static final Identifier WHEAT_TABLE_ID = Blocks.WHEAT.getLootTableKey().get().getValue();
    private static final Identifier ZOMBIE_VILLAGER_LOOT_TABLE_ID = EntityType.ZOMBIE_VILLAGER.getLootTableKey().get().getValue();

    private static LootPool.Builder makeBuilder(Item item, int chance) {
        LootPool.Builder builder = LootPool.builder()
            .with(ItemEntry.builder(item));

        if (chance == 1) {
            return builder;
        }

        return builder.with(ItemEntry.builder(Items.AIR).weight(chance - 1));
    }

    private static void checkVanillaLootTables(Identifier id, List<LootPool.Builder> poolBuilders, MiniblockMerchantsConfig config, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (config.overgrownCarrotChance > 0 && CARROT_LOOT_TABLE_ID.equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.OVERGROWN_CARROT_ITEM.get(), config.overgrownCarrotChance));
        }

        if (config.sculptingClayChance > 0 && CLAY_LOOT_TABLE_ID.equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.SCULPTING_CLAY_ITEM.get(), config.sculptingClayChance));
        }

        if (config.enchantedRedDeliciousChance > 0 && OAK_LEAVES_TABLE_ID.equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.ENCHANTED_RED_DELICIOUS_ITEM.get(), config.enchantedRedDeliciousChance));
        }

        if (config.radiatingRedstoneChance > 0 && REDSTONE_ORE_TABLE_ID.equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.RADIATING_REDSTONE_ITEM.get(), config.radiatingRedstoneChance));
        }

        if (config.cultivatedSaplingChance > 0 && SPRUCE_LEAVES_TABLE_ID.equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.CULTIVATED_SAPLING_ITEM.get(), config.cultivatedSaplingChance));
        }

        if (config.shimmeringWheatChance > 0 && WHEAT_TABLE_ID.equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.SHIMMERING_WHEAT_ITEM.get(), config.shimmeringWheatChance));
        }

        if (LootTables.FISHING_TREASURE_GAMEPLAY.getValue().equals(id)) {
            if (config.forgottenScrapMetalChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.FORGOTTEN_SCRAP_METAL_ITEM.get(), config.forgottenScrapMetalChance));
            }

            if (config.soakedVillagerPlushieChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.SOAKED_VILLAGER_PLUSHIE_ITEM.get(), config.soakedVillagerPlushieChance));
            }

            if (config.drenchedScoreSheetChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.DRENCHED_SCORE_SHEET_ITEM.get(), config.drenchedScoreSheetChance));
            }

            if (config.rottingRecyclingBinChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.ROTTING_RECYCLING_BIN_ITEM.get(), config.rottingRecyclingBinChance));
            }

            if (config.crystalPhialChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.CRYSTAL_PHIAL_ITEM.get(), config.crystalPhialChance));
            }

            if (config.mixologyStationChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.MIXOLOGY_STATION_ITEM.get(), config.mixologyStationChance));
            }
        }

        if (config.mastercraftedIronChance > 0 && LootTables.VILLAGE_ARMORER_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.MASTERCRAFTED_IRON_ITEM.get(), config.mastercraftedIronChance));
        }

        if (config.wagyuBeefChance > 0 && LootTables.VILLAGE_BUTCHER_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.WAGYU_BEEF_ITEM.get(), config.wagyuBeefChance));
        }

        if (config.fineThreadChance > 0 && LootTables.VILLAGE_SHEPARD_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.FINE_THREAD_ITEM.get(), config.fineThreadChance));
        }

        if (config.unusuallyDenseRockChance > 0 && LootTables.ABANDONED_MINESHAFT_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.UNUSUALLY_DENSE_ROCK_ITEM.get(), config.unusuallyDenseRockChance));
        }

        if (config.buddingCactusChance > 0 && LootTables.DESERT_PYRAMID_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.BUDDING_CACTUS_ITEM.get(), config.buddingCactusChance));
        }

        if (config.galileanSpyglassChance > 0 && LootTables.IGLOO_CHEST_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.GALILEAN_SPYGLASS_ITEM.get(), config.galileanSpyglassChance));
        }

        if (config.prismaticHoneycombChance > 0 && LootTables.JUNGLE_TEMPLE_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.PRISMATIC_HONEYCOMB_ITEM.get(), config.prismaticHoneycombChance));
        }

        if (config.fragrantFlowerChance > 0 && LootTables.PILLAGER_OUTPOST_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.FRAGRANT_FLOWER_ITEM.get(), config.fragrantFlowerChance));
        }

        if (config.sparklingBlazePowderChance > 0 && LootTables.RUINED_PORTAL_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.SPARKLING_BLAZE_POWDER_ITEM.get(), config.sparklingBlazePowderChance));
        }

        if (config.pureGoldChance > 0 && LootTables.SIMPLE_DUNGEON_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.PURE_GOLD_ITEM.get(), config.pureGoldChance));
        }

        if (LootTables.STRONGHOLD_LIBRARY_CHEST.getValue().equals(id)) {
            if (config.endlessBookshelfChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.ENDLESS_BOOKSHELF_ITEM.get(), config.endlessBookshelfChance));
            }

            if (config.bookOfRitualsChance > 0) {
                poolBuilders.add(makeBuilder(ModItems.BOOK_OF_RITUALS_ITEM.get(), config.bookOfRitualsChance));
            }
        }

        if (config.ancientShellChance > 0 && LootTables.UNDERWATER_RUIN_BIG_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.ANCIENT_SHELL_ITEM.get(), config.ancientShellChance));
        }

        if (CREEPER_LOOT_TABLE_ID.equals(id)) {
            poolBuilders.add(makeBuilder(ModItems.STABILIZED_EXPLOSION_ITEM.get(), config.stabilizedExplosionChance));
        }

        if (VILLAGER_LOOT_TABLE_ID.equals(id)) {
            poolBuilders.add(getVillagerHeadLootTable());
        }

        if (ZOMBIE_VILLAGER_LOOT_TABLE_ID.equals(id)) {
            poolBuilders.add(getZombieVillagerHeadLootTable(wrapperLookup));
        }
    }

    public static List<LootPool.Builder> generatePoolbuilders(Identifier id, RegistryWrapper.WrapperLookup wrapperLookup) {
        MiniblockMerchantsConfig config = MiniblockMerchantsConfig.HANDLER.instance();

        List<LootPool.Builder> poolBuilders = new ArrayList<>();

        checkVanillaLootTables(id, poolBuilders, config, wrapperLookup);

        return poolBuilders;
    }

    public static void modifyLootTables(RegistryKey<LootTable> id, LootTable.Builder tableBuilder, RegistryWrapper.WrapperLookup wrapperLookup) {
        modifyLootTables(id.getValue(), tableBuilder, wrapperLookup);
    }

    public static void modifyLootTables(Identifier id, LootTable.Builder tableBuilder, RegistryWrapper.WrapperLookup wrapperLookup) {
        List<LootPool.Builder> poolBuilders = generatePoolbuilders(id, wrapperLookup);

        for (LootPool.Builder builder : poolBuilders) {
            tableBuilder.pool(builder);
        }
    }
}
