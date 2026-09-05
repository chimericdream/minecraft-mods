package com.chimericdream.logallthethings.carpetlog;

import java.util.Set;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.chimericdream.logallthethings.LogAllTheThingsMod.REGISTRY_HELPER;

public final class CarpetLogBlocks {
    public static final RegistrySupplier<CarpetedBlock> CARPETED_BLOCK;
    public static final RegistrySupplier<BlockEntityType<CarpetedBlockEntity>> CARPETED_BLOCK_ENTITY;

    static {
        CARPETED_BLOCK = REGISTRY_HELPER.registerBlock("carpeted_block", CarpetedBlock::new);
        CARPETED_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
            "carpeted_block",
            () -> new BlockEntityType<>(CarpetedBlockEntity::new, Set.of(CARPETED_BLOCK.get()))
        );
    }

    private CarpetLogBlocks() {
    }

    public static void init() {
    }
}
