package com.chimericdream.logallthethings.windowlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.chimericdream.logallthethings.ModInfo;
import com.chimericdream.logallthethings.lavalog.LavaLogHelper;
import com.chimericdream.logallthethings.lavalog.LavaLogProperties;

/**
 * The block a window-logged slab/stair actually becomes in the world. Carries no blockstate
 * properties of its own (see {@code windowed_block.json}, which points every state at the empty
 * {@code minecraft:block/air} model) — all the real state lives in its {@link WindowedBlockEntity},
 * and {@code RenderShape.INVISIBLE} hands rendering entirely to
 * {@code windowlog.client.WindowedBlockEntityRenderer}. Shape/collision delegate to the union of the
 * host and window sub-states; drops are hardcoded to one of each sub-block's item, which is safe
 * because every vanilla slab/stair/pane always drops exactly itself regardless of loot conditions.
 */
public class WindowedBlock extends Block implements EntityBlock, LiquidBlockContainer, BucketPickup {
    public WindowedBlock() {
        super(
            BlockBehaviour.Properties.of()
                .noOcclusion()
                .dynamicShape()
                // dynamicShape() disables BlockBehaviour's per-state shape cache, and that cache is
                // what its "legacySolid"/blocksMotion() flag is derived from - without forceSolidOn(),
                // this block reads as non-solid the same way {@code minecraft:air} or a flower does,
                // which is what let flowing lava treat it as fair game to destroy instead of leaving it
                // alone the way a real (non-dynamic-shape) stair/slab/pane does.
                .forceSolidOn()
                .strength(2.0F)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "windowed_block")))
        );
        registerDefaultState(defaultBlockState().setValue(LavaLogProperties.LAVALOGGED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(properties -> new WindowedBlock());
    }

    /**
     * Lets a lava-logged stair/slab stay lava-logged once window-logged (and vice versa - see
     * {@link WindowLogHelper#tryWindowLog}/{@code tryPartialBreak}, which carry
     * {@link LavaLogProperties#LAVALOGGED} across that transition on the host state). Unlike a plain
     * stair/slab, {@code WindowedBlock} has no properties of its own to derive {@code getFluidState()}
     * from - it needs its own copy of the flag, kept in sync with the block entity's {@code hostState}
     * by {@link #latt$syncHostLavaLogged}, since {@code getFluidState(BlockState)} is state-only (no
     * level/pos to consult the block entity with).
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LavaLogProperties.LAVALOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WindowedBlockEntity(pos, state);
    }

    /**
     * MC 26.2's {@link RenderShape} only distinguishes {@code MODEL} from {@code INVISIBLE} — the old
     * {@code ENTITYBLOCK_ANIMATED} value is gone, and {@code BlockEntityRenderDispatcher} now dispatches
     * to a registered {@code BlockEntityRenderer} purely by block entity type, independent of this
     * value. {@code INVISIBLE} matches {@code windowed_block.json}'s empty {@code minecraft:block/air}
     * model and skips binding a (pointless) model for it.
     */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    /**
     * Flammability's own {@code LATT$FireBlockMixin} substitutes the host state when consulting fire
     * odds, so {@link com.chimericdream.logallthethings.lavalog.LavaLogFlammability#isFlammable} is
     * checked against the real host below rather than this block's own (never-flammable) state - a
     * window-logged oak-stairs host should still refuse a lava bucket, exactly like a plain oak stairs
     * would.
     */
    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity user, BlockGetter level, BlockPos pos, BlockState state, Fluid type) {
        if (type != Fluids.LAVA) {
            return false;
        }

        BlockState hostState = getHostState(level, pos);
        return !hostState.isAir() && LavaLogHelper.canLavaLog(level, pos, state, hostState);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!fluidState.is(Fluids.LAVA) || !LavaLogHelper.placeLava(level, pos, state, fluidState)) {
            return false;
        }

        latt$syncHostLavaLogged(level, pos, true);
        return true;
    }

    @Override
    public ItemStack pickupBlock(@Nullable LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state) {
        ItemStack result = LavaLogHelper.pickupLava(level, pos, state);
        if (!result.isEmpty()) {
            latt$syncHostLavaLogged(level, pos, false);
        }

        return result;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return LavaLogHelper.getPickupSound();
    }

    /**
     * Mirrors {@link LavaLogProperties#LAVALOGGED} from this block's own carrier state onto the block
     * entity's {@code hostState}, so the plain stair/slab that comes back out of
     * {@code WindowLogHelper#tryPartialBreak} (or gets read by {@code WindowedBlock#getDrops}) still
     * remembers whether it was lava-logged while windowed.
     */
    private static void latt$syncHostLavaLogged(LevelAccessor level, BlockPos pos, boolean lavalogged) {
        if (level.getBlockEntity(pos) instanceof WindowedBlockEntity be && be.getHostState().hasProperty(LavaLogProperties.LAVALOGGED)) {
            be.setHostState(be.getHostState().setValue(LavaLogProperties.LAVALOGGED, lavalogged));
            be.setChanged();
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(LavaLogProperties.LAVALOGGED) ? Fluids.LAVA.getSource(false) : super.getFluidState(state);
    }

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
        if (state.getValue(LavaLogProperties.LAVALOGGED)) {
            ticks.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(level));
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    public BlockState getHostState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof WindowedBlockEntity be ? be.getHostState() : Blocks.AIR.defaultBlockState();
    }

    public BlockState getWindowState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof WindowedBlockEntity be ? be.getWindowState() : Blocks.AIR.defaultBlockState();
    }

    /**
     * A stair's open notch is only half the block's width (the raised step occupies the other half),
     * but a real {@code minecraft:glass_pane} blockstate's connected shape always spans the block's
     * <em>full</em> 16px width/depth regardless — unioning it in directly (as this used to) covers the
     * already-solid host half too, so the visible collision box comes out full-width instead of fitted
     * to the notch (a "1x8x16" slab flush against the upper step, not the "8x8x1" the hand-authored
     * {@code WindowFrameRenderer} glass mesh actually occupies). For stairs, a shape sized to that same
     * mesh is used instead of the pane's own shape; slabs keep the pane's real shape since a slab's
     * missing half genuinely is the pane's full 16x16 footprint.
     */
    private static final double STAIR_WINDOW_HALF_THICKNESS = 0.0625; // half of 2px, matching the glass mesh's 7-9 Z range

    private static VoxelShape stairWindowShape(Direction facing, Half half) {
        double lowY = half == Half.BOTTOM ? 0.5 : 0.0;
        double highY = half == Half.BOTTOM ? 1.0 : 0.5;
        double centerLow = 0.5 - STAIR_WINDOW_HALF_THICKNESS;
        double centerHigh = 0.5 + STAIR_WINDOW_HALF_THICKNESS;

        return switch (facing) {
            case EAST -> Shapes.box(0.0, lowY, centerLow, 0.5, highY, centerHigh);
            case WEST -> Shapes.box(0.5, lowY, centerLow, 1.0, highY, centerHigh);
            case SOUTH -> Shapes.box(centerLow, lowY, 0.0, centerHigh, highY, 0.5);
            case NORTH -> Shapes.box(centerLow, lowY, 0.5, centerHigh, highY, 1.0);
            default -> Shapes.empty();
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState hostState = getHostState(level, pos);

        VoxelShape windowShape = hostState.getBlock() instanceof StairBlock
            ? stairWindowShape(hostState.getValue(StairBlock.FACING), hostState.getValue(StairBlock.HALF))
            : getWindowState(level, pos).getShape(level, pos, context);

        return Shapes.or(hostState.getShape(level, pos, context), windowShape);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    /**
     * Without this, mining speed comes from this block's own fixed {@code strength(2.0F)} regardless
     * of which part is targeted. Delegating to whichever sub-state {@link WindowLogHelper#isAimingAtWindow}
     * says the player is aiming at gives each part its own real hardness/correct-tool speed (glass
     * pane vs. the host stair/slab), and reusing that exact aim test keeps the speed you mine at
     * consistent with what {@link WindowLogHelper#tryPartialBreak} actually breaks once you finish.
     */
    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof WindowedBlockEntity be && level instanceof Level realLevel) {
            BlockState targeted = WindowLogHelper.pickTargetedState(realLevel, pos, be.getWindowState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                return targeted.getDestroyProgress(player, level, pos);
            }
        }

        return super.getDestroyProgress(state, player, level, pos);
    }

    /**
     * Vanilla's default (see {@code Block#playerWillDestroy}) fires the break-particle burst using
     * whatever {@code state} is being destroyed — this block's own single, blockstate-less state, whose
     * {@code RenderShape.INVISIBLE} model gives the particle system no texture to draw. Popping just the
     * window (see {@code WindowLogHelper#tryPartialBreak}) already fires its own correctly-textured event
     * for that case; this covers the other path, where aiming at the host lets vanilla's normal breaking
     * take over and destroy the whole block - delegating to whichever sub-state was actually aimed at
     * (same aim test as {@link #getDestroyProgress}) gives real glass/host particles either way.
     */
    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof WindowedBlockEntity be) {
            BlockState targeted = WindowLogHelper.pickTargetedState(level, pos, be.getWindowState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                level.levelEvent(player, 2001, pos, Block.getId(targeted));
                return;
            }
        }

        super.spawnDestroyParticles(level, player, pos, state);
    }

    /**
     * This block has no registered {@code BlockItem} of its own (window-logging is something you do to
     * an existing slab/stair, not something you place directly), so vanilla's default here would return
     * an empty pick-block result. Returning whichever sub-block the aiming player is actually looking at
     * - reusing the same aim test as {@link #getDestroyProgress}/{@link #spawnDestroyParticles}, via
     * {@link WindowLogHelper#pickTargetedStateForPickBlock} since this hook isn't given the player - lets
     * pick-block hand back a real glass pane/bars or a real host slab/stair, matching what that part of
     * the block actually is.
     */
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        if (level.getBlockEntity(pos) instanceof WindowedBlockEntity be) {
            BlockState targeted = WindowLogHelper.pickTargetedStateForPickBlock(level, pos, be.getWindowState(), be.getHostState());

            if (!targeted.isAir()) {
                return new ItemStack(targeted.getBlock());
            }
        }

        return super.getCloneItemStack(level, pos, state, includeData);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof WindowedBlockEntity be)) {
            return List.of();
        }

        List<ItemStack> drops = new ArrayList<>(2);
        if (!be.getHostState().isAir()) {
            drops.add(new ItemStack(be.getHostState().getBlock()));
        }
        if (!be.getWindowState().isAir()) {
            drops.add(new ItemStack(be.getWindowState().getBlock()));
        }

        return drops;
    }
}
