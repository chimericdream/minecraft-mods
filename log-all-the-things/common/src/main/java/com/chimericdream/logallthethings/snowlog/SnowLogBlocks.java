package com.chimericdream.logallthethings.snowlog;

import java.util.Set;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.chimericdream.logallthethings.LogAllTheThingsMod.REGISTRY_HELPER;

public final class SnowLogBlocks {
    public static final RegistrySupplier<SnowedBlock> SNOWED_BLOCK;
    public static final RegistrySupplier<BlockEntityType<SnowedBlockEntity>> SNOWED_BLOCK_ENTITY;

    static {
        SNOWED_BLOCK = REGISTRY_HELPER.registerBlock("snowed_block", SnowedBlock::new);
        SNOWED_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
            "snowed_block",
            () -> new BlockEntityType<>(SnowedBlockEntity::new, Set.of(SNOWED_BLOCK.get()))
        );
    }

    private SnowLogBlocks() {
    }

    public static void init() {
    }
}
