package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.CandleLitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.block.PumpkinContents;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Coverage for {@link PumpkinContents}: which message each pumpkin state maps to, and that the sneak +
 * empty-hand inspect gesture works on every kind of decorated pumpkin without changing it. The overlay
 * message itself goes to the player's connection, which a mock player doesn't expose, so what actually
 * appears above the hotbar is covered by the manual steps in {@code TEST_PLAN.md}.
 */
@SuppressWarnings("unused")
public class PumpkinContentsGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);

    private static BlockState candlePumpkin(int candles, boolean lit) {
        return ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get().defaultBlockState()
            .setValue(CandleLitDecoratedPumpkinBlock.CANDLES, candles)
            .setValue(CandleLitDecoratedPumpkinBlock.LIT, lit);
    }

    private static TranslatableContents contents(Component component) {
        return (TranslatableContents) component.getContents();
    }

    private static void assertMessage(GameTestHelper context, BlockState state, String key, Object... args) {
        Component message = PumpkinContents.describe(state);
        context.assertTrue(message != null, Component.literal("expected a message for " + state));
        TranslatableContents translatable = contents(message);
        context.assertValueEqual(translatable.getKey(), key, "translation key for " + state);
        context.assertValueEqual(List.of(translatable.getArgs()), List.of(args), "translation args for " + state);
    }

    @GameTest
    public void hollowPumpkinSaysEmpty(GameTestHelper context) {
        assertMessage(context, ModBlocks.DECORATED_PUMPKIN.get().defaultBlockState(), PumpkinContents.EMPTY_KEY);
        context.succeed();
    }

    @GameTest
    public void torchPumpkinsUseTheTorchName(GameTestHelper context) {
        assertMessage(context, ModBlocks.LIT_DECORATED_PUMPKIN.get().defaultBlockState(), Items.TORCH.getDescriptionId());
        assertMessage(context, ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get().defaultBlockState(), Items.SOUL_TORCH.getDescriptionId());
        assertMessage(context, ModBlocks.LIT_DECORATED_PUMPKIN_GREEN.get().defaultBlockState(), Items.COPPER_TORCH.getDescriptionId());
        assertMessage(context, ModBlocks.LIT_DECORATED_PUMPKIN_RED.get().defaultBlockState(), Items.REDSTONE_TORCH.getDescriptionId());
        context.succeed();
    }

    @GameTest
    public void candlePumpkinsSayCountAndLitState(GameTestHelper context) {
        assertMessage(context, candlePumpkin(1, true), PumpkinContents.CANDLE_LIT_KEY, 1);
        assertMessage(context, candlePumpkin(1, false), PumpkinContents.CANDLE_SNUFFED_KEY, 1);

        for (int candles = 2; candles <= 4; candles++) {
            assertMessage(context, candlePumpkin(candles, true), PumpkinContents.CANDLES_LIT_KEY, candles);
            assertMessage(context, candlePumpkin(candles, false), PumpkinContents.CANDLES_SNUFFED_KEY, candles);
        }

        context.succeed();
    }

    @GameTest
    public void otherBlocksHaveNoMessage(GameTestHelper context) {
        context.assertTrue(PumpkinContents.describe(Blocks.CARVED_PUMPKIN.defaultBlockState()) == null, Component.literal("carved pumpkin should have no message"));
        context.assertTrue(PumpkinContents.describe(Blocks.JACK_O_LANTERN.defaultBlockState()) == null, Component.literal("jack o'lantern should have no message"));
        context.succeed();
    }

    private static Player sneakingEmptyHand(GameTestHelper context) {
        PumpkinMessageRecorder.install();
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setShiftKeyDown(true);

        return player;
    }

    @GameTest
    public void inspectingHollowPumpkinChangesNothing(GameTestHelper context) {
        context.setBlock(TARGET, ModBlocks.DECORATED_PUMPKIN.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, Direction.SOUTH));

        Player player = sneakingEmptyHand(context);
        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.DECORATED_PUMPKIN.get(), TARGET);
        context.assertValueEqual(PumpkinMessageRecorder.keys(player), List.of(PumpkinContents.EMPTY_KEY), "messages sent");
        context.succeed();
    }

    @GameTest
    public void inspectingTorchPumpkinChangesNothing(GameTestHelper context) {
        context.setBlock(TARGET, ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, Direction.SOUTH));

        Player player = sneakingEmptyHand(context);
        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get(), TARGET);
        context.assertValueEqual(PumpkinMessageRecorder.keys(player), List.of(Items.SOUL_TORCH.getDescriptionId()), "messages sent");
        context.succeed();
    }

    @GameTest
    public void inspectingCandlePumpkinChangesNothing(GameTestHelper context) {
        context.setBlock(TARGET, candlePumpkin(3, true));

        Player player = sneakingEmptyHand(context);
        context.useBlock(TARGET, player);

        context.assertBlockState(
            TARGET,
            state -> state.getValue(CandleLitDecoratedPumpkinBlock.LIT) && state.getValue(CandleLitDecoratedPumpkinBlock.CANDLES) == 3,
            state -> Component.literal("inspecting should not change the pumpkin, was " + state)
        );
        context.assertValueEqual(PumpkinMessageRecorder.keys(player), List.of(PumpkinContents.CANDLES_LIT_KEY), "messages sent");
        context.succeed();
    }
}
