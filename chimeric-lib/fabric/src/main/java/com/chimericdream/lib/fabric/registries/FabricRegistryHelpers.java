package com.chimericdream.lib.fabric.registries;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;

public class FabricRegistryHelpers {
    public static void registerFlammableBlock(Block block) {
        registerFlammableBlock(block, 300, 30, 20);
    }

    public static void registerFlammableBlock(Block block, int burnTime) {
        registerFlammableBlock(block, burnTime, 30, 20);
    }

    public static void registerFlammableBlock(Block block, int burnChance, int spreadChance) {
        registerFlammableBlock(block, 300, burnChance, spreadChance);
    }

    public static void registerFlammableBlock(Block block, int burnTime, int burnChance, int spreadChance) {
        FuelRegistry.INSTANCE.add(block, burnTime);
        FlammableBlockRegistry.getDefaultInstance().add(block, burnChance, spreadChance);
    }
}
