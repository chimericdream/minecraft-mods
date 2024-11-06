package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SuspiciousSoulSandBlock extends BrushableFloatingNonFullBlock {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "suspicious_soul_sand");

    public SuspiciousSoulSandBlock() {
        super(
            Blocks.SOUL_SAND,
            SoundEvents.ITEM_BRUSH_BRUSHING_SAND,
            SoundEvents.ITEM_BRUSH_BRUSHING_SAND_COMPLETE,
            AbstractBlock.Settings
                .create()
                .mapColor(MapColor.BROWN)
                .strength(0.5F)
                .velocityMultiplier(0.4F)
                .sounds(BlockSoundGroup.SOUL_SAND)
                .solidBlock(Blocks::always)
                .blockVision(Blocks::always)
                .suffocates(Blocks::always)
                .pistonBehavior(PistonBehavior.DESTROY)
        );
    }
}
