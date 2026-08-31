package com.chimericdream.logallthethings.carpetlog;

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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Shared logic behind carpet-logging: turning a plain slab/stair plus a held carpet into a
 * {@link CarpetedBlock}, and letting a player pop just the carpet back out by targeting it
 * specifically when mining. Mirrors {@code windowlog.WindowLogHelper} — see that class for the
 * rationale behind wiring both entry points to Architectury events rather than mixins. Carpet needs no
 * equivalent of {@code orientWindowPane}: a real {@code CarpetBlock} carries no orientation properties
 * of its own, so the item's default state is placed as-is.
 */
public final class CarpetLogHelper {
    private CarpetLogHelper() {
    }

    public static EventResult tryCarpetLog(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Level level = player.level();
        ItemStack stack = player.getItemInHand(hand);

        if (!player.mayBuild() || !(stack.getItem() instanceof BlockItem carpetItem) || !carpetItem.getBlock().defaultBlockState().is(CarpetLogTags.CARPET)) {
            return EventResult.pass();
        }
        // Sneaking bypasses carpet-logging entirely, same as window-logging's sneak-bypass - passing
        // through here hands the click back to vanilla's normal BlockItem placement.
        if (player.isShiftKeyDown()) {
            return EventResult.pass();
        }

        BlockState targetState = level.getBlockState(pos);
        if (!targetState.is(CarpetLogTags.CARPETABLE) || targetState.getBlock() instanceof CarpetedBlock) {
            return EventResult.pass();
        }
        if (targetState.getOptionalValue(SlabBlock.TYPE).map(SlabType.DOUBLE::equals).orElse(false)) {
            return EventResult.pass();
        }
        if (targetState.getOptionalValue(StairBlock.SHAPE).map(shape -> shape != StairsShape.STRAIGHT).orElse(false)) {
            return EventResult.pass();
        }

        if (!level.isClientSide()) {
            BlockState carpetState = carpetItem.getBlock().defaultBlockState();

            BlockState carpetedState = CarpetLogBlocks.CARPETED_BLOCK.get().defaultBlockState();
            level.setBlock(pos, carpetedState, Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(pos) instanceof CarpetedBlockEntity be) {
                be.setHostState(targetState);
                be.setCarpetState(carpetState);
                be.setChanged();
                level.sendBlockUpdated(pos, targetState, targetState, Block.UPDATE_CLIENTS);

                // Deferred until here for the same reason as WindowLogHelper#tryWindowLog - a neighbour
                // recomputing its shape any earlier would see the placeholder CarpetedBlock before its
                // host/carpet state is populated.
                level.getBlockState(pos).updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
            }

            SoundType sound = carpetState.getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        player.swing(hand);

        return EventResult.interruptTrue();
    }

    public static EventResult tryPartialBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
        if (!(state.getBlock() instanceof CarpetedBlock) || !(level.getBlockEntity(pos) instanceof CarpetedBlockEntity be)) {
            return EventResult.pass();
        }

        BlockState carpetState = be.getCarpetState();
        if (!isAimingAtCarpet(level, pos, carpetState, player)) {
            return EventResult.pass();
        }

        if (!player.isCreative()) {
            Block.popResource(level, pos, new ItemStack(carpetState.getBlock()));
        }

        level.setBlock(pos, be.getHostState(), Block.UPDATE_ALL);
        level.levelEvent(null, 2001, pos, Block.getId(carpetState));

        return EventResult.interruptFalse();
    }

    /**
     * Whether {@code player} is currently aiming at {@code carpetState}'s own (real, unfitted) shape
     * within the block at {@code pos} — shared by {@link #tryPartialBreak} (which sub-block to pop) and
     * {@code CarpetedBlock#getDestroyProgress} (which sub-block's mining speed applies). Mirrors
     * {@code WindowLogHelper#isAimingAtWindow}, including using the sub-block's own natural shape here
     * rather than {@code CarpetedBlock}'s fitted-to-the-host-notch collision shape — good enough for aim
     * detection, and consistent with the precedent that class already sets.
     */
    public static boolean isAimingAtCarpet(Level level, BlockPos pos, BlockState carpetState, Player player) {
        if (carpetState.isAir()) {
            return false;
        }

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getLookAngle().scale(player.blockInteractionRange()));
        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(pos)) {
            return false;
        }

        Vec3 localHit = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        return carpetState.getShape(level, pos).toAabbs().stream()
            .anyMatch(box -> box.inflate(0.05).contains(localHit));
    }

    public static BlockState pickTargetedState(Level level, BlockPos pos, BlockState carpetState, BlockState hostState, Player player) {
        return isAimingAtCarpet(level, pos, carpetState, player) ? carpetState : hostState;
    }

    /**
     * Same decision as {@link #pickTargetedState}, but for {@code CarpetedBlock#getCloneItemStack} -
     * see {@code WindowLogHelper#pickTargetedStateForPickBlock} for why scanning nearby players is a
     * reasonable stand-in when the vanilla hook doesn't hand us the player doing the picking.
     */
    public static BlockState pickTargetedStateForPickBlock(LevelReader levelReader, BlockPos pos, BlockState carpetState, BlockState hostState) {
        if (levelReader instanceof Level level) {
            for (Player player : level.players()) {
                if (player.isWithinBlockInteractionRange(pos, 1.0) && isAimingAtCarpet(level, pos, carpetState, player)) {
                    return carpetState;
                }
            }
        }

        return hostState;
    }
}
