package com.chimericdream.logallthethings.windowlog;

import java.util.Set;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.chimericdream.logallthethings.LogAllTheThingsMod.REGISTRY_HELPER;

public final class WindowLogBlocks {
    public static final RegistrySupplier<WindowedBlock> WINDOWED_BLOCK;
    public static final RegistrySupplier<BlockEntityType<WindowedBlockEntity>> WINDOWED_BLOCK_ENTITY;

    static {
        WINDOWED_BLOCK = REGISTRY_HELPER.registerBlock("windowed_block", WindowedBlock::new);
        WINDOWED_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
            "windowed_block",
            () -> new BlockEntityType<>(WindowedBlockEntity::new, Set.of(WINDOWED_BLOCK.get()))
        );
    }

    private WindowLogBlocks() {
    }

    public static void init() {
    }
}
