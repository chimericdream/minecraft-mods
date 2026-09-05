package com.chimericdream.bettertargetdummies.block;

import com.chimericdream.bettertargetdummies.block.entity.TargetDummyBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

import static com.chimericdream.bettertargetdummies.BetterTargetDummiesMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.COMBAT);

    public static final RegistrySupplier<Block> TARGET_DUMMY = REGISTRY_HELPER.registerWithItem(TargetDummyBlock.BLOCK_ID, TargetDummyBlock::new, DEFAULT_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<TargetDummyBlockEntity>> TARGET_DUMMY_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        TargetDummyBlockEntity.ENTITY_ID,
        () -> new BlockEntityType<>(
            TargetDummyBlockEntity::new,
            Set.of(TARGET_DUMMY.get())
        )
    );

    public static void init() {
    }
}
