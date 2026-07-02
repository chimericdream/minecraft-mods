package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class SuspiciousRootedDirtBlock extends BrushableFloatingBlock implements BonemealableBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "suspicious_rooted_dirt");

    public SuspiciousRootedDirtBlock() {
        super(
            Blocks.ROOTED_DIRT,
            SoundEvents.BRUSH_GRAVEL,
            SoundEvents.BRUSH_GRAVEL_COMPLETED,
            BlockBehaviour.Properties
                .of()
                .mapColor(MapColor.DIRT)
                .strength(0.5F)
                .sound(SoundType.ROOTED_DIRT)
                .pushReaction(PushReaction.DESTROY)
                .setId(ResourceKey.create(Registries.BLOCK, BLOCK_ID))
        );
    }

    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.below()).isAir();
    }

    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        world.setBlockAndUpdate(pos.below(), Blocks.HANGING_ROOTS.defaultBlockState());
    }

    public BlockPos getParticlePos(BlockPos pos) {
        return pos.below();
    }
}
