package com.chimericdream.sponj.blocks;

import com.chimericdream.sponj.BlockUtils;
import com.chimericdream.sponj.ModInfo;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class SponjBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "sponj");
    public static final RegistryKey<Block> BLOCK_REGISTRY_KEY = RegistryKey.of(RegistryKeys.BLOCK, BLOCK_ID);
    public static final RegistryKey<Item> ITEM_REGISTRY_KEY = RegistryKey.of(RegistryKeys.ITEM, BLOCK_ID);

    public SponjBlock() {
        super(Settings.copy(Blocks.SPONGE).registryKey(BLOCK_REGISTRY_KEY));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.update(world, pos);
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, @Nullable WireOrientation wireOrientation, boolean notify) {
        this.update(world, pos);
        super.neighborUpdate(state, world, pos, block, wireOrientation, notify);
    }

    protected void update(World world, BlockPos pos) {
        if (this.absorbWater(world, pos)) {
            world.setBlockState(pos, ModBlocks.WET_SPONJ_BLOCK.get().getDefaultState(), 2);
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
        }

    }

    private boolean absorbWater(World world, BlockPos pos) {
        List<BlockPos> sponjes = BlockUtils.getConnectedBlocksByType(world, pos, ModBlocks.getSponjBlocks(), 32);
        int sponjCount = sponjes.size();

        int absorptionRadius = 6 + (3 * (sponjCount - 1));
        int maxAbsorption = 64 * sponjCount;

        Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Pair<>(pos, 0));

        int i = 0;

        while (!queue.isEmpty()) {
            Pair<BlockPos, Integer> pair = queue.poll();
            BlockPos blockPos = pair.getLeft();

            int j = pair.getRight();

            Direction[] directions = Direction.values();

            for (Direction direction : directions) {
                BlockPos blockPos2 = blockPos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos2);
                FluidState fluidState = world.getFluidState(blockPos2);

                if (fluidState.isIn(FluidTags.WATER)) {
                    if (blockState.getBlock() instanceof FluidDrainable && !((FluidDrainable) blockState.getBlock()).tryDrainFluid(null, world, blockPos2, blockState).isEmpty()) {
                        ++i;

                        if (j < absorptionRadius) {
                            queue.add(new Pair<>(blockPos2, j + 1));
                        }
                    } else if (blockState.getBlock() instanceof FluidBlock) {
                        world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
                        ++i;
                        if (j < absorptionRadius) {
                            queue.add(new Pair<>(blockPos2, j + 1));
                        }
                    } else if (blockState.isReplaceable()) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
                        dropStacks(blockState, world, blockPos2, blockEntity);
                        world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Pair<>(blockPos2, j + 1));
                        }
                    }
                }
            }

            if (i > maxAbsorption) {
                break;
            }
        }

        if (i > 0) {
            for (BlockPos sponjPos : sponjes) {
                if (world.getBlockState(sponjPos).getBlock().equals(ModBlocks.SPONJ_BLOCK.get())) {
                    world.setBlockState(sponjPos, ModBlocks.WET_SPONJ_BLOCK.get().getDefaultState(), 2);
                    world.syncWorldEvent(2001, sponjPos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
                }
            }
        }

        return i > 0;
    }
}
