package com.chimericdream.jdcrafte.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.jdcrafte.JDCrafteMod.REGISTRY_HELPER;

public class ModBlocks {
    public static final RegistrySupplier<Block> FEEDING_TROUGH = REGISTRY_HELPER.registerWithItem(
        "feeding_trough",
        () -> new FeedingTroughBlock(
            BlockBehaviour.Properties
                .of()
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .setId(ResourceKey.create(Registries.BLOCK, REGISTRY_HELPER.makeId("feeding_trough")))
        ),
        new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
    );

    public static void init() {
    }
}
