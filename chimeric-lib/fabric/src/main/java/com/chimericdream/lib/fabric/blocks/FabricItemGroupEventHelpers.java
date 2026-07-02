package com.chimericdream.lib.fabric.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class FabricItemGroupEventHelpers {
    public static void addBlockToItemGroup(Block block, ResourceKey<CreativeModeTab> group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(g -> g.accept(block));
    }

    public static void addBlockToItemGroups(Block block, List<ResourceKey<CreativeModeTab>> groups) {
        groups.forEach(
                group -> ItemGroupEvents
                        .modifyEntriesEvent(group)
                        .register(g -> g.accept(block))
        );
    }
}
