package com.chimericdream.logallthethings.fabric.test;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import com.chimericdream.lib.testkit.gametest.GameTestPlayers;
import com.chimericdream.logallthethings.lavalog.LavaLogProperties;

/**
 * Coverage for lava-logging (see {@code com.chimericdream.logallthethings.lavalog} and the per-block
 * mixins in {@code com.chimericdream.logallthethings.mixin}).
 *
 * <p>{@link GameTestHelper#useBlock} is deliberately not used here: it only drives
 * {@code BlockState#useItemOn} / {@code ItemStack#useOn}, neither of which {@code BucketItem}
 * overrides — buckets do their own reach-limited raycast from inside {@code Item#use}, which is the
 * "general use item" dispatch a real client falls back to. {@link GameTestPlayers#makeFacingPlayer}
 * and {@link GameTestPlayers#useItem} simulate that directly: position and orient a mock player at the
 * target block, call {@code use()}, and apply whatever the result says the held item became (the real
 * interaction manager's job on an actual server) since nothing else in a GameTest does that for us.
 *
 * <p>{@code lavaBucketLavaLogsANonFlammableSlab} and {@code lavaBucketLavaLogsIronBars} exercise that
 * full bucket path end-to-end — the regression class an earlier version of this feature actually hit
 * (a mismatched field-type on the {@code Fluids.WATER} redirect in {@code LATT$BucketItemMixin} /
 * {@code LATT$BucketItemFabricMixin} meant the lava bucket's fill request never reached the block at
 * all). The various {@code *FluidStateIsLava} smoke tests below cover the other regression class found
 * this session — {@code FenceBlock}/{@code IronBarsBlock} inheriting {@code getFluidState} from the
 * abstract {@code CrossCollisionBlock} rather than declaring their own, which made a plain
 * {@code @Inject} silently fail to find its target — by forcing {@code lavalogged=true} directly on one
 * representative block per mixin class and checking {@code getFluidState}, which is enough to prove
 * that class's {@code createBlockStateDefinition}/constructor/{@code getFluidState} wiring is intact
 * without re-running the full bucket flow for every block family.
 */
