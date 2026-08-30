package com.chimericdream.logallthethings.fabric.test;

import java.lang.reflect.Method;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import dev.architectury.event.EventResult;

import com.chimericdream.lib.testkit.gametest.GameTestPlayers;
import com.chimericdream.logallthethings.lavalog.LavaLogProperties;
import com.chimericdream.logallthethings.windowlog.WindowLogBlocks;
import com.chimericdream.logallthethings.windowlog.WindowLogHelper;
import com.chimericdream.logallthethings.windowlog.WindowedBlock;
import com.chimericdream.logallthethings.windowlog.WindowedBlockEntity;

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

    private static WindowedBlockEntity targetWindowedBlockEntity(GameTestHelper context) {
        if (!(context.getLevel().getBlockEntity(context.absolutePos(TARGET)) instanceof WindowedBlockEntity be)) {
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

    /**
     * Same positioning as {@link #facingPlayerAt}, but via {@code makeMockServerPlayerInLevel} instead
     * of {@code makeMockServerPlayer} — {@code WindowedBlock#getCloneItemStack} (pick-block) isn't given
     * a player, so {@link WindowLogHelper#pickTargetedStateForPickBlock} finds one by scanning
     * {@code level.players()}, which only a player actually placed in the level (not a detached mock)
     * appears in.
     */
    private static ServerPlayer facingPlayerInLevelAt(GameTestHelper context, Vec3 aimPoint) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        Vec3 playerPos = Vec3.atBottomCenterOf(context.absolutePos(PLAYER_POS));
        player.setPos(playerPos.x, playerPos.y, playerPos.z);
        GameTestPlayers.lookAt(player, aimPoint);
        return player;
    }

    // --- Placement (RIGHT_CLICK_BLOCK) ---

    @GameTest
    public void windowLoggingAStoneSlabWithAGlassPane(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE, 2));

        EventResult result = WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (!result.interruptsFurtherEvaluation()) {
            context.fail("Expected window-logging a stone slab with a glass pane to interrupt the event, got " + result);
        }

        if (!(targetState(context).getBlock() instanceof WindowedBlock)) {
            context.fail("Target should have become a WindowedBlock, got " + targetState(context));
        }

        WindowedBlockEntity be = targetWindowedBlockEntity(context);
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
    public void windowLoggingAnEastFacingStairsOrientsThePaneEastWest(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        if (!be.getWindowState().getValue(CrossCollisionBlock.EAST) || !be.getWindowState().getValue(CrossCollisionBlock.WEST)) {
            context.fail("Window state should be forced flat east-west to stand across the notch of an east-facing stair, got " + be.getWindowState());
        }
        if (be.getWindowState().getValue(CrossCollisionBlock.NORTH) || be.getWindowState().getValue(CrossCollisionBlock.SOUTH)) {
            context.fail("Window state should not also be connected north-south, got " + be.getWindowState());
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingAStoneSlabWithIronBars(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_BARS));

        EventResult result = WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (!result.interruptsFurtherEvaluation()) {
            context.fail("Expected window-logging a stone slab with iron bars to interrupt the event, got " + result);
        }

        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        if (!be.getWindowState().is(Blocks.IRON_BARS)) {
            context.fail("Window state should be iron bars, got " + be.getWindowState());
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingIsRefusedOnADoubleSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        EventResult result = WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Window-logging a double slab should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowedBlock) {
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

        EventResult result = WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Window-logging an inner/outer corner stair should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowedBlock) {
            context.fail("Corner stair should not have become a WindowedBlock");
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingIsRefusedOnANonWindowableBlock(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        EventResult result = WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Window-logging plain stone should be refused, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowedBlock) {
            context.fail("Plain stone should not have become a WindowedBlock");
        }

        context.succeed();
    }

    // --- Aim-based partial breaking (BlockEvent.BREAK) ---

    @GameTest
    public void breakingThePaneRevertsToThePlainSlab(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, paneOnlyPointOf(context));

        EventResult result = WindowLogHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        if (!result.interruptsFurtherEvaluation() || !result.isFalse()) {
            context.fail("Expected aiming at the pane to cancel vanilla breaking with a false outcome, got " + result);
        }
        if (!targetState(context).is(Blocks.STONE_SLAB)) {
            context.fail("Target should have reverted to a plain stone slab, got " + targetState(context));
        }

        context.succeed();
    }

    @GameTest
    public void breakingThePaneRevertsToALavaLoggedSlab(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get().defaultBlockState().setValue(LavaLogProperties.LAVALOGGED, true));
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP).setValue(LavaLogProperties.LAVALOGGED, true));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, paneOnlyPointOf(context));

        WindowLogHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        BlockState reverted = targetState(context);
        if (!reverted.is(Blocks.STONE_SLAB) || !reverted.getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Target should have reverted to a lava-logged stone slab, got " + reverted);
        }
        if (!context.getLevel().getFluidState(context.absolutePos(TARGET)).is(Fluids.LAVA)) {
            context.fail("Reverted slab's fluid state should be lava");
        }

        context.succeed();
    }

    @GameTest
    public void breakingTheHostBreaksTheWholeWindowedBlock(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        ServerPlayer player = facingPlayerAt(context, hostOnlyPointOf(context));

        EventResult result = WindowLogHelper.tryPartialBreak(context.getLevel(), context.absolutePos(TARGET), targetState(context), player);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Expected aiming at the host slab to pass through to vanilla breaking, got " + result);
        }
        if (!(targetState(context).getBlock() instanceof WindowedBlock)) {
            context.fail("Target should still be the WindowedBlock (vanilla handles the actual removal), got " + targetState(context));
        }

        context.succeed();
    }

    // --- Solidity / mining stats ---

    @GameTest
    public void windowedBlockBlocksMotionSoFlowingFluidsCannotDestroyIt(GameTestHelper context) {
        if (!WindowLogBlocks.WINDOWED_BLOCK.get().defaultBlockState().blocksMotion()) {
            context.fail(
                "WindowedBlock should report blocksMotion()=true (via forceSolidOn(), since dynamicShape() "
                    + "otherwise disables the shape cache that flag is normally derived from) so flowing "
                    + "lava/water treats it like a real stair/slab/pane instead of a destructible plant/flower"
            );
        }

        context.succeed();
    }

    @GameTest
    public void miningSpeedMatchesTheTargetedSubBlock(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        BlockPos pos = context.absolutePos(TARGET);
        BlockState windowedState = targetState(context);

        ServerPlayer windowPlayer = facingPlayerAt(context, paneOnlyPointOf(context));
        float windowProgress = windowedState.getDestroyProgress(windowPlayer, context.getLevel(), pos);
        float expectedWindowProgress = be.getWindowState().getDestroyProgress(windowPlayer, context.getLevel(), pos);
        if (Math.abs(windowProgress - expectedWindowProgress) > 1.0e-5) {
            context.fail("Expected mining the glass portion to use glass's own destroy progress (" + expectedWindowProgress + "), got " + windowProgress);
        }

        ServerPlayer hostPlayer = facingPlayerAt(context, hostOnlyPointOf(context));
        float hostProgress = windowedState.getDestroyProgress(hostPlayer, context.getLevel(), pos);
        float expectedHostProgress = be.getHostState().getDestroyProgress(hostPlayer, context.getLevel(), pos);
        if (Math.abs(hostProgress - expectedHostProgress) > 1.0e-5) {
            context.fail("Expected mining the host portion to use its own destroy progress (" + expectedHostProgress + "), got " + hostProgress);
        }

        if (windowProgress == hostProgress) {
            context.fail("Expected window (glass) and host (slab) destroy progress to differ, both were " + windowProgress);
        }

        context.succeed();
    }

    @GameTest
    public void preferredToolForTheHostFollowsTheHostBlockType(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.OAK_STAIRS.defaultBlockState());
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        BlockPos pos = context.absolutePos(TARGET);
        BlockState windowedState = targetState(context);

        ServerPlayer withAxe = facingPlayerAt(context, hostOnlyPointOf(context));
        withAxe.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WOODEN_AXE));
        float axeProgress = windowedState.getDestroyProgress(withAxe, context.getLevel(), pos);

        ServerPlayer withPickaxe = facingPlayerAt(context, hostOnlyPointOf(context));
        withPickaxe.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WOODEN_PICKAXE));
        float pickaxeProgress = windowedState.getDestroyProgress(withPickaxe, context.getLevel(), pos);

        if (axeProgress <= pickaxeProgress) {
            context.fail("Expected an axe to mine an oak-stairs host faster than a pickaxe, got axe=" + axeProgress + " pickaxe=" + pickaxeProgress);
        }

        be.setHostState(Blocks.STONE_BRICK_STAIRS.defaultBlockState());
        be.setChanged();
        windowedState = targetState(context);

        ServerPlayer withAxe2 = facingPlayerAt(context, hostOnlyPointOf(context));
        withAxe2.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WOODEN_AXE));
        float axeProgress2 = windowedState.getDestroyProgress(withAxe2, context.getLevel(), pos);

        ServerPlayer withPickaxe2 = facingPlayerAt(context, hostOnlyPointOf(context));
        withPickaxe2.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WOODEN_PICKAXE));
        float pickaxeProgress2 = windowedState.getDestroyProgress(withPickaxe2, context.getLevel(), pos);

        if (pickaxeProgress2 <= axeProgress2) {
            context.fail("Expected a pickaxe to mine a stone-brick-stairs host faster than an axe, got pickaxe=" + pickaxeProgress2 + " axe=" + axeProgress2);
        }

        context.succeed();
    }

    // --- Pick block (getCloneItemStack) ---

    @GameTest
    public void pickBlockAimedAtTheWindowReturnsTheWindowItem(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.STAINED_GLASS_PANE.yellow().defaultBlockState());
        be.setChanged();

        facingPlayerInLevelAt(context, paneOnlyPointOf(context));

        BlockPos pos = context.absolutePos(TARGET);
        ItemStack picked = targetState(context).getCloneItemStack(context.getLevel(), pos, false);

        if (!picked.is(Items.STAINED_GLASS_PANE.yellow())) {
            context.fail("Expected pick-block on the pane to return a yellow stained glass pane, got " + picked);
        }

        context.succeed();
    }

    @GameTest
    public void pickBlockAimedAtTheHostReturnsTheHostItem(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_BRICK_STAIRS.defaultBlockState());
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        facingPlayerInLevelAt(context, hostOnlyPointOf(context));

        BlockPos pos = context.absolutePos(TARGET);
        ItemStack picked = targetState(context).getCloneItemStack(context.getLevel(), pos, false);

        if (!picked.is(Items.STONE_BRICK_STAIRS)) {
            context.fail("Expected pick-block on the host to return stone brick stairs, got " + picked);
        }

        context.succeed();
    }

    // --- Connecting real panes/bars to a window-logged neighbor (LATT$IronBarsBlockMixin) ---

    @GameTest
    public void realPaneConnectsWhenPlacedNextToAWindowLoggedNeighbor(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState().setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true));
        be.setChanged();

        context.placeBlock(TARGET.east(), Blocks.GLASS_PANE, Direction.UP);

        BlockState neighborState = context.getLevel().getBlockState(context.absolutePos(TARGET.east()));
        if (!neighborState.getValue(CrossCollisionBlock.WEST)) {
            context.fail("Expected a freshly-placed pane to connect its WEST arm toward the window-logged neighbor, got " + neighborState);
        }
        if (neighborState.getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Connecting to a window-logged neighbor should not affect the fresh pane's own lava-logged state, got " + neighborState);
        }

        context.succeed();
    }

    /**
     * Regression coverage for {@code LATT$StainedGlassPaneBlockMixin}: {@code StainedGlassPaneBlock}'s
     * own constructor re-calls {@code registerDefaultState} (to set its NORTH/EAST/SOUTH/WEST/
     * WATERLOGGED defaults) from {@code stateDefinition.any()} rather than {@code defaultBlockState()},
     * which silently dropped {@code LATT$IronBarsBlockMixin}'s {@code LAVALOGGED = false} fix-up back to
     * {@code any()}'s implicit (and, for a fresh {@code BooleanProperty}, {@code true}) default - so
     * every freshly-placed stained pane, of any color, came back already lava-logged even with no lava
     * anywhere in the world. Plain (colorless) {@code glass_pane}/{@code iron_bars} never hit this, since
     * a bare {@code IronBarsBlock} only calls {@code registerDefaultState} once.
     */
    @GameTest
    public void freshStainedGlassPaneIsNotLavaLoggedByDefault(GameTestHelper context) {
        if (Blocks.STAINED_GLASS_PANE.yellow().defaultBlockState().getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Expected a yellow stained glass pane's own default state to not be lava-logged");
        }

        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        be.setWindowState(Blocks.STAINED_GLASS_PANE.yellow().defaultBlockState().setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true));
        be.setChanged();

        context.placeBlock(TARGET.east(), Blocks.STAINED_GLASS_PANE.yellow(), Direction.UP);

        BlockState neighborState = context.getLevel().getBlockState(context.absolutePos(TARGET.east()));
        if (neighborState.getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Expected a freshly-placed yellow pane next to a window-logged neighbor to not be lava-logged, got " + neighborState);
        }

        context.succeed();
    }

    @GameTest
    public void windowLoggingConnectsAnAlreadyPlacedNeighborPane(GameTestHelper context) {
        context.setBlock(TARGET.west(), Blocks.GLASS_PANE);
        context.setBlock(TARGET, Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));
        WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        BlockState neighborState = context.getLevel().getBlockState(context.absolutePos(TARGET.west()));
        if (!neighborState.getValue(CrossCollisionBlock.EAST)) {
            context.fail("Expected the pre-existing neighbor pane to connect its EAST arm toward the newly window-logged block, got " + neighborState);
        }

        context.succeed();
    }

    // --- Flammability follows the host block (LATT$FireBlockMixin) ---

    /**
     * {@code FireBlock#getIgniteOdds(LevelReader, BlockPos)} is private - it's the exact method
     * {@code LATT$FireBlockMixin} redirects, and calling it directly (via reflection) checks that
     * mechanism deterministically rather than waiting on vanilla's randomized per-tick fire spread.
     */
    private static int igniteOddsAt(GameTestHelper context, BlockPos pos) {
        try {
            Method method = FireBlock.class.getDeclaredMethod("getIgniteOdds", LevelReader.class, BlockPos.class);
            method.setAccessible(true);
            return (int) method.invoke(Blocks.FIRE, context.getLevel(), pos);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to call FireBlock#getIgniteOdds via reflection", e);
        }
    }

    @GameTest
    public void windowLoggedOakStairsAreFlammableLikeOak(GameTestHelper context) {
        context.setBlock(TARGET, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.OAK_STAIRS.defaultBlockState());
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState());
        be.setChanged();

        int oddsNextToOak = igniteOddsAt(context, context.absolutePos(TARGET.above()));
        if (oddsNextToOak <= 0) {
            context.fail("Expected fire to see an oak-stairs-hosted windowed block as flammable, ignite odds were " + oddsNextToOak);
        }

        be.setHostState(Blocks.STONE_BRICK_STAIRS.defaultBlockState());
        be.setChanged();

        int oddsNextToStone = igniteOddsAt(context, context.absolutePos(TARGET.above()));
        if (oddsNextToStone != 0) {
            context.fail("Expected fire to see a stone-brick-stairs-hosted windowed block as non-flammable, ignite odds were " + oddsNextToStone);
        }

        context.succeed();
    }

    // --- Sneaking bypasses window-logging (place the pane normally instead) ---

    @GameTest
    public void shiftKeyDownBypassesWindowLoggingOfAStoneSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));
        player.setShiftKeyDown(true);

        EventResult result = WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Expected sneaking to bypass window-logging and pass through to vanilla placement, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowedBlock) {
            context.fail("Sneaking while placing a pane on a windowable slab should not window-log it");
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1) {
            context.fail("Sneaking should leave the pane un-consumed, since window-logging never ran");
        }

        context.succeed();
    }

    @GameTest
    public void shiftKeyDownBypassesWindowLoggingWithIronBars(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST));

        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_BARS));
        player.setShiftKeyDown(true);

        EventResult result = WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.SOUTH);

        if (result.interruptsFurtherEvaluation()) {
            context.fail("Expected sneaking to bypass window-logging with iron bars too, got " + result);
        }
        if (targetState(context).getBlock() instanceof WindowedBlock) {
            context.fail("Sneaking while placing iron bars on a windowable stair should not window-log it");
        }

        context.succeed();
    }

    // --- Window-logged slab pane orientation follows the placing player's facing (perpendicular) ---

    @GameTest
    public void windowLoggingASlabWithPlayerFacingEastWestOrientsThePaneNorthSouth(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        BlockPos eastOfTarget = TARGET.offset(3, 0, 0);
        Player player = GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, eastOfTarget, TARGET);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_PANE));

        WindowLogHelper.tryWindowLog(player, InteractionHand.MAIN_HAND, context.absolutePos(TARGET), Direction.WEST);

        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        if (!be.getWindowState().getValue(CrossCollisionBlock.NORTH) || !be.getWindowState().getValue(CrossCollisionBlock.SOUTH)) {
            context.fail("Expected the pane to orient north-south when the placing player faced east/west, got " + be.getWindowState());
        }
        if (be.getWindowState().getValue(CrossCollisionBlock.EAST) || be.getWindowState().getValue(CrossCollisionBlock.WEST)) {
            context.fail("Window state should not also be connected east-west, got " + be.getWindowState());
        }

        context.succeed();
    }

    // --- A regular stair next to a window-logged stair still forms a normal corner ---

    /**
     * {@code StairBlock#getStairsShape} is private - calling it via reflection lets this test check the
     * exact corner-shape outcome ({@code LATT$StairBlockMixin}'s target) deterministically, without
     * depending on {@code GameTestHelper#placeBlock}'s own placement-context quirks for stairs.
     */
    private static StairsShape stairsShapeAt(GameTestHelper context, BlockState state, BlockPos pos) {
        try {
            Method method = StairBlock.class.getDeclaredMethod("getStairsShape", BlockState.class, BlockGetter.class, BlockPos.class);
            method.setAccessible(true);
            return (StairsShape) method.invoke(null, state, context.getLevel(), context.absolutePos(pos));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to call StairBlock#getStairsShape via reflection", e);
        }
    }

    @GameTest
    public void regularStairFormsTheSameCornerNextToAWindowLoggedHostAsNextToAPlainOne(GameTestHelper context) {
        // The new stair's FACING (EAST, axis X) must differ in axis from the host's FACING (NORTH, axis
        // Z) and the host must sit along the new stair's own FACING axis (west of it, i.e. "in front" of
        // an east-facing stair) for getStairsShape's corner branch to trigger at all - see StairBlock's
        // decompiled source for the exact geometry this mirrors.
        BlockState newStairState = Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);

        BlockPos windowedHostPos = TARGET;
        BlockPos windowedNewPos = windowedHostPos.east();
        context.setBlock(windowedHostPos, WindowLogBlocks.WINDOWED_BLOCK.get());
        WindowedBlockEntity be = targetWindowedBlockEntity(context);
        be.setHostState(Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH));
        be.setWindowState(Blocks.GLASS_PANE.defaultBlockState().setValue(CrossCollisionBlock.NORTH, true).setValue(CrossCollisionBlock.SOUTH, true));
        be.setChanged();
        context.setBlock(windowedNewPos, newStairState);
        StairsShape windowedNeighborShape = stairsShapeAt(context, newStairState, windowedNewPos);

        BlockPos plainHostPos = TARGET.offset(0, 0, 4);
        BlockPos plainNewPos = plainHostPos.east();
        context.setBlock(plainHostPos, Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH));
        context.setBlock(plainNewPos, newStairState);
        StairsShape plainNeighborShape = stairsShapeAt(context, newStairState, plainNewPos);

        if (plainNeighborShape == StairsShape.STRAIGHT) {
            context.fail("Test setup error: the plain-neighbor control should form a corner, got STRAIGHT");
        }
        if (windowedNeighborShape != plainNeighborShape) {
            context.fail(
                "Expected a stair next to a window-logged stair host to compute the same corner shape ("
                    + plainNeighborShape + ") as next to a plain matching stair, got " + windowedNeighborShape
            );
        }

        context.succeed();
    }
}
