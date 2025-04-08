package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashSet;

import static com.chimericdream.archaeologytweaks.ArchaeologyTweaksMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL);

    public static final RegistrySupplier<Block> SUSPICIOUS_CLAY = REGISTRY_HELPER.registerWithItem(SuspiciousClayBlock.BLOCK_ID, SuspiciousClayBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_DIRT = REGISTRY_HELPER.registerWithItem(SuspiciousDirtBlock.BLOCK_ID, SuspiciousDirtBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_MUD = REGISTRY_HELPER.registerWithItem(SuspiciousMudBlock.BLOCK_ID, SuspiciousMudBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_PACKED_MUD = REGISTRY_HELPER.registerWithItem(SuspiciousPackedMudBlock.BLOCK_ID, SuspiciousPackedMudBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_RED_SAND = REGISTRY_HELPER.registerWithItem(SuspiciousRedSandBlock.BLOCK_ID, SuspiciousRedSandBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_ROOTED_DIRT = REGISTRY_HELPER.registerWithItem(SuspiciousRootedDirtBlock.BLOCK_ID, SuspiciousRootedDirtBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_SOUL_SAND = REGISTRY_HELPER.registerWithItem(SuspiciousSoulSandBlock.BLOCK_ID, SuspiciousSoulSandBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_SOUL_SOIL = REGISTRY_HELPER.registerWithItem(SuspiciousSoulSoilBlock.BLOCK_ID, SuspiciousSoulSoilBlock::new, DEFAULT_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<ATBrushableBlockEntity>> BRUSHABLE_MOD_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        Identifier.of(ModInfo.MOD_ID, "brushable_block_entity_type"),
        () -> new BlockEntityType<>(
            ATBrushableBlockEntity::new,
            new HashSet<>(Arrays.asList(
                ModBlocks.SUSPICIOUS_CLAY.get(),
                ModBlocks.SUSPICIOUS_DIRT.get(),
                ModBlocks.SUSPICIOUS_MUD.get(),
                ModBlocks.SUSPICIOUS_PACKED_MUD.get(),
                ModBlocks.SUSPICIOUS_RED_SAND.get(),
                ModBlocks.SUSPICIOUS_ROOTED_DIRT.get(),
                ModBlocks.SUSPICIOUS_SOUL_SAND.get(),
                ModBlocks.SUSPICIOUS_SOUL_SOIL.get()
            ))
        )
    );

    public static void init() {
    }
}
