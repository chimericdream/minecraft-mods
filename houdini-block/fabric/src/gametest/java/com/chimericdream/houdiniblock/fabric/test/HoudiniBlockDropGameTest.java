package com.chimericdream.houdiniblock.fabric.test;

import com.chimericdream.houdiniblock.blocks.ModBlocks;
import com.chimericdream.houdiniblock.items.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * Server-side coverage for {@code HoudiniBlock}'s two item-spawning paths.
 *
 * <p>Both used to call {@code world.addFreshEntity} (and {@code setBlockAndUpdate}) with no
 * {@code isClientSide} guard, so the client spawned its own ghost copy of the dropped block that
 * popped out of existence as soon as the server synced. The ghost itself only exists on a client, so
 * a server GameTest can't observe it — what these tests pin down is that adding the guards didn't
 * change the server-authoritative outcome: each path still yields exactly one Houdini Block.
 */
@SuppressWarnings("unused")
public class HoudiniBlockDropGameTest {
    private static final BlockPos BLOCK_POS = new BlockPos(2, 2, 2);
    private static final double SEARCH_RANGE = 2.0;

    /**
     * A mock player whose {@code gameMode()} actually reports {@code gameType}.
     *
     * <p>26.1.2's {@code GameTestHelper} has no game-mode-taking server-player factory:
     * {@code makeMockPlayer(GameType)} honours the game mode but returns a plain {@link
     * net.minecraft.world.entity.player.Player} (no {@code gameMode} field to drive
     * {@code destroyBlock}), while {@code makeMockServerPlayerInLevel()} returns a
     * {@link ServerPlayer} whose {@code gameMode()} is hardcoded to CREATIVE and which ignores
     * {@code setGameMode}. Neither gives a survival {@code ServerPlayer}, so build one directly and
     * override the accessor - {@code ServerPlayerGameMode} reads the mode back through it.
     */
    private static ServerPlayer mockPlayer(GameTestHelper context, GameType gameType) {
        ServerLevel level = context.getLevel();

        return new ServerPlayer(
            level.getServer(),
            level,
            new GameProfile(UUID.randomUUID(), "test-mock-player"),
            ClientInformation.createDefault()
        ) {
            @Override
            public GameType gameMode() {
                return gameType;
            }
        };
    }

    /**
     * Right-clicking a Houdini Block with a block in hand swaps the two and hands the Houdini Block
     * back — exactly once.
     */
    @GameTest
    public void replacingYieldsExactlyOneHoudiniBlock(GameTestHelper context) {
        context.setBlock(BLOCK_POS, ModBlocks.HOUDINI_BLOCK.get().defaultBlockState());

        ServerPlayer player = mockPlayer(context, GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));

        context.useBlock(BLOCK_POS, player);

        context.assertBlockPresent(Blocks.STONE, BLOCK_POS);
        context.assertItemEntityCountIs(ModItems.HOUDINI_BLOCK_ITEM.get(), BLOCK_POS, SEARCH_RANGE, 1);
        context.succeed();
    }

    /**
     * Breaking the block by hand hands it back exactly once via {@code playerWillDestroy}.
     */
    @GameTest
    public void breakingYieldsExactlyOneHoudiniBlock(GameTestHelper context) {
        context.setBlock(BLOCK_POS, ModBlocks.HOUDINI_BLOCK.get().defaultBlockState());

        ServerPlayer player = mockPlayer(context, GameType.SURVIVAL);
        player.gameMode.destroyBlock(context.absolutePos(BLOCK_POS));

        context.assertBlockPresent(Blocks.AIR, BLOCK_POS);
        context.assertItemEntityCountIs(ModItems.HOUDINI_BLOCK_ITEM.get(), BLOCK_POS, SEARCH_RANGE, 1);
        context.succeed();
    }

    /**
     * The block hands itself back manually, so it must carry no loot table — otherwise a real player
     * breaking it with a correct tool gets the loot drop <em>on top of</em> the manual spawn (two
     * blocks for one). The mock player used elsewhere can't reproduce that through {@code destroyBlock}
     * (its {@code hasCorrectToolForDrops} is always false, so the vanilla break path never reaches the
     * loot roll), so this asserts on the loot table directly: {@code noLootTable()} makes
     * {@code getDrops} empty, where the inherited stone-derived table used to yield one Houdini Block.
     */
    @GameTest
    public void theBlockHasNoLootTable(GameTestHelper context) {
        BlockPos absolute = context.absolutePos(BLOCK_POS);
        context.setBlock(BLOCK_POS, ModBlocks.HOUDINI_BLOCK.get().defaultBlockState());

        BlockState state = context.getLevel().getBlockState(absolute);
        LootParams.Builder params = new LootParams.Builder(context.getLevel())
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(absolute))
            .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE))
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);

        List<ItemStack> drops = state.getDrops(params);
        if (!drops.isEmpty()) {
            context.fail("The Houdini Block should have no loot table (it drops itself manually), but getDrops returned " + drops);
        }

        context.succeed();
    }

    /**
     * A creative player gets no block back from either path.
     */
    @GameTest
    public void creativeReplacementDropsNothing(GameTestHelper context) {
        context.setBlock(BLOCK_POS, ModBlocks.HOUDINI_BLOCK.get().defaultBlockState());

        ServerPlayer player = mockPlayer(context, GameType.CREATIVE);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));

        context.useBlock(BLOCK_POS, player);

        context.assertBlockPresent(Blocks.STONE, BLOCK_POS);
        context.assertItemEntityCountIs(ModItems.HOUDINI_BLOCK_ITEM.get(), BLOCK_POS, SEARCH_RANGE, 0);
        context.succeed();
    }
}
