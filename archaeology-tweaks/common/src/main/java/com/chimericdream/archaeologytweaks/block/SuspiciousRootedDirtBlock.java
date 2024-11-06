package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SuspiciousRootedDirtBlock extends BrushableFloatingBlock implements Fertilizable {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "suspicious_rooted_dirt");

    public SuspiciousRootedDirtBlock() {
        super(
            Blocks.ROOTED_DIRT,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
            SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
            AbstractBlock.Settings
                .create()
                .mapColor(MapColor.DIRT_BROWN)
                .strength(0.5F)
                .sounds(BlockSoundGroup.ROOTED_DIRT)
                .pistonBehavior(PistonBehavior.DESTROY)
        );
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.down()).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos.down(), Blocks.HANGING_ROOTS.getDefaultState());
    }

    public BlockPos getFertilizeParticlePos(BlockPos pos) {
        return pos.down();
    }
}
