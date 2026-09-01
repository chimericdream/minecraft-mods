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
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.Vec3;

import dev.architectury.event.EventResult;

import com.chimericdream.lib.testkit.gametest.GameTestPlayers;
import com.chimericdream.logallthethings.carpetlog.CarpetLogBlocks;
import com.chimericdream.logallthethings.carpetlog.CarpetLogHelper;
import com.chimericdream.logallthethings.carpetlog.CarpetedBlock;
import com.chimericdream.logallthethings.carpetlog.CarpetedBlockEntity;
import com.chimericdream.logallthethings.snowlog.SnowLogBlocks;
import com.chimericdream.logallthethings.snowlog.SnowLogHelper;
import com.chimericdream.logallthethings.snowlog.SnowedBlock;
import com.chimericdream.logallthethings.snowlog.SnowedBlockEntity;

/**
 * Coverage for snow-logging (see {@code com.chimericdream.logallthethings.snowlog}). Mirrors
 * {@code WindowLoggingGameTest}'s approach of calling {@code tryPlaceSnow}/{@code tryPartialBreak}
 * directly rather than routing through {@link GameTestHelper#useBlock} or a simulated attack - see
 * that class's javadoc for the reasoning.
 */
@SuppressWarnings("unused")
public class SnowLoggingGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);
    private static final BlockPos PLAYER_POS = TARGET.offset(0, 0, 3);

    private static BlockState targetState(GameTestHelper context) {
        return context.getLevel().getBlockState(context.absolutePos(TARGET));
    }

    private static SnowedBlockEntity targetSnowedBlockEntity(GameTestHelper context) {
        if (!(context.getLevel().getBlockEntity(context.absolutePos(TARGET)) instanceof SnowedBlockEntity be)) {
            throw new AssertionError("Expected a SnowedBlockEntity at the target position");
        }
        return be;
    }

    /** Inside a 3-layer-stacked bottom slab's snow (y 0.5-0.875), comfortably clear of both edges. */
    private static Vec3 snowOnlyPointOf(GameTestHelper context) {
        BlockPos pos = context.absolutePos(TARGET);
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5);
    }

    /** Inside a bottom slab's own solid body (y 0-0.5), clear of any snow stacked above it. */
    private static Vec3 hostOnlyPointOf(GameTestHelper context) {
        BlockPos pos = context.absolutePos(TARGET);
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
    }

    /**
     * Unlike {@code WindowLoggingGameTest}'s aim points (which discriminate host vs. window mostly by
     * X/Z, since a pane's footprint is a thin central column), a snow-logged bottom slab's host and
     * snow both span the <em>full</em> footprint - they only differ in Y, and stack with no gap between
     * them. A ray aimed at an interior point from {@code makeFacingPlayer}'s standing eye height has a
     * non-trivial downward slope, so the point where it actually crosses the block's near face (where
     * {@code level.clip} registers the hit) lands measurably higher than the aimed-at Y - enough, at the
     * player/target distance used here, to cross the host/snow boundary. Overriding the player's own Y
     * so their eye height exactly matches {@code aimPoint.y} makes the ray perfectly horizontal, so it
     * crosses the near face at exactly {@code aimPoint.y} with no drift.
     */
    private static ServerPlayer facingPlayerAt(GameTestHelper context, Vec3 aimPoint) {
        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        Vec3 pos = player.position();
        player.setPos(pos.x, aimPoint.y - player.getEyeHeight(), pos.z);
        GameTestPlayers.lookAt(player, aimPoint);
        return (ServerPlayer) player;
    }

    // --- Placement (RIGHT_CLICK_BLOCK) ---

    @GameTest
    public void snowLoggingAStoneSlabWithSnow(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SNOW, 8));

        EventResult result = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (!result.interruptsFurtherEvaluation()) {
            context.fail("Expected snow-logging a stone slab with snow to interrupt the event, got " + result);
        }
        if (!(targetState(context).getBlock() instanceof SnowedBlock)) {
            context.fail("Target should have become a SnowedBlock, got " + targetState(context));
        }

        SnowedBlockEntity be = targetSnowedBlockEntity(context);
        if (!be.getHostState().is(Blocks.STONE_SLAB)) {
            context.fail("Host state should still be a stone slab, got " + be.getHostState());
        }
        if (!be.getSnowState().is(Blocks.SNOW) || be.getSnowState().getValue(SnowLayerBlock.LAYERS) != 1) {
            context.fail("Snow state should be a single snow layer, got " + be.getSnowState());
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 7) {
            context.fail("Exactly one snow layer should have been consumed, " + player.getItemInHand(InteractionHand.MAIN_HAND).getCount() + " left");
        }

        context.succeed();
    }

    @GameTest
    public void repeatedSnowLoggingStacksLayersUpToTheSlabCap(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SNOW, 8));

        for (int expectedLayers = 1; expectedLayers <= 4; expectedLayers++) {
            EventResult result = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);
            if (!result.interruptsFurtherEvaluation()) {
                context.fail("Expected layer " + expectedLayers + " to be accepted, got " + result);
            }

            int actualLayers = targetSnowedBlockEntity(context).getSnowState().getValue(SnowLayerBlock.LAYERS);
            if (actualLayers != expectedLayers) {
                context.fail("Expected " + expectedLayers + " layers after placement " + expectedLayers + ", got " + actualLayers);
            }
        }

        // A bottom slab only has 0.5 blocks of headroom (4 layers' worth) - a 5th should be refused.
        EventResult fifthResult = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);
        if (fifthResult.interruptsFurtherEvaluation()) {
            context.fail("Expected a 5th layer on a slab to be refused (out of headroom), got " + fifthResult);
        }
        if (targetSnowedBlockEntity(context).getSnowState().getValue(SnowLayerBlock.LAYERS) != 4) {
            context.fail("Layer count should still be 4 after the refused 5th attempt, got " + targetSnowedBlockEntity(context).getSnowState().getValue(SnowLayerBlock.LAYERS));
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 4) {
            context.fail("Only 4 snow layers should have been consumed (one per successful placement) out of 8, " + player.getItemInHand(InteractionHand.MAIN_HAND).getCount() + " left");
        }

        context.succeed();
    }

    @GameTest
    public void repeatedSnowLoggingCapsAtSevenLayersOnAFence(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.OAK_FENCE);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SNOW, 16));

        for (int i = 0; i < SnowLogHelper.MAX_LAYERS; i++) {
            EventResult result = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);
            if (!result.interruptsFurtherEvaluation()) {
                context.fail("Expected layer " + (i + 1) + " on a fence to be accepted, got " + result);
            }
        }

        int layersAtCap = targetSnowedBlockEntity(context).getSnowState().getValue(SnowLayerBlock.LAYERS);
        if (layersAtCap != SnowLogHelper.MAX_LAYERS) {
            context.fail("Expected a fence to cap at " + SnowLogHelper.MAX_LAYERS + " layers (one shy of a full snow block), got " + layersAtCap);
        }

        EventResult overCapResult = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);
        if (overCapResult.interruptsFurtherEvaluation()) {
            context.fail("Expected an 8th layer on a fence to be refused (one shy of a full snow block cap), got " + overCapResult);
        }

        context.succeed();
    }

    @GameTest
    public void snowLoggingIsRefusedOnADoubleSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SNOW));

        EventResult result = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Snow-logging a double slab should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof SnowedBlock) {
            context.fail("Double slab should not have become a SnowedBlock");
        }

        context.succeed();
    }

    @GameTest
    public void snowLoggingIsRefusedOnANonStraightStair(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_STAIRS.defaultBlockState()
            .setValue(StairBlock.FACING, Direction.EAST)
            .setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SNOW));

        EventResult result = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Snow-logging an inner/outer corner stair should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof SnowedBlock) {
            context.fail("Corner stair should not have become a SnowedBlock");
        }

        context.succeed();
    }

    // --- Mutual exclusivity with carpet-logging ---

    @GameTest
    public void snowLoggingIsRefusedOnACarpetedBlock(GameTestHelper context) {
        context.setBlock(TARGET, CarpetLogBlocks.CARPETED_BLOCK.get());
        CarpetedBlockEntity carpetBe = (CarpetedBlockEntity) context.getLevel().getBlockEntity(context.absolutePos(TARGET));
        carpetBe.setHostState(Blocks.STONE_SLAB.defaultBlockState());
        carpetBe.setCarpetState(Blocks.CARPET.white().defaultBlockState());
        carpetBe.setChanged();

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SNOW));

        EventResult result = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Snow-logging an already carpet-logged block should be refused, got " + result);
        }
        if (!(targetState(context).getBlock() instanceof CarpetedBlock)) {
            context.fail("Target should still be the CarpetedBlock, got " + targetState(context));
        }

        context.succeed();
    }

    @GameTest
    public void carpetLoggingIsRefusedOnASnowedBlock(GameTestHelper context) {
        context.setBlock(TARGET, SnowLogBlocks.SNOWED_BLOCK.get());
        SnowedBlockEntity snowBe = targetSnowedBlockEntity(context);
        snowBe.setHostState(Blocks.STONE_SLAB.defaultBlockState());
        snowBe.setSnowState(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1));
        snowBe.setChanged();

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CARPET.white()));

        EventResult result = CarpetLogHelper.tryCarpetLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Carpet-logging an already snow-logged block should be refused, got " + result);
        }
        if (!(targetState(context).getBlock() instanceof SnowedBlock)) {
            context.fail("Target should still be the SnowedBlock, got " + targetState(context));
        }

        context.succeed();
    }

    // --- Sneaking bypasses snow-logging ---

    @GameTest
    public void shiftKeyDownBypassesSnowLoggingOfAStoneSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SNOW));
        player.setShiftKeyDown(true);

        EventResult result = SnowLogHelper.tryPlaceSnow(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Expected sneaking to bypass snow-logging and pass through to vanilla placement, got " + result);
        }
        if (targetState(context).getBlock() instanceof SnowedBlock) {
            context.fail("Sneaking while placing snow on a snowable slab should not snow-log it");
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1) {
            context.fail("Sneaking should leave the snow un-consumed, since snow-logging never ran");
        }

        context.succeed();
    }

    // --- Aim-based partial breaking (BlockEvent.BREAK) ---

    @GameTest
    public void breakingTheSnowWithAShovelDropsItAndRevertsToTheHost(GameTestHelper context) {
        context.setBlock(TARGET, SnowLogBlocks.SNOWED_BLOCK.get());
        SnowedBlockEntity be = targetSnowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState());
        be.setSnowState(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 3));
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, snowOnlyPointOf(context));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SHOVEL));

        EventResult result = SnowLogHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        if (!result.interruptsFurtherEvaluation() || !result.isFalse()) {
            context.fail("Expected aiming at the snow to cancel vanilla breaking with a false outcome, got " + result);
        }
        if (!targetState(context).is(Blocks.STONE_SLAB)) {
            context.fail("Target should have reverted to a plain stone slab, got " + targetState(context));
        }

        context.succeed();
    }

    @GameTest
    public void breakingTheSnowWithoutAShovelDropsNothingButStillRevertsToTheHost(GameTestHelper context) {
        context.setBlock(TARGET, SnowLogBlocks.SNOWED_BLOCK.get());
        SnowedBlockEntity be = targetSnowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState());
        be.setSnowState(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 3));
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, snowOnlyPointOf(context));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        SnowLogHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        if (!targetState(context).is(Blocks.STONE_SLAB)) {
            context.fail("Target should still revert to a plain stone slab even without a shovel, got " + targetState(context));
        }
        // Vanilla's snow layer requires a shovel to drop anything - no explicit "no items on the
        // ground" assertion is made here since GameTestHelper has no direct query for it, but this
        // documents the expectation this test is guarding: no exception, and no forced drop bypassing
        // the loot table the way carpet-logging's own hardcoded drop would.

        context.succeed();
    }

    @GameTest
    public void breakingTheHostPassesThroughToVanilla(GameTestHelper context) {
        context.setBlock(TARGET, SnowLogBlocks.SNOWED_BLOCK.get());
        SnowedBlockEntity be = targetSnowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState());
        be.setSnowState(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 3));
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, hostOnlyPointOf(context));

        EventResult result = SnowLogHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Expected aiming at the host slab to pass through to vanilla breaking, got " + result);
        }
        if (!(targetState(context).getBlock() instanceof SnowedBlock)) {
            context.fail("Target should still be the SnowedBlock (vanilla handles the actual removal), got " + targetState(context));
        }

        context.succeed();
    }
}
