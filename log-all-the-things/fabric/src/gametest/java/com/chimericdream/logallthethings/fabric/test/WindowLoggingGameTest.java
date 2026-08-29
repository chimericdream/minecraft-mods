package com.chimericdream.logallthethings.fabric.test;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.Vec3;

import dev.architectury.event.EventResult;

import com.chimericdream.lib.testkit.gametest.GameTestPlayers;
import com.chimericdream.logallthethings.windowlog.WindowLoggingBlocks;
import com.chimericdream.logallthethings.windowlog.WindowLoggingHelper;
import com.chimericdream.logallthethings.windowlog.WindowLoggedBlock;
import com.chimericdream.logallthethings.windowlog.WindowLoggedBlockEntity;

/**
 * Coverage for window-logging (see {@code com.chimericdream.logallthethings.windowlog}).
 *
 * <p>{@code tryWindowLog}/{@code tryPartialBreak} are called directly rather than routed through
 * {@link GameTestHelper#useBlock} or a simulated attack: they're the exact handlers
 * {@code InteractionEvent.RIGHT_CLICK_BLOCK}/{@code BlockEvent.BREAK} call in {@code LogAllTheThingsMod},
 * so calling them directly with a positioned mock player exercises the real logic without needing to
 * reproduce the interaction manager's dispatch plumbing (see {@code LavaLoggingGameTest}'s javadoc for
 * the same reasoning applied to buckets).
 *
 * <p>The partial-break tests pick aim points deliberately outside the ambiguous region where a pane's
 * default (unconnected) column shape and a slab's half-height shape could both claim the same point:
 * {@code paneOnlyPointOf}/{@code hostOnlyPointOf} choose a low Y clear of a top slab's [0.5, 1] band for
 * the pane hit, and a corner X/Z clear of the pane's central column for the host hit.
 */
@SuppressWarnings("unused")
public class WindowLoggingGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);
    private static final BlockPos PLAYER_POS = TARGET.offset(0, 0, 3);

    private static BlockState targetState(GameTestHelper context) {
        return context.getLevel().getBlockState(context.absolutePos(TARGET));
    }

    private static WindowLoggedBlockEntity targetWindowedBlockEntity(GameTestHelper context) {
        if (!(context.getLevel().getBlockEntity(context.absolutePos(TARGET)) instanceof WindowLoggedBlockEntity be)) {
            throw new AssertionError("Expected a WindowedBlockEntity at the target position");
        }
        return be;
    }

    private static Vec3 paneOnlyPointOf(GameTestHelper context) {
        BlockPos pos = context.absolutePos(TARGET);
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
    }

    private static Vec3 hostOnlyPointOf(GameTestHelper context) {
        BlockPos pos = context.absolutePos(TARGET);
        return new Vec3(pos.getX() + 0.1, pos.getY() + 0.75, pos.getZ() + 0.1);
    }

    private static ServerPlayer facingPlayerAt(GameTestHelper context, Vec3 aimPoint) {
        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        GameTestPlayers.lookAt(player, aimPoint);
        return (ServerPlayer) player;
    }

    // --- Placement (RIGHT_CLICK_BLOCK) ---

    @GameTest
    public void windowLoggingAStoneSlabWithAGlassPane(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE, 2));

        EventResult result = WindowLoggingHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (!result.interruptsFurtherEvaluation()) {
            context.fail("Expected window-logging a stone slab with a glass pane to interrupt the event, got " + result);
        }

        if (!(targetState(context).getBlock() instanceof WindowLoggedBlock)) {
            context.fail("Target should have become a WindowedBlock, got " + targetState(context));
        }

        WindowLoggedBlockEntity be = targetWindowedBlockEntity(context);
        if (!be.getHostState().is(Blocks.STONE_SLAB)) {
            context.fail("Host state should still be a stone slab, got " + be.getHostState());
        }
        if (!be.getWindowState().is(Blocks.GLASS_PANE)) {
            context.fail("Window state should be a glass pane, got " + be.getWindowState());
        }
        if (!be.getWindowState().getValue(CrossCollisionBlock.EAST) || !be.getWindowState().getValue(CrossCollisionBlock.WEST)) {
            context.fail("Window state should be forced flat east-west (no host facing to derive from), got " + be.getWindowState());
        }

        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1) {
            context.fail("Exactly one glass pane should have been consumed, " + player.getItemInHand(InteractionHand.MAIN_HAND).getCount() + " left");
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingAnEastFacingStairsOrientsThePaneNorthSouth(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        WindowLoggingHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        WindowLoggedBlockEntity be = targetWindowedBlockEntity(context);
        if (!be.getWindowState().getValue(CrossCollisionBlock.NORTH) || !be.getWindowState().getValue(CrossCollisionBlock.SOUTH)) {
            context.fail("Window state should be forced flat north-south to face the same way an east-facing stair opens, got " + be.getWindowState());
        }
        if (be.getWindowState().getValue(CrossCollisionBlock.EAST) || be.getWindowState().getValue(CrossCollisionBlock.WEST)) {
            context.fail("Window state should not also be connected east-west, got " + be.getWindowState());
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingIsRefusedOnADoubleSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        EventResult result = WindowLoggingHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Window-logging a double slab should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowLoggedBlock) {
            context.fail("Double slab should not have become a WindowedBlock");
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingIsRefusedOnANonStraightStair(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_STAIRS.defaultBlockState()
            .setValue(StairBlock.FACING, Direction.EAST)
            .setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        EventResult result = WindowLoggingHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Window-logging an inner/outer corner stair should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowLoggedBlock) {
            context.fail("Corner stair should not have become a WindowedBlock");
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingIsRefusedOnANonWindowableBlock(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        EventResult result = WindowLoggingHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Window-logging plain stone should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowLoggedBlock) {
            context.fail("Plain stone should not have become a WindowedBlock");
        }

        context.succeed();
    }

    // --- Aim-based partial breaking (BlockEvent.BREAK) ---

    @GameTest
    public void breakingThePaneRevertsToThePlainSlab(GameTestHelper context) {
        context.setBlock(TARGET, WindowLoggingBlocks.WINDOW_LOGGED_BLOCK.get());
        WindowLoggedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, paneOnlyPointOf(context));

        EventResult result = WindowLoggingHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        if (!result.interruptsFurtherEvaluation() || !result.isFalse()) {
            context.fail("Expected aiming at the pane to cancel vanilla breaking with a false outcome, got " + result);
        }
        if (!targetState(context).is(Blocks.STONE_SLAB)) {
            context.fail("Target should have reverted to a plain stone slab, got " + targetState(context));
        }

        context.succeed();
    }

    @GameTest
    public void breakingTheHostBreaksTheWholeWindowedBlock(GameTestHelper context) {
        context.setBlock(TARGET, WindowLoggingBlocks.WINDOW_LOGGED_BLOCK.get());
        WindowLoggedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, hostOnlyPointOf(context));

        EventResult result = WindowLoggingHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Expected aiming at the host slab to pass through to vanilla breaking, got " + result);
        }
        if (!(targetState(context).getBlock() instanceof WindowLoggedBlock)) {
            context.fail("Target should still be the WindowedBlock (vanilla handles the actual removal), got " + targetState(context));
        }

        context.succeed();
    }
}
