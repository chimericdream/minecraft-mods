package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SuspiciousMudBlock extends BrushableFloatingNonFullBlock {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "suspicious_mud");

    public SuspiciousMudBlock() {
        super(
            Blocks.MUD,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
            AbstractBlock.Settings
                .copyShallow(Blocks.DIRT)
                .mapColor(MapColor.TERRACOTTA_CYAN)
                .solidBlock(Blocks::always)
                .blockVision(Blocks::always)
                .suffocates(Blocks::always)
                .sounds(BlockSoundGroup.MUD)
                .pistonBehavior(PistonBehavior.DESTROY)
        );
    }
}
