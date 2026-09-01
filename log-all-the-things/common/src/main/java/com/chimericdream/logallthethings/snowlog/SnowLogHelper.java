package com.chimericdream.logallthethings.snowlog;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.chimericdream.logallthethings.carpetlog.CarpetedBlock;

/**
 * Shared logic behind snow-logging: turning a plain slab/straight-stairs/wall/fence/chain/bars/pane
 * (see {@link SnowLogTags#SNOWABLE}) plus a held snow layer item into a {@link SnowedBlock}, letting
 * a player build it up one layer at a time (up to whatever headroom the host actually has), and
 * popping all its layers back out at once by targeting the snow while mining. Mirrors
 * {@code carpetlog.CarpetLogHelper} - see that class for the rationale behind wiring both entry
 * points to Architectury events rather than mixins, and for why the double-slab/non-straight-stair
 * guards only ever apply to those two host types.
 *
 * <p>Snow-logging and carpet-logging are mutually exclusive for now: a {@link CarpetedBlock} is never
 * a valid snow-log target, and (see {@code CarpetLogHelper#tryCarpetLog}) a {@link SnowedBlock} is
 * never a valid carpet-log target.
 */
public final class SnowLogHelper {
    /** One shy of a full snow block ({@link SnowLayerBlock#MAX_HEIGHT}), regardless of host. */
    public static final int MAX_LAYERS = SnowLayerBlock.MAX_HEIGHT - 1;

    private SnowLogHelper() {
    }

    public static EventResult tryPlaceSnow(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Level level = player.level();
        ItemStack stack = player.getItemInHand(hand);

        if (!player.mayBuild() || !(stack.getItem() instanceof BlockItem snowItem) || !snowItem.getBlock().defaultBlockState().is(SnowLogTags.SNOW_LAYER)) {
            return EventResult.pass();
        }
        // Sneaking bypasses snow-logging entirely, same as carpet-logging's sneak-bypass - passing
        // through here hands the click back to vanilla's normal BlockItem placement.
        if (player.isShiftKeyDown()) {
            return EventResult.pass();
        }

        BlockState targetState = level.getBlockState(pos);

        if (targetState.getBlock() instanceof SnowedBlock) {
            return tryAddLayer(level, pos, targetState, player, hand, stack);
        }

        if (!targetState.is(SnowLogTags.SNOWABLE) || targetState.getBlock() instanceof CarpetedBlock) {
            return EventResult.pass();
        }
        if (targetState.getOptionalValue(SlabBlock.TYPE).map(SlabType.DOUBLE::equals).orElse(false)) {
            return EventResult.pass();
        }
        if (targetState.getOptionalValue(StairBlock.SHAPE).map(shape -> shape != StairsShape.STRAIGHT).orElse(false)) {
            return EventResult.pass();
        }

        if (!level.isClientSide()) {
            BlockState snowState = snowItem.getBlock().defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1);

            BlockState snowedState = SnowLogBlocks.SNOWED_BLOCK.get().defaultBlockState();
            level.setBlock(pos, snowedState, Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(pos) instanceof SnowedBlockEntity be) {
                be.setHostState(targetState);
                be.setSnowState(snowState);
                be.setChanged();
                level.sendBlockUpdated(pos, targetState, targetState, Block.UPDATE_CLIENTS);

                // Deferred until here for the same reason as CarpetLogHelper#tryCarpetLog - a neighbour
                // recomputing its shape any earlier would see the placeholder SnowedBlock before its
                // host/snow state is populated.
                level.getBlockState(pos).updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
            }

            playPlaceSound(level, pos, snowState);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        player.swing(hand);

        return EventResult.interruptTrue();
    }

