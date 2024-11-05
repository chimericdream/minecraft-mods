package com.chimericdream.miniblockmerchants.neoforge.client;

import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.item.ModItems;
import com.chimericdream.miniblockmerchants.neoforge.client.render.item.VillagerConversionItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class MiniblockMerchantsNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(RegisterEvent event) {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(
            new VillagerConversionItemRenderer.VillagerConversionItemRendererExtension(),
            ModItems.ANCIENT_SHELL_ITEM.get(),
            ModItems.BOOK_OF_RITUALS_ITEM.get(),
            ModItems.BUDDING_CACTUS_ITEM.get(),
            ModItems.CRYSTAL_PHIAL_ITEM.get(),
            ModItems.CULTIVATED_SAPLING_ITEM.get(),
            ModItems.DRENCHED_SCORE_SHEET_ITEM.get(),
            ModItems.ENCHANTED_RED_DELICIOUS_ITEM.get(),
            ModItems.ENDLESS_BOOKSHELF_ITEM.get(),
            ModItems.FINE_THREAD_ITEM.get(),
            ModItems.FORGOTTEN_SCRAP_METAL_ITEM.get(),
            ModItems.FRAGRANT_FLOWER_ITEM.get(),
            ModItems.GALILEAN_SPYGLASS_ITEM.get(),
            ModItems.MASTERCRAFTED_IRON_ITEM.get(),
            ModItems.MIXOLOGY_STATION_ITEM.get(),
            ModItems.OVERGROWN_CARROT_ITEM.get(),
            ModItems.PRISMATIC_HONEYCOMB_ITEM.get(),
            ModItems.PURE_GOLD_ITEM.get(),
            ModItems.RADIATING_REDSTONE_ITEM.get(),
            ModItems.ROTTING_RECYCLING_BIN_ITEM.get(),
            ModItems.SCULPTING_CLAY_ITEM.get(),
            ModItems.SHIMMERING_WHEAT_ITEM.get(),
            ModItems.SOAKED_VILLAGER_PLUSHIE_ITEM.get(),
            ModItems.SPARKLING_BLAZE_POWDER_ITEM.get(),
            ModItems.STABILIZED_EXPLOSION_ITEM.get(),
            ModItems.UNUSUALLY_DENSE_ROCK_ITEM.get(),
            ModItems.WAGYU_BEEF_ITEM.get()
        );
    }
}
