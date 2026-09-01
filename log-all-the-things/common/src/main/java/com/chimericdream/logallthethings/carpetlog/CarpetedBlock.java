package com.chimericdream.logallthethings.carpetlog;

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
import com.chimericdream.logallthethings.ModInfo;

/**
 * The block a carpet-logged host (slab, stairs, wall, fence, chain, bars, or glass pane) actually
 * becomes in the world. Carries no blockstate properties of its own (see {@code carpeted_block.json},
 * which points every state at the empty {@code minecraft:block/air} model) — all the real state lives
 * in its {@link CarpetedBlockEntity}, and {@code RenderShape.INVISIBLE} hands rendering entirely to
 * {@code carpetlog.client.CarpetedBlockEntityRenderer}. Shape/collision delegate to the host plus a
 * thin footprint fitted to whichever part of the host is actually flat enough for a carpet to lie on
 * (see {@link #carpetShape}); drops are hardcoded to one of each sub-block's item, which is
 * safe because every vanilla slab/stair/wall/fence/chain/bars/pane/carpet always drops exactly itself
 * regardless of loot conditions.
 */
public class CarpetedBlock extends Block implements EntityBlock {
    public CarpetedBlock() {
        super(
            BlockBehaviour.Properties.of()
                .noOcclusion()
                .dynamicShape()
                .forceSolidOn()
                .strength(2.0F)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "carpeted_block")))
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(properties -> new CarpetedBlock());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CarpetedBlockEntity(pos, state);
    }

    /**
     * See {@code WindowedBlock#getRenderShape} for why {@code INVISIBLE} is correct here under MC
     * 26.2's render-feature overhaul.
     */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public BlockState getHostState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CarpetedBlockEntity be ? be.getHostState() : Blocks.AIR.defaultBlockState();
    }

    public BlockState getCarpetState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CarpetedBlockEntity be ? be.getCarpetState() : Blocks.AIR.defaultBlockState();
    }

    /**
     * Keeps a connectable host (wall/fence/bars/pane) looking connected to its real neighbours even
     * after carpet-logging - without this, the stored {@code hostState} is frozen exactly as it was
     * the moment it got carpet-logged, so a fence placed next to this block afterward would visibly
     * connect toward it (per {@code LATT$FenceBlockMixin} et al., which substitute this block's stored
     * host in place of its own connection-property-less carrier state) while this block's own rendered
     * stub of the connection facing that fence stayed stuck open. Delegating to the host's own
     * {@code updateShape} - the same call a live fence/wall/bars/pane would receive for this exact
     * neighbour change - keeps this in lockstep with vanilla's own connection rules, and substituting
     * the neighbour through {@link CarpetLogHelper#effectiveNeighborState} first means a neighbour that
     * is itself carpet-logged is seen as its real host too, not as an unrecognized block.
     */
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
        if (level instanceof Level realLevel && !realLevel.isClientSide() && level.getBlockEntity(pos) instanceof CarpetedBlockEntity be) {
            BlockState hostState = be.getHostState();
            if (!hostState.isAir()) {
                BlockState effectiveNeighbor = CarpetLogHelper.effectiveNeighborState(level, neighbourPos, neighbourState);
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
        return Shapes.or(hostState.getShape(level, pos, context), carpetShape(hostState));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    /**
     * Without this, mining speed comes from this block's own fixed {@code strength(2.0F)} regardless
     * of which part is targeted — mirrors {@code WindowedBlock#getDestroyProgress}.
     */
    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CarpetedBlockEntity be && level instanceof Level realLevel) {
            BlockState targeted = CarpetLogHelper.pickTargetedState(realLevel, pos, be.getCarpetState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                return targeted.getDestroyProgress(player, level, pos);
            }
        }

        return super.getDestroyProgress(state, player, level, pos);
    }

    /**
     * Mirrors {@code WindowedBlock#spawnDestroyParticles} — this block's own single, blockstate-less
     * state has no real texture to draw break particles from, so this delegates to whichever sub-state
     * was actually aimed at.
     */
    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof CarpetedBlockEntity be) {
            BlockState targeted = CarpetLogHelper.pickTargetedState(level, pos, be.getCarpetState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                level.levelEvent(player, 2001, pos, Block.getId(targeted));
                return;
            }
        }

        super.spawnDestroyParticles(level, player, pos, state);
    }

    /**
     * This block has no registered {@code BlockItem} of its own, so vanilla's default here would
     * return an empty pick-block result — mirrors {@code WindowedBlock#getCloneItemStack}.
     */
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        if (level.getBlockEntity(pos) instanceof CarpetedBlockEntity be) {
            BlockState targeted = CarpetLogHelper.pickTargetedStateForPickBlock(level, pos, be.getCarpetState(), be.getHostState());

            if (!targeted.isAir()) {
                return new ItemStack(targeted.getBlock());
            }
        }

        return super.getCloneItemStack(level, pos, state, includeData);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CarpetedBlockEntity be)) {
            return List.of();
        }

        List<ItemStack> drops = new ArrayList<>(2);
        if (!be.getHostState().isAir()) {
            drops.add(new ItemStack(be.getHostState().getBlock()));
        }
        if (!be.getCarpetState().isAir()) {
            drops.add(new ItemStack(be.getCarpetState().getBlock()));
        }

        return drops;
    }

    /** Matches the 1px-thick layer {@code assets/logallthethings/models/block/*_carpet.json} draws. */
    private static final double CARPET_THICKNESS = 1.0 / 16.0;

    /**
     * The carpet-only portion of the composite's shape — package-visible (not just used for
     * collision here, but also by {@link CarpetLogHelper#isAimingAtCarpet}, which needs this exact
     * fitted shape rather than a real {@code CarpetBlock}'s own natural shape: a real carpet's shape
     * is always a full 16x16 square fixed at the bottom of its own block (y0-1), which doesn't
     * correspond to where this mod actually draws it for most host configurations (e.g. on top of a
     * bottom slab), so using it for aim-detection would make "pop just the carpet" only work by
     * coincidence for the top-slab/top-stairs cases where the real shape happens to land in the right
     * place. Unioning in a real carpet's full shape (or the real stair's own full shape) here would
     * also make the wrong region readable as "carpet" for pick-block/mining-speed purposes even
     * though nothing is drawn there.
     *
     * <p>Every other carpetable host (walls, fences, chains, bars, glass panes) has no flat surface of
     * its own for a carpet to sit on top of - it's the same as a real carpet placed in the same block
     * as one of those, lying flush across the whole footprint at floor level (y0-1). That's exactly a
     * real {@code CarpetBlock}'s own natural shape, which is why {@code CarpetedBlockEntityRenderer}'s
     * plain {@code submitMovingBlock} fallback (no {@code CarpetFrameRenderer} overlay needed) already
     * renders it correctly with no per-host model authoring.
     *
     * <p>The stair case is computed assuming {@code FACING == EAST} and rotated to the real facing
     * via {@link HostFootprint#stairsYRotation}. {@code CarpetFrameRenderer} instead ships one pre-rotated model
     * file per facing (see its class doc for why a single shared-and-rotated mesh doesn't work for
     * these non-square elements) rather than rotating a shared shape at render time like this method
     * does — but each of those files was generated by rotating the {@code _east} file by this exact
     * same degree table, so the two stay in agreement. A bottom stair's carpet covers the whole
     * walkable surface: the low run (west, sitting on the host's 7px-tall lower body) and the raised
     * corner's own top (east, sitting on the corner's own 7px-shorter body) — two separate boxes,
     * unioned. A top stair only carpets the flat side's hanging strip; there's no analogous
     * "corner top" for an upside-down stair.
     */
    static VoxelShape carpetShape(BlockState hostState) {
        if (hostState.isAir()) {
            return Shapes.empty();
        }

        if (hostState.getBlock() instanceof StairBlock) {
            if (hostState.getValue(StairBlock.SHAPE) != StairsShape.STRAIGHT) {
                return Shapes.empty();
            }

            int rotation = HostFootprint.stairsYRotation(hostState.getValue(StairBlock.FACING));

            if (hostState.getValue(StairBlock.HALF) == Half.TOP) {
                return HostFootprint.rotateFootprintY(0.0, 0.0, 0.0, 0.5, CARPET_THICKNESS, 1.0, rotation);
            }

            VoxelShape lowRun = HostFootprint.rotateFootprintY(0.0, 0.5 - CARPET_THICKNESS, 0.0, 0.5, 0.5, 1.0, rotation);
            VoxelShape cornerTop = HostFootprint.rotateFootprintY(0.5, 1.0 - CARPET_THICKNESS, 0.0, 1.0, 1.0, 1.0, rotation);
            return Shapes.or(lowRun, cornerTop);
        }

        if (hostState.getBlock() instanceof SlabBlock) {
            boolean top = hostState.getValue(SlabBlock.TYPE) == SlabType.TOP;
            double y1 = top ? CARPET_THICKNESS : 0.5;
            double y0 = y1 - CARPET_THICKNESS;

            return Shapes.box(0.0, y0, 0.0, 1.0, y1, 1.0);
        }

        return Shapes.box(0.0, 0.0, 0.0, 1.0, CARPET_THICKNESS, 1.0);
    }
}
