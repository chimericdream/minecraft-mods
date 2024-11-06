package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SuspiciousPackedMudBlock extends BrushableFloatingBlock {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "suspicious_packed_mud");

    public SuspiciousPackedMudBlock() {
        super(
            Blocks.PACKED_MUD,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
            AbstractBlock.Settings
                .copyShallow(Blocks.DIRT)
                .strength(1.0F, 3.0F)
                .sounds(BlockSoundGroup.PACKED_MUD)
                .pistonBehavior(PistonBehavior.DESTROY)
        );
    }
}
