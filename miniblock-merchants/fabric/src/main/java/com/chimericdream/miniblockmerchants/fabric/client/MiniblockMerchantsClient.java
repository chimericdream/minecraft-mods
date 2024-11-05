package com.chimericdream.miniblockmerchants.fabric.client;

import com.chimericdream.miniblockmerchants.MiniblockMerchantsMod;
import com.chimericdream.miniblockmerchants.fabric.client.render.item.VillagerConversionItemRenderer;
import com.chimericdream.miniblockmerchants.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

@Environment(EnvType.CLIENT)
public class MiniblockMerchantsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MiniblockMerchantsMod.LOGGER.info("Initializing client code");

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ANCIENT_SHELL_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.BOOK_OF_RITUALS_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.BUDDING_CACTUS_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.CRYSTAL_PHIAL_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.CULTIVATED_SAPLING_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.DRENCHED_SCORE_SHEET_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ENCHANTED_RED_DELICIOUS_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ENDLESS_BOOKSHELF_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.FINE_THREAD_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.FORGOTTEN_SCRAP_METAL_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.FRAGRANT_FLOWER_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.GALILEAN_SPYGLASS_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.MASTERCRAFTED_IRON_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.MIXOLOGY_STATION_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.OVERGROWN_CARROT_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.PRISMATIC_HONEYCOMB_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.PURE_GOLD_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.RADIATING_REDSTONE_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ROTTING_RECYCLING_BIN_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SCULPTING_CLAY_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SHIMMERING_WHEAT_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SOAKED_VILLAGER_PLUSHIE_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SPARKLING_BLAZE_POWDER_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.STABILIZED_EXPLOSION_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.UNUSUALLY_DENSE_ROCK_ITEM.get(), new VillagerConversionItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WAGYU_BEEF_ITEM.get(), new VillagerConversionItemRenderer());
    }
}
