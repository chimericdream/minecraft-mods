package com.chimericdream.sponj.blocks;

import com.chimericdream.sponj.BlockUtils;
import com.chimericdream.sponj.ModInfo;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;

public class LavaSponjBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "lava_sponj");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public LavaSponjBlock() {
        super(Properties.ofFullCopy(Blocks.SPONGE).setId(BLOCK_REGISTRY_KEY));
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.is(state.getBlock())) {
            this.update(world, pos);
        }
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, @Nullable Orientation wireOrientation, boolean notify) {
        this.update(world, pos);
        super.neighborChanged(state, world, pos, block, wireOrientation, notify);
    }

    protected void update(Level world, BlockPos pos) {
        if (this.absorbLava(world, pos)) {
            world.setBlock(pos, ModBlocks.WET_LAVA_SPONJ_BLOCK.get().defaultBlockState(), 2);
            world.levelEvent(2001, pos, Block.getId(Blocks.LAVA.defaultBlockState()));
        }
    }

    private boolean absorbLava(Level world, BlockPos pos) {
        List<BlockPos> sponjes = BlockUtils.getConnectedBlocksByType(world, pos, ModBlocks.getLavaSponjBlocks(), 32);
        int sponjCount = sponjes.size();

        int absorptionRadius = 6 + (3 * (sponjCount - 1));
        int maxAbsorption = 64 * sponjCount;

        Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(Pair.of(pos, 0));
        int i = 0;

        while (!queue.isEmpty()) {
            Pair<BlockPos, Integer> pair = queue.poll();
            BlockPos blockPos = pair.getFirst();
            int j = pair.getSecond();

            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                BlockPos blockPos2 = blockPos.relative(direction);
                BlockState blockState = world.getBlockState(blockPos2);
                FluidState fluidState = world.getFluidState(blockPos2);

                if (fluidState.is(FluidTags.LAVA)) {
                    if (blockState.getBlock() instanceof BucketPickup && !((BucketPickup) blockState.getBlock()).pickupBlock(null, world, blockPos2, blockState).isEmpty()) {
                        ++i;
                        if (j < absorptionRadius) {
                            queue.add(Pair.of(blockPos2, j + 1));
                        }
                    } else if (blockState.getBlock() instanceof LiquidBlock) {
                        world.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < absorptionRadius) {
                            queue.add(Pair.of(blockPos2, j + 1));
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
                if (world.getBlockState(sponjPos).getBlock().equals(ModBlocks.LAVA_SPONJ_BLOCK.get())) {
                    world.setBlock(sponjPos, ModBlocks.WET_LAVA_SPONJ_BLOCK.get().defaultBlockState(), 2);
                    world.levelEvent(2001, sponjPos, Block.getId(Blocks.LAVA.defaultBlockState()));
                }
            }
        }

        return i > 0;
    }
}