@SuppressWarnings("unused")
public class LavaLoggingGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);
    private static final BlockPos PLAYER_POS = TARGET.offset(0, 0, 3);

    private static Player placePlayerFacingTarget(GameTestHelper context) {
        return GameTestPlayers.makeFacingPlayer(context, GameType.SURVIVAL, PLAYER_POS, TARGET);
    }

    private static BlockState targetState(GameTestHelper context) {
        return context.getLevel().getBlockState(context.absolutePos(TARGET));
    }

    /**
     * Calls {@code BucketItem#emptyContents} directly at the target position with no
     * {@code BlockHitResult}, instead of simulating the full raycast-driven {@code use()} flow. This is
     * deliberate for the refusal tests below: when the target legitimately refuses (flammable, already
     * logged with the other fluid), a raycast-driven {@code use()} call doesn't fail outright — vanilla's
     * own bucket fallback recurses to the adjacent position (here, open test-region air) and places a
     * loose fluid block there instead, which makes {@code result.consumesAction()} true for reasons
     * unrelated to the block actually under test. Calling {@code emptyContents} directly with a
     * {@code null} hit result short-circuits that fallback ({@code hitResult != null && ...}), giving an
     * unambiguous {@code false} when the target refuses, with no player positioning required either.
     * Bucket-specific, so it stays local rather than joining {@link GameTestPlayers}.
     */
    private static boolean emptyBucketDirectlyAtTarget(GameTestHelper context, Item bucketItem) {
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        return ((BucketItem) bucketItem).emptyContents(player, context.getLevel(), context.absolutePos(TARGET), null);
    }

    // --- Full bucket-interaction flow (place -> lava-log -> pick back up) ---

    @GameTest
    public void lavaBucketLavaLogsANonFlammableSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB);

        Player player = placePlayerFacingTarget(context);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.LAVA_BUCKET));

        InteractionResult result = GameTestPlayers.useItem(context.getLevel(), player, InteractionHand.MAIN_HAND);

        if (!result.consumesAction()) {
            context.fail("Expected the lava bucket to be used on a stone slab, got " + result);
        }

        if (!targetState(context).getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Stone slab should be lava-logged after using a lava bucket on it");
        }

        if (!context.getLevel().getFluidState(context.absolutePos(TARGET)).is(Fluids.LAVA)) {
            context.fail("Lava-logged slab's fluid state should be lava");
        }

        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.BUCKET)) {
            context.fail("Bucket should have emptied to a plain bucket, got " + player.getItemInHand(InteractionHand.MAIN_HAND));
        }

        context.succeed();
    }

    @GameTest
    public void emptyBucketPicksLavaBackUpFromALavaLoggedSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB.defaultBlockState().setValue(LavaLogProperties.LAVALOGGED, true));

        Player player = placePlayerFacingTarget(context);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));

        InteractionResult result = GameTestPlayers.useItem(context.getLevel(), player, InteractionHand.MAIN_HAND);

        if (!result.consumesAction()) {
            context.fail("Expected the empty bucket to pick lava up from the slab, got " + result);
        }

        if (targetState(context).getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Slab should no longer be lava-logged after being picked up");
        }

        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.LAVA_BUCKET)) {
            context.fail("Bucket should have filled with lava, got " + player.getItemInHand(InteractionHand.MAIN_HAND));
        }

        context.succeed();
    }

    @GameTest
    public void lavaBucketLavaLogsIronBars(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.IRON_BARS);

        Player player = placePlayerFacingTarget(context);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.LAVA_BUCKET));

        InteractionResult result = GameTestPlayers.useItem(context.getLevel(), player, InteractionHand.MAIN_HAND);

        if (!result.consumesAction()) {
            context.fail("Expected the lava bucket to be used on iron bars, got " + result);
        }

        if (!targetState(context).getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Iron bars should be lava-logged after using a lava bucket on them");
        }

        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.BUCKET)) {
            context.fail("Bucket should have emptied to a plain bucket, got " + player.getItemInHand(InteractionHand.MAIN_HAND));
        }

        context.succeed();
    }

    // --- Flammability gate ---

    @GameTest
    public void lavaBucketIsRefusedOnAFlammableSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.OAK_SLAB);

        boolean handled = emptyBucketDirectlyAtTarget(context, Items.LAVA_BUCKET);

        if (handled) {
            context.fail("Lava bucket should be refused on a flammable oak slab");
        }

        if (targetState(context).getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Oak slab should not be lava-logged");
        }

        context.succeed();
    }

    // --- Mutual exclusion with water-logging ---

    @GameTest
    public void lavaBucketIsRefusedOnAnAlreadyWaterloggedSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true));

        boolean handled = emptyBucketDirectlyAtTarget(context, Items.LAVA_BUCKET);

        if (handled) {
            context.fail("Lava bucket should be refused on an already-waterlogged slab");
        }

        if (targetState(context).getValue(LavaLogProperties.LAVALOGGED)) {
            context.fail("Waterlogged slab should not also become lava-logged");
        }

        context.succeed();
    }

    @GameTest
    public void waterBucketIsRefusedOnAnAlreadyLavaLoggedSlab(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.STONE_SLAB.defaultBlockState().setValue(LavaLogProperties.LAVALOGGED, true));

        boolean handled = emptyBucketDirectlyAtTarget(context, Items.WATER_BUCKET);

        if (handled) {
            context.fail("Water bucket should be refused on an already-lava-logged slab");
        }

        if (targetState(context).getValue(BlockStateProperties.WATERLOGGED)) {
            context.fail("Lava-logged slab should not also become waterlogged");
        }

        context.succeed();
    }

    // --- Per-block-class fluid-state smoke tests (createBlockStateDefinition + constructor default +
    // getFluidState wiring, one representative block per mixin class) ---

    private static void assertForcedLavaLoggedStateIsLava(GameTestHelper context, Block block) {
        context.setBlock(TARGET, block.defaultBlockState().setValue(LavaLogProperties.LAVALOGGED, true));

        if (!context.getLevel().getFluidState(context.absolutePos(TARGET)).is(Fluids.LAVA)) {
            context.fail(block + " with lavalogged=true should report a lava fluid state");
        }

        context.succeed();
    }

    @GameTest
    public void lavaLoggedStairsFluidStateIsLava(GameTestHelper context) {
        assertForcedLavaLoggedStateIsLava(context, Blocks.STONE_STAIRS);
    }

    @GameTest
    public void lavaLoggedWallFluidStateIsLava(GameTestHelper context) {
        assertForcedLavaLoggedStateIsLava(context, Blocks.COBBLESTONE_WALL);
    }

    @GameTest
    public void lavaLoggedFenceFluidStateIsLava(GameTestHelper context) {
        assertForcedLavaLoggedStateIsLava(context, Blocks.NETHER_BRICK_FENCE);
    }

    @GameTest
    public void lavaLoggedTrapdoorFluidStateIsLava(GameTestHelper context) {
        assertForcedLavaLoggedStateIsLava(context, Blocks.IRON_TRAPDOOR);
    }

    @GameTest
    public void lavaLoggedLadderFluidStateIsLava(GameTestHelper context) {
        assertForcedLavaLoggedStateIsLava(context, Blocks.LADDER);
    }

    @GameTest
    public void lavaLoggedChainFluidStateIsLava(GameTestHelper context) {
        assertForcedLavaLoggedStateIsLava(context, Blocks.IRON_CHAIN);
    }
}
