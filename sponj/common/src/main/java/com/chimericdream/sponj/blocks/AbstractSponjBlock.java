package com.chimericdream.sponj.blocks;

import com.chimericdream.lib.blocks.BlockUtils;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;

/**
 * Shared dry-sponge behavior. A dry sponj absorbs a fluid outward from itself (bounded by a radius
 * and a block budget that both scale with the number of connected sponjes) and turns itself and the
 * connected sponjes wet. The only per-fluid variation is the fluid tag, the dry/wet block pair, the
 * fluid used for the break particle event, and whether "washable" replaceable blocks (kelp,
 * seagrass) are cleared too — that branch is water-only.
 */
public abstract class AbstractSponjBlock extends Block {
    protected AbstractSponjBlock(ResourceKey<Block> blockKey) {
        super(Properties.ofFullCopy(Blocks.SPONGE).setId(blockKey));
    }

    /** The fluid this sponj absorbs. */
    protected abstract TagKey<Fluid> getFluidTag();

    /** The wet block this sponj becomes once it has absorbed something. */
    protected abstract Block getWetBlock();

    /** This (dry) block, used to identify connected sponjes still eligible to turn wet. */
    protected abstract Block getDryBlock();

    /** The dry + wet blocks of this fluid family, for the connected-sponj flood fill. */
    protected abstract List<Block> getConnectedBlockTypes();

    /** The fluid block state whose id drives the {@code 2001} break particle level event. */
    protected abstract BlockState getAbsorbedFluidState();

    /** Whether replaceable "washable" blocks (kelp, seagrass, …) are cleared too. Water only. */
    protected abstract boolean absorbsWashableBlocks();

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.is(state.getBlock())) {
            this.update(world, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, @Nullable Orientation wireOrientation, boolean notify) {
        this.update(world, pos);
        super.neighborChanged(state, world, pos, block, wireOrientation, notify);
    }

    protected void update(Level world, BlockPos pos) {
        if (this.absorb(world, pos)) {
            world.setBlock(pos, getWetBlock().defaultBlockState(), 2);
            world.levelEvent(2001, pos, Block.getId(getAbsorbedFluidState()));
        }
    }

    private boolean absorb(Level world, BlockPos pos) {
        List<BlockPos> sponjes = BlockUtils.getConnectedBlocksByType(world, pos, getConnectedBlockTypes(), ModBlocks.MAX_CONNECTED_SPONJES);
        int sponjCount = sponjes.size();

        int absorptionRadius = 6 + (3 * (sponjCount - 1));
        int maxAbsorption = 64 * sponjCount;

        TagKey<Fluid> fluidTag = getFluidTag();
        boolean washable = absorbsWashableBlocks();

        Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(Pair.of(pos, 0));

        int i = 0;

        while (!queue.isEmpty()) {
            Pair<BlockPos, Integer> pair = queue.poll();
            BlockPos blockPos = pair.getFirst();

            int j = pair.getSecond();

            for (Direction direction : Direction.values()) {
                BlockPos blockPos2 = blockPos.relative(direction);
                BlockState blockState = world.getBlockState(blockPos2);
                FluidState fluidState = world.getFluidState(blockPos2);

                if (fluidState.is(fluidTag)) {
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
                    } else if (washable && blockState.canBeReplaced()) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
                        dropResources(blockState, world, blockPos2, blockEntity);
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
            Block dryBlock = getDryBlock();
            Block wetBlock = getWetBlock();
            int fluidId = Block.getId(getAbsorbedFluidState());

            for (BlockPos sponjPos : sponjes) {
                if (world.getBlockState(sponjPos).getBlock().equals(dryBlock)) {
                    world.setBlock(sponjPos, wetBlock.defaultBlockState(), 2);
                    world.levelEvent(2001, sponjPos, fluidId);
                }
            }
        }

        return i > 0;
    }
}
