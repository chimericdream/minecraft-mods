package com.chimericdream.miniblockmerchants.fabric.client;

import com.chimericdream.lib.text.TextHelpers;
import com.chimericdream.miniblockmerchants.MiniblockMerchantsMod;
import com.chimericdream.miniblockmerchants.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.registry.entry.RegistryEntryList;

@Environment(EnvType.CLIENT)
public class MiniblockMerchantsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MiniblockMerchantsMod.LOGGER.info("Initializing client code");

        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (!itemStack.isIn(RegistryEntryList.of(
                ModItems.ANCIENT_SHELL_ITEM,
                ModItems.BOOK_OF_RITUALS_ITEM,
                ModItems.BUDDING_CACTUS_ITEM,
                ModItems.CRYSTAL_PHIAL_ITEM,
                ModItems.CULTIVATED_SAPLING_ITEM,
                ModItems.DRENCHED_SCORE_SHEET_ITEM,
                ModItems.ENCHANTED_RED_DELICIOUS_ITEM,
                ModItems.ENDLESS_BOOKSHELF_ITEM,
                ModItems.FINE_THREAD_ITEM,
                ModItems.FORGOTTEN_SCRAP_METAL_ITEM,
                ModItems.FRAGRANT_FLOWER_ITEM,
                ModItems.GALILEAN_SPYGLASS_ITEM,
                ModItems.MASTERCRAFTED_IRON_ITEM,
                ModItems.MIXOLOGY_STATION_ITEM,
                ModItems.OVERGROWN_CARROT_ITEM,
                ModItems.PRISMATIC_HONEYCOMB_ITEM,
                ModItems.PURE_GOLD_ITEM,
                ModItems.RADIATING_REDSTONE_ITEM,
                ModItems.ROTTING_RECYCLING_BIN_ITEM,
                ModItems.SCULPTING_CLAY_ITEM,
                ModItems.SHIMMERING_WHEAT_ITEM,
                ModItems.SOAKED_VILLAGER_PLUSHIE_ITEM,
                ModItems.SPARKLING_BLAZE_POWDER_ITEM,
                ModItems.STABILIZED_EXPLOSION_ITEM,
                ModItems.UNUSUALLY_DENSE_ROCK_ITEM,
                ModItems.WAGYU_BEEF_ITEM
            ))) {
                return;
            }

            list.add(TextHelpers.getTooltip("tooltip.conversion_item.lore"));
        });
    }
}
