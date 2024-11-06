package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import static com.chimericdream.archaeologytweaks.registry.ModRegistries.registerBlockEntity;
import static com.chimericdream.archaeologytweaks.registry.ModRegistries.registerWithItem;

public class ModBlocks {
    private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL);

    public static final RegistrySupplier<Block> SUSPICIOUS_CLAY = registerWithItem(SuspiciousClayBlock.BLOCK_ID, SuspiciousClayBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_DIRT = registerWithItem(SuspiciousDirtBlock.BLOCK_ID, SuspiciousDirtBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_MUD = registerWithItem(SuspiciousMudBlock.BLOCK_ID, SuspiciousMudBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_PACKED_MUD = registerWithItem(SuspiciousPackedMudBlock.BLOCK_ID, SuspiciousPackedMudBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_RED_SAND = registerWithItem(SuspiciousRedSandBlock.BLOCK_ID, SuspiciousRedSandBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_ROOTED_DIRT = registerWithItem(SuspiciousRootedDirtBlock.BLOCK_ID, SuspiciousRootedDirtBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_SOUL_SAND = registerWithItem(SuspiciousSoulSandBlock.BLOCK_ID, SuspiciousSoulSandBlock::new, DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> SUSPICIOUS_SOUL_SOIL = registerWithItem(SuspiciousSoulSoilBlock.BLOCK_ID, SuspiciousSoulSoilBlock::new, DEFAULT_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<BrushableBlockEntity>> BRUSHABLE_MOD_BLOCK_ENTITY = registerBlockEntity(
        Identifier.of(ModInfo.MOD_ID, "brushable_block"),
        () -> BlockEntityType.Builder.create(
            BrushableBlockEntity::new,
            ModBlocks.SUSPICIOUS_CLAY.get(),
            ModBlocks.SUSPICIOUS_DIRT.get(),
            ModBlocks.SUSPICIOUS_MUD.get(),
            ModBlocks.SUSPICIOUS_PACKED_MUD.get(),
            ModBlocks.SUSPICIOUS_RED_SAND.get(),
            ModBlocks.SUSPICIOUS_ROOTED_DIRT.get(),
            ModBlocks.SUSPICIOUS_SOUL_SAND.get(),
            ModBlocks.SUSPICIOUS_SOUL_SOIL.get()
        ).build(null)
    );
}