    /**
     * Right-clicking an already snow-logged block with another snow layer item stacks one more layer,
     * the same way right-clicking a real snow layer with more snow does - capped at whatever headroom
     * {@link #computeMaxLayers} says the host actually has. At the cap, this passes through to
     * vanilla's normal placement (e.g. starting a fresh snow layer in the block above), which is the
     * correct, expected fallback.
     */
    private static EventResult tryAddLayer(Level level, BlockPos pos, BlockState snowedState, Player player, InteractionHand hand, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof SnowedBlockEntity be)) {
            return EventResult.pass();
        }

        int currentLayers = SnowedBlock.layersOf(be.getSnowState());
        int maxLayers = computeMaxLayers(be.getHostState());
        if (currentLayers >= maxLayers) {
            return EventResult.pass();
        }

        if (!level.isClientSide()) {
            BlockState newSnowState = be.getSnowState().setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
            be.setSnowState(newSnowState);
            be.setChanged();
            level.sendBlockUpdated(pos, snowedState, snowedState, Block.UPDATE_CLIENTS);

            playPlaceSound(level, pos, newSnowState);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        player.swing(hand);

        return EventResult.interruptTrue();
    }

    private static void playPlaceSound(Level level, BlockPos pos, BlockState snowState) {
        SoundType sound = snowState.getSoundType();
        level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
    }

    /**
     * How many snow layers actually fit on {@code hostState} - the lesser of {@link #MAX_LAYERS} and
     * however much headroom the host's own shape leaves open. See {@code SnowedBlock#snowShape} for
     * exactly where that headroom is (above a bottom slab/stairs' low run, below a top slab/stairs'
     * flat side, or the full column for every other host).
     */
    public static int computeMaxLayers(BlockState hostState) {
        return Math.min(MAX_LAYERS, spaceLayers(hostState));
    }

    private static int spaceLayers(BlockState hostState) {
        if (hostState.getBlock() instanceof SlabBlock || hostState.getBlock() instanceof StairBlock) {
            // Half a block of headroom (0.5 / (1.0/8.0) layers) either above a bottom slab/stairs' own
            // top surface, or below a top slab/stairs' own flat underside.
            return 4;
        }

        return SnowLayerBlock.MAX_HEIGHT;
    }

    public static EventResult tryPartialBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
        if (!(state.getBlock() instanceof SnowedBlock) || !(level.getBlockEntity(pos) instanceof SnowedBlockEntity be)) {
            return EventResult.pass();
        }

        BlockState snowState = be.getSnowState();
        if (!isAimingAtSnow(level, pos, be.getHostState(), snowState, player)) {
            return EventResult.pass();
        }

        if (!player.isCreative()) {
            Block.dropResources(snowState, level, pos, null, player, player.getMainHandItem());
        }

        level.setBlock(pos, be.getHostState(), Block.UPDATE_ALL);
        level.levelEvent(null, 2001, pos, Block.getId(snowState));

        return EventResult.interruptFalse();
    }

    /**
     * Whether {@code player} is currently aiming at the snow portion of the block at {@code pos} -
     * mirrors {@code CarpetLogHelper#isAimingAtCarpet}, using {@code SnowedBlock#snowShape} (the exact
     * fitted geometry actually drawn for {@code hostState}/{@code layers}) as the hit-test shape.
     */
    public static boolean isAimingAtSnow(Level level, BlockPos pos, BlockState hostState, BlockState snowState, Player player) {
        if (snowState.isAir()) {
            return false;
        }

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getLookAngle().scale(player.blockInteractionRange()));
        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(pos)) {
            return false;
        }

        Vec3 localHit = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        return SnowedBlock.snowShape(hostState, SnowedBlock.layersOf(snowState)).toAabbs().stream()
            .anyMatch(box -> box.inflate(0.05).contains(localHit));
    }

    public static BlockState pickTargetedState(Level level, BlockPos pos, BlockState snowState, BlockState hostState, Player player) {
        return isAimingAtSnow(level, pos, hostState, snowState, player) ? snowState : hostState;
    }

    /**
     * Same decision as {@link #pickTargetedState}, but for {@code SnowedBlock#getCloneItemStack} - see
     * {@code CarpetLogHelper#pickTargetedStateForPickBlock} for why scanning nearby players is a
     * reasonable stand-in when the vanilla hook doesn't hand us the player doing the picking.
     */
    public static BlockState pickTargetedStateForPickBlock(LevelReader levelReader, BlockPos pos, BlockState snowState, BlockState hostState) {
        if (levelReader instanceof Level level) {
            for (Player player : level.players()) {
                if (player.isWithinBlockInteractionRange(pos, 1.0) && isAimingAtSnow(level, pos, hostState, snowState, player)) {
                    return snowState;
                }
            }
        }

        return hostState;
    }

    /** Mirrors {@code CarpetLogHelper#effectiveNeighborState}, substituting a {@link SnowedBlock} neighbour's stored host state the same way. */
    public static BlockState effectiveNeighborState(BlockGetter level, BlockPos neighborPos, BlockState neighborState) {
        if (neighborState.getBlock() instanceof SnowedBlock && level.getBlockEntity(neighborPos) instanceof SnowedBlockEntity be) {
            BlockState hostState = be.getHostState();
            if (!hostState.isAir()) {
                return hostState;
            }
        }

        return neighborState;
    }
}
