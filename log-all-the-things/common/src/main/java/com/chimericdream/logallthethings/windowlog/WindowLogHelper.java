package com.chimericdream.logallthethings.windowlog;

import java.util.Optional;

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
 * {@link WindowedBlock}, and (per the aim-based partial-breaking design) letting a player pop just
 * the pane back out by targeting it specifically when mining.
 *
 * <p>Both entry points are wired to cross-loader Architectury events in
 * {@code LogAllTheThingsMod.init()} ({@code InteractionEvent.RIGHT_CLICK_BLOCK} and
 * {@code BlockEvent.BREAK}) rather than mixins — unlike lava-logging, nothing here needs to change
 * vanilla's {@code StairBlock}/{@code SlabBlock} classes themselves.
 */
public final class WindowLogHelper {
    private WindowLogHelper() {
    }

    public static EventResult tryWindowLog(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Level level = player.level();
        ItemStack stack = player.getItemInHand(hand);

        if (!player.mayBuild() || !(stack.getItem() instanceof BlockItem paneItem) || !paneItem.getBlock().defaultBlockState().is(WindowLogTags.WINDOW)) {
            return EventResult.pass();
        }

        BlockState targetState = level.getBlockState(pos);
        if (!targetState.is(WindowLogTags.WINDOWABLE) || targetState.getBlock() instanceof WindowedBlock) {
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

            level.setBlock(pos, WindowLogBlocks.WINDOWED_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(pos) instanceof WindowedBlockEntity be) {
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
     * faces north/south) when the host has no horizontal facing at all (slabs, symmetric either way), or
     * whichever axis the host's {@link StairBlock#FACING} itself runs along, so the pane's collision
     * plate stands across the stair's open notch the same way {@code WindowFrameRenderer}'s hand-authored
     * glass mesh already does — extending <em>along</em> {@code FACING}'s axis, not perpendicular to it
     * (a stair's notch spans the full width of the axis perpendicular to {@code FACING}, so a plate
     * extending along {@code FACING}'s own axis is the one that reaches across that opening). The
     * no-facing case is checked explicitly (rather than defaulting {@code FACING} to some direction and
     * falling into one of the two branches below) because {@code NORTH} and {@code SOUTH} no longer share
     * an answer with each other the way they coincidentally did under the old (buggy) perpendicular
     * logic — a real north/south-facing stair needs a different outcome than a slab with no facing at
     * all, even though both would otherwise land on the same default.
     */
    private static BlockState orientWindowPane(BlockState windowState, BlockState hostState) {
        Optional<Direction> facing = hostState.getOptionalValue(StairBlock.FACING);

        if (facing.isEmpty() || facing.get().getAxis() == Direction.Axis.X) {
            return windowState.setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true);
        }

        return windowState.setValue(CrossCollisionBlock.NORTH, true).setValue(CrossCollisionBlock.SOUTH, true);
    }

    public static EventResult tryPartialBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
        if (!(state.getBlock() instanceof WindowedBlock) || !(level.getBlockEntity(pos) instanceof WindowedBlockEntity be)) {
            return EventResult.pass();
        }

        BlockState windowState = be.getWindowState();
        if (!isAimingAtWindow(level, pos, windowState, player)) {
            return EventResult.pass();
        }

        if (!player.isCreative()) {
            Block.popResource(level, pos, new ItemStack(windowState.getBlock()));
        }

        level.setBlock(pos, be.getHostState(), Block.UPDATE_ALL);
        level.levelEvent(null, 2001, pos, Block.getId(windowState));

        return EventResult.interruptFalse();
    }

    /**
     * Whether {@code player} is currently aiming precisely at {@code windowState}'s own shape within
     * the block at {@code pos}, rather than at the host portion — shared by {@link #tryPartialBreak}
     * (which pane to pop) and {@code WindowedBlock#getDestroyProgress} (which sub-block's mining speed
     * applies), so a block always breaks at the speed of whichever part the completed break actually
     * affects.
     */
    public static boolean isAimingAtWindow(Level level, BlockPos pos, BlockState windowState, Player player) {
        if (windowState.isAir()) {
            return false;
        }

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getLookAngle().scale(player.blockInteractionRange()));
        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(pos)) {
            return false;
        }

        Vec3 localHit = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        return windowState.getShape(level, pos).toAabbs().stream()
            .anyMatch(box -> box.inflate(0.05).contains(localHit));
    }
}
