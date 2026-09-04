package com.chimericdream.logallthethings.snowlog;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.chimericdream.logallthethings.HostFootprint;
import com.chimericdream.logallthethings.LoggedNeighborHelper;
import com.chimericdream.logallthethings.ModInfo;

/**
 * The block a snow-logged host (slab, straight stairs, wall, fence, chain, bars, or glass pane)
 * actually becomes in the world. Mirrors {@code carpetlog.CarpetedBlock} exactly - see that class's
 * doc for the invisible-carrier/{@code RenderShape.INVISIBLE}/{@code EntityBlock} rationale, which
 * applies unchanged here. The one real difference from carpet is that a snow layer has genuine
 * height (up to {@link SnowLogHelper#MAX_LAYERS} layers, one shy of a full snow block) instead of a
 * fixed 1px decal, so {@link #snowShape} computes real volume rather than a flush skin - see its own
 * doc for how much space each host actually offers.
 */
public class SnowedBlock extends Block implements EntityBlock {
    /** Matches {@code SnowLayerBlock}'s own per-layer height (each layer is 2px, 8 layers = 1 block). */
    static final double LAYER_HEIGHT = 1.0 / 8.0;

    public SnowedBlock() {
        super(
            BlockBehaviour.Properties.of()
                .noOcclusion()
                .dynamicShape()
                .forceSolidOn()
                .strength(2.0F)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "snowed_block")))
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(properties -> new SnowedBlock());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SnowedBlockEntity(pos, state);
    }

    /** See {@code CarpetedBlock#getRenderShape} - identical reasoning, this block is BE-rendered only. */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public BlockState getHostState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SnowedBlockEntity be ? be.getHostState() : Blocks.AIR.defaultBlockState();
    }

    public BlockState getSnowState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SnowedBlockEntity be ? be.getSnowState() : Blocks.AIR.defaultBlockState();
    }

    /** See {@code CarpetedBlock#updateShape} - identical reasoning, delegated via {@link LoggedNeighborHelper#effectiveNeighborState}. */
    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos,
        BlockState neighbourState,
        RandomSource random
    ) {
        if (level instanceof Level realLevel && !realLevel.isClientSide() && level.getBlockEntity(pos) instanceof SnowedBlockEntity be) {
            BlockState hostState = be.getHostState();
            if (!hostState.isAir()) {
                BlockState effectiveNeighbor = LoggedNeighborHelper.effectiveNeighborState(level, neighbourPos, neighbourState);
                BlockState updatedHost = hostState.updateShape(realLevel, ticks, pos, directionToNeighbour, neighbourPos, effectiveNeighbor, random);

                if (!updatedHost.equals(hostState)) {
                    be.setHostState(updatedHost);
                    be.setChanged();
                    realLevel.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                }
            }
        }

        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState hostState = getHostState(level, pos);
        BlockState snowState = getSnowState(level, pos);
        return Shapes.or(hostState.getShape(level, pos, context), snowShape(hostState, layersOf(snowState)));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    /** See {@code CarpetedBlock#getDestroyProgress} - identical reasoning. */
    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SnowedBlockEntity be && level instanceof Level realLevel) {
            BlockState targeted = SnowLogHelper.pickTargetedState(realLevel, pos, be.getSnowState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                return targeted.getDestroyProgress(player, level, pos);
            }
        }

        return super.getDestroyProgress(state, player, level, pos);
    }

    /** See {@code CarpetedBlock#spawnDestroyParticles} - identical reasoning. */
    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof SnowedBlockEntity be) {
            BlockState targeted = SnowLogHelper.pickTargetedState(level, pos, be.getSnowState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                level.levelEvent(player, 2001, pos, Block.getId(targeted));
                return;
            }
        }

        super.spawnDestroyParticles(level, player, pos, state);
    }

    /** See {@code CarpetedBlock#getCloneItemStack} - identical reasoning. */
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        if (level.getBlockEntity(pos) instanceof SnowedBlockEntity be) {
            BlockState targeted = SnowLogHelper.pickTargetedStateForPickBlock(level, pos, be.getSnowState(), be.getHostState());

            if (!targeted.isAir()) {
                return new ItemStack(targeted.getBlock());
            }
        }

        return super.getCloneItemStack(level, pos, state, includeData);
    }

    /**
     * Unlike {@code CarpetedBlock#getDrops} - which hardcodes both drops because every carpetable
     * host and every carpet always drops itself unconditionally - the snow portion here must go
     * through its real loot table: vanilla's snow layer requires a shovel to drop anything at all.
     * {@code params} already carries whichever tool broke this block (the same {@code LootParams}
     * vanilla's own block-breaking flow builds), so delegating to {@code BlockState#getDrops}
     * evaluates that condition correctly instead of assuming a drop always happens.
     */
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof SnowedBlockEntity be)) {
            return List.of();
        }

        List<ItemStack> drops = new ArrayList<>(2);
        if (!be.getHostState().isAir()) {
            drops.add(new ItemStack(be.getHostState().getBlock()));
        }
        if (!be.getSnowState().isAir()) {
            drops.addAll(be.getSnowState().getDrops(params));
        }

        return drops;
    }

    public static int layersOf(BlockState snowState) {
        return snowState.isAir() ? 0 : snowState.getValue(SnowLayerBlock.LAYERS);
    }

    /**
     * The snow-only portion of the composite's shape - real volume (not a flush decal like
     * {@code CarpetedBlock#carpetShape}), fitted to whichever part of {@code hostState} actually has
     * open headroom for it:
     * <ul>
     *   <li>Bottom slab / non-raised straight-stairs low-run half: open space starts at the host's own
     *   top surface (y=0.5) and grows upward, capped at y=1.0 (4 layers' worth of headroom).</li>
     *   <li>Top slab / top-stairs flat half: open space starts at the floor (y=0) and grows upward,
     *   capped at y=0.5 (also 4 layers).</li>
     *   <li>Every other host (walls, fences, chains, bars, panes): a full open column from y=0, same
     *   as snow placed in an empty block - this is exactly {@code SnowLayerBlock}'s own natural shape,
     *   which is why the non-stair/slab render path in {@code SnowedBlockEntityRenderer} can submit
     *   the real snow blockstate unmodified.</li>
     * </ul>
     * The straight-stairs cases are restricted to the low-run/flat half footprint only - the raised
     * corner is already solid up to the block's own ceiling on that side, with zero headroom for even
     * one layer - using the same {@link HostFootprint#rotateFootprintY} rotation
     * {@code CarpetedBlock#carpetShape} uses for its own {@code lowRun}/flat-side regions, so collision
     * here always agrees with whatever facing-specific box
     * {@code SnowedBlockEntityRenderer}'s stairs path actually draws.
     */
    public static VoxelShape snowShape(BlockState hostState, int layers) {
        if (hostState.isAir() || layers <= 0) {
            return Shapes.empty();
        }

        double height = layers * LAYER_HEIGHT;

        if (hostState.getBlock() instanceof StairBlock) {
            if (hostState.getValue(StairBlock.SHAPE) != StairsShape.STRAIGHT) {
                return Shapes.empty();
            }

            int rotation = HostFootprint.stairsYRotation(hostState.getValue(StairBlock.FACING));

            if (hostState.getValue(StairBlock.HALF) == Half.TOP) {
                return HostFootprint.rotateFootprintY(0.0, 0.0, 0.0, 0.5, height, 1.0, rotation);
            }

            return HostFootprint.rotateFootprintY(0.0, 0.5, 0.0, 0.5, 0.5 + height, 1.0, rotation);
        }

        if (hostState.getBlock() instanceof SlabBlock) {
            boolean top = hostState.getValue(SlabBlock.TYPE) == SlabType.TOP;
            double y0 = top ? 0.0 : 0.5;
            return Shapes.box(0.0, y0, 0.0, 1.0, y0 + height, 1.0);
        }

        return Shapes.box(0.0, 0.0, 0.0, 1.0, height, 1.0);
    }
}
