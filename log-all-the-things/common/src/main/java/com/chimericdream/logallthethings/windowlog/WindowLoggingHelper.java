package com.chimericdream.logallthethings.windowlog;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Shared logic behind window-logging: turning a plain slab/stair plus a held pane into a
 * {@link WindowLoggedBlock}, and (per the aim-based partial-breaking design) letting a player pop just
 * the pane back out by targeting it specifically when mining.
 *
 * <p>Both entry points are wired to cross-loader Architectury events in
 * {@code LogAllTheThingsMod.init()} ({@code InteractionEvent.RIGHT_CLICK_BLOCK} and
 * {@code BlockEvent.BREAK}) rather than mixins — unlike lava-logging, nothing here needs to change
 * vanilla's {@code StairBlock}/{@code SlabBlock} classes themselves.
 */
public final class WindowLoggingHelper {
    private WindowLoggingHelper() {
    }

    public static EventResult tryWindowLog(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Level level = player.level();
        ItemStack stack = player.getItemInHand(hand);

        if (!player.mayBuild() || !(stack.getItem() instanceof BlockItem paneItem) || !paneItem.getBlock().defaultBlockState().is(WindowLoggingTags.WINDOW)) {
            return EventResult.pass();
        }

        BlockState targetState = level.getBlockState(pos);
        if (!targetState.is(WindowLoggingTags.WINDOW_LOGGABLE) || targetState.getBlock() instanceof WindowLoggedBlock) {
            return EventResult.pass();
        }
        if (targetState.getOptionalValue(SlabBlock.TYPE).map(SlabType.DOUBLE::equals).orElse(false)) {
            return EventResult.pass();
        }
        if (targetState.getOptionalValue(StairBlock.SHAPE).map(shape -> shape != StairsShape.STRAIGHT).orElse(false)) {
            return EventResult.pass();
        }

        if (!level.isClientSide()) {
            BlockState windowState = orientWindowPane(paneItem.getBlock().defaultBlockState(), targetState);

            level.setBlock(pos, WindowLoggingBlocks.WINDOW_LOGGED_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(pos) instanceof WindowLoggedBlockEntity be) {
                be.setHostState(targetState);
                be.setWindowState(windowState);
                be.setChanged();
                level.sendBlockUpdated(pos, targetState, targetState, Block.UPDATE_CLIENTS);
            }

            SoundType sound = windowState.getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        player.swing(hand);

        return EventResult.interruptTrue();
    }

    /**
     * A pane's default (no-neighbour) shape is just its unconnected center post — deriving connections
     * from the real world neighbours (as a placed pane normally would) leaves it that way here, since
     * window-logging's neighbours are almost never another connectable pane/wall. A real window should
     * look like a flat pane filling the opening instead, so this forces a connection on one axis
     * unconditionally rather than computing it: {@link CrossCollisionBlock#EAST}/{@code WEST} (pane
     * faces north/south) when the host has no horizontal facing (slabs, symmetric either way), or
     * whichever axis is perpendicular to the host's {@link StairBlock#FACING} so the pane faces the same
     * way the stair opens.
     */
    private static BlockState orientWindowPane(BlockState windowState, BlockState hostState) {
        Direction facing = hostState.getOptionalValue(StairBlock.FACING).orElse(Direction.NORTH);

        if (facing.getAxis() == Direction.Axis.X) {
            return windowState.setValue(CrossCollisionBlock.NORTH, true).setValue(CrossCollisionBlock.SOUTH, true);
        }

        return windowState.setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true);
    }

    public static EventResult tryPartialBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
        if (!(state.getBlock() instanceof WindowLoggedBlock) || !(level.getBlockEntity(pos) instanceof WindowLoggedBlockEntity be)) {
            return EventResult.pass();
        }

        BlockState windowState = be.getWindowState();
        if (windowState.isAir()) {
            return EventResult.pass();
        }

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getLookAngle().scale(player.blockInteractionRange()));
        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(pos)) {
            return EventResult.pass();
        }

        Vec3 localHit = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        boolean hitWindow = windowState.getShape(level, pos).toAabbs().stream()
            .anyMatch(box -> box.inflate(0.05).contains(localHit));
        if (!hitWindow) {
            return EventResult.pass();
        }

        if (!player.isCreative()) {
            Block.popResource(level, pos, new ItemStack(windowState.getBlock()));
        }

        level.setBlock(pos, be.getHostState(), Block.UPDATE_ALL);
        level.levelEvent(null, 2001, pos, Block.getId(windowState));

        return EventResult.interruptFalse();
    }
}
