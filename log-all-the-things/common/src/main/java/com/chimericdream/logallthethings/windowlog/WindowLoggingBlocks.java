package com.chimericdream.logallthethings.windowlog;

import java.util.Set;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.chimericdream.logallthethings.LogAllTheThingsMod.REGISTRY_HELPER;

public final class WindowLoggingBlocks {
    public static final RegistrySupplier<WindowLoggedBlock> WINDOW_LOGGED_BLOCK;
    public static final RegistrySupplier<BlockEntityType<WindowLoggedBlockEntity>> WINDOW_LOGGED_BLOCK_ENTITY;

    static {
        WINDOW_LOGGED_BLOCK = REGISTRY_HELPER.registerBlock("window_logged_block", WindowLoggedBlock::new);
        WINDOW_LOGGED_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
            "window_ed_block",
            () -> new BlockEntityType<>(WindowLoggedBlockEntity::new, Set.of(WINDOW_LOGGED_BLOCK.get()))
        );
    }

    private WindowLoggingBlocks() {
    }

    public static void init() {
    }
}
