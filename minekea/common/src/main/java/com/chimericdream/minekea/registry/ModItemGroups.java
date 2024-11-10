package com.chimericdream.minekea.registry;

import com.chimericdream.minekea.block.building.beams.Beams;
import com.chimericdream.minekea.block.building.compressed.CompressedBlocks;
import com.chimericdream.minekea.block.building.covers.Covers;
import com.chimericdream.minekea.block.building.dyed.DyedBlocks;
import com.chimericdream.minekea.block.furniture.tables.Tables;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.ItemGroup;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class ModItemGroups {
    public static final RegistrySupplier<ItemGroup> BEAMS_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.minekea.blocks.building.beams",
        () -> Beams.BLOCKS.getFirst().get()
    );

    public static final RegistrySupplier<ItemGroup> COMPRESSED_BLOCK_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.minekea.blocks.building.compressed",
        () -> CompressedBlocks.BLOCKS.getFirst().get()
    );

    public static final RegistrySupplier<ItemGroup> COVERS_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.minekea.blocks.building.covers",
        () -> Covers.BLOCKS.getFirst().get()
    );

    public static RegistrySupplier<ItemGroup> DYED_BLOCK_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.minekea.blocks.building.dyed",
        () -> DyedBlocks.BLOCKS.getFirst().get()
    );

    public static final RegistrySupplier<ItemGroup> FURNITURE_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.minekea.blocks.furniture",
        () -> Tables.BLOCKS.getFirst().get()
    );

    public static void init() {
    }
}
