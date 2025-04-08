package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SuspiciousSoulSoilBlock extends BrushableFloatingBlock {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "suspicious_soul_soil");

    public SuspiciousSoulSoilBlock() {
        super(
            Blocks.SOUL_SOIL,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
            AbstractBlock.Settings
                .create()
                .mapColor(MapColor.BROWN)
                .strength(0.5F)
                .sounds(BlockSoundGroup.SOUL_SOIL)
                .pistonBehavior(PistonBehavior.DESTROY)
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, BLOCK_ID))
        );
    }
}
