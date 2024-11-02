package com.chimericdream.lib.fabric.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;

import java.util.List;

public class FabricItemGroupEventHelpers {
    public static void addBlockToItemGroup(Block block, RegistryKey<ItemGroup> group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(g -> g.add(block));
    }

    public static void addBlockToItemGroups(Block block, List<RegistryKey<ItemGroup>> groups) {
        groups.forEach(
            group -> ItemGroupEvents
                .modifyEntriesEvent(group)
                .register(g -> g.add(block))
        );
    }
}
