package com.chimericdream.forwardingaddress.neoforge.event;

import com.chimericdream.forwardingaddress.ModInfo;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import com.chimericdream.forwardingaddress.migration.RetiredItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class ForwardingAddressEventHandler {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            MigrationEvents.onChunkLoaded(level, event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        RetiredItems.appendTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        for (CreativeModeTab.TabVisibility visibility : CreativeModeTab.TabVisibility.values()) {
            event.removeIf(RetiredItems::isRetired, visibility);
        }
    }
}
