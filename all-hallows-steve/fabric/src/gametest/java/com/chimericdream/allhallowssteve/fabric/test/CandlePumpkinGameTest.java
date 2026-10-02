package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.CandleLitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.Collections;
import java.util.List;

/**
 * Player-side coverage for candle-lit decorated pumpkins: adding candles (shift-click, via
 * {@code AHS$CandleBlockItemMixin}), snuffing, relighting, shears, light levels, and breaking.
 * {@link GameTestHelper#useBlock} runs the block's {@code useItemOn}/{@code useWithoutItem} first and
 * falls through to {@code ItemStack#useOn}, which is where the candle mixin hooks in.
 */
@SuppressWarnings("unused")
public class CandlePumpkinGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);

    private static final int COLOR = 0x3366CC;
    private static final PumpkinStencilsComponent STENCILS = PumpkinStencilsComponent.EMPTY.with(Direction.SOUTH, "creeper");

    private static Item red() {
        return Items.DYED_CANDLE.red();
    }

    private static Player playerHolding(GameTestHelper context, ItemStack stack, boolean sneaking) {
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.setShiftKeyDown(sneaking);

        return player;
    }

    private static Player playerHolding(GameTestHelper context, Item item, int count, boolean sneaking) {
        return playerHolding(context, new ItemStack(item, count), sneaking);
    }

    private static void placeHollowPumpkin(GameTestHelper context) {
        context.setBlock(TARGET, ModBlocks.DECORATED_PUMPKIN.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, Direction.SOUTH));
        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);
        pumpkin.setColor(COLOR);
        pumpkin.setStencils(STENCILS);
    }

    private static void placeCandlePumpkin(GameTestHelper context, List<Item> candles, boolean lit) {
        context.setBlock(TARGET, ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get().defaultBlockState()
            .setValue(CandleLitDecoratedPumpkinBlock.FACING, Direction.SOUTH)
            .setValue(CandleLitDecoratedPumpkinBlock.CANDLES, candles.size())
            .setValue(CandleLitDecoratedPumpkinBlock.LIT, lit));
        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);
        pumpkin.setColor(COLOR);
        pumpkin.setStencils(STENCILS);
        pumpkin.setCandles(candles);
    }

    private static void assertDecorationKept(GameTestHelper context) {
        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);
        context.assertValueEqual(pumpkin.getColor(), COLOR, "pumpkin color");
        context.assertValueEqual(pumpkin.getStencils(), STENCILS, "pumpkin stencils");
        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.FACING), Direction.SOUTH, "pumpkin facing");
    }

    @GameTest
    public void sneakingCandleLightsHollowPumpkin(GameTestHelper context) {
        placeHollowPumpkin(context);
        Player player = playerHolding(context, red(), 2, true);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get(), TARGET);
        BlockState state = context.getBlockState(TARGET);
        context.assertValueEqual(state.getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 1, "candle count");
        context.assertValueEqual(state.getValue(CandleLitDecoratedPumpkinBlock.LIT), true, "lit");
        context.assertValueEqual(context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).getCandles(), List.of(red()), "candles held");
        assertDecorationKept(context);
        context.assertValueEqual(player.getMainHandItem().getCount(), 1, "candles left in hand");
        context.succeed();
    }

    @GameTest
    public void plainCandleClickDoesNotAddToPumpkin(GameTestHelper context) {
        placeHollowPumpkin(context);
        Player player = playerHolding(context, red(), 2, false);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.DECORATED_PUMPKIN.get(), TARGET);
        context.succeed();
    }

    @GameTest
    public void sneakingCandleAddsToExistingCandles(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red()), true);
        Player player = playerHolding(context, Items.CANDLE, 2, true);

        context.useBlock(TARGET, player);

        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 2, "candle count");
        context.assertValueEqual(context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).getCandles(), List.of(red(), Items.CANDLE), "candles held, in order");
        assertDecorationKept(context);
        context.assertValueEqual(player.getMainHandItem().getCount(), 1, "candles left in hand");
        context.succeed();
    }

    @GameTest
    public void fifthCandleIsRejected(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red(), red(), red(), red()), true);
        Player player = playerHolding(context, Items.CANDLE, 2, true);

        context.useBlock(TARGET, player);

        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 4, "candle count");
        context.assertValueEqual(context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).getCandles().size(), 4, "candles held");
        context.succeed();
    }

    @GameTest
    public void sneakingCandleDoesNotAddToTorchLitPumpkin(GameTestHelper context) {
        context.setBlock(TARGET, ModBlocks.LIT_DECORATED_PUMPKIN.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, Direction.SOUTH));
        Player player = playerHolding(context, red(), 2, true);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.LIT_DECORATED_PUMPKIN.get(), TARGET);
        context.succeed();
    }

    @GameTest
    public void sneakingTorchDoesNotLightCandlePumpkin(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red()), true);
        Player player = playerHolding(context, Items.TORCH, 2, true);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get(), TARGET);
        context.succeed();
    }

    @GameTest
    public void lightLevelsMatchVanillaCandles(GameTestHelper context) {
        for (int candles = 1; candles <= 4; candles++) {
            placeCandlePumpkin(context, Collections.nCopies(candles, red()), true);
            context.assertValueEqual(context.getBlockState(TARGET).getLightEmission(), candles * 3, candles + " lit candle(s) light level");

            placeCandlePumpkin(context, Collections.nCopies(candles, red()), false);
            context.assertValueEqual(context.getBlockState(TARGET).getLightEmission(), 0, candles + " snuffed candle(s) light level");
        }

        context.succeed();
    }

    @GameTest
    public void overlaySuffixFollowsCountAndLitState(GameTestHelper context) {
        for (int candles = 1; candles <= 4; candles++) {
            placeCandlePumpkin(context, Collections.nCopies(candles, red()), true);
            context.assertValueEqual(CandleLitDecoratedPumpkinBlock.overlaySuffix(context.getBlockState(TARGET)), "_candlelit_" + candles, "lit overlay suffix");

            placeCandlePumpkin(context, Collections.nCopies(candles, red()), false);
            context.assertValueEqual(CandleLitDecoratedPumpkinBlock.overlaySuffix(context.getBlockState(TARGET)), "", "snuffed overlay suffix");
        }

        context.succeed();
    }

    @GameTest
    public void emptyHandSnuffsAndKeepsCandles(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red(), Items.CANDLE), true);
        Player player = playerHolding(context, ItemStack.EMPTY, false);

        context.useBlock(TARGET, player);

        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.LIT), false, "lit");
        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 2, "candle count");
        context.assertValueEqual(context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).getCandles(), List.of(red(), Items.CANDLE), "candles held");
        assertDecorationKept(context);
        context.succeed();
    }

    @GameTest
    public void sneakingEmptyHandDoesNotSnuff(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red()), true);
        Player player = playerHolding(context, ItemStack.EMPTY, true);

        context.useBlock(TARGET, player);

        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.LIT), true, "lit");
        context.succeed();
    }

    @GameTest
    public void flintAndSteelRelightsSnuffedPumpkin(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red(), red()), false);
        Player player = playerHolding(context, Items.FLINT_AND_STEEL, 1, false);

        context.useBlock(TARGET, player);

        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.LIT), true, "lit");
        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 2, "candle count");
        context.assertValueEqual(player.getMainHandItem().getDamageValue(), 1, "flint and steel damage");
        assertDecorationKept(context);
        context.succeed();
    }

    @GameTest
    public void flintAndSteelLeavesLitPumpkinAlone(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red()), true);
        Player player = playerHolding(context, Items.FLINT_AND_STEEL, 1, false);

        context.useBlock(TARGET, player);

        context.assertValueEqual(player.getMainHandItem().getDamageValue(), 0, "flint and steel damage");
        context.succeed();
    }

    @GameTest
    public void candleAddedToSnuffedPumpkinStaysSnuffed(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red()), false);
        Player player = playerHolding(context, Items.CANDLE, 2, true);

        context.useBlock(TARGET, player);

        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 2, "candle count");
        context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.LIT), false, "lit");
        context.succeed();
    }

    @GameTest
    public void shearsRemoveCandlesInTheirColors(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red(), Items.CANDLE), true);
        Player player = playerHolding(context, Items.SHEARS, 1, false);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.DECORATED_PUMPKIN.get(), TARGET);
        context.assertValueEqual(context.getBlockState(TARGET).getValue(DecoratedPumpkinBlock.FACING), Direction.SOUTH, "pumpkin facing");
        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);
        context.assertValueEqual(pumpkin.getColor(), COLOR, "pumpkin color");
        context.assertValueEqual(pumpkin.getStencils(), STENCILS, "pumpkin stencils");
        context.assertValueEqual(pumpkin.getCandles(), List.of(), "candles held");
        context.assertItemEntityPresent(red());
        context.assertItemEntityPresent(Items.CANDLE);
        context.assertValueEqual(player.getMainHandItem().getDamageValue(), 1, "shears damage");
        context.succeed();
    }

    @GameTest
    public void breakingDropsPumpkinAndCandles(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red(), Items.CANDLE), true);

        context.getLevel().destroyBlock(context.absolutePos(TARGET), true);

        context.assertItemEntityPresent(ModBlocks.DECORATED_PUMPKIN.get().asItem());
        context.assertItemEntityPresent(red());
        context.assertItemEntityPresent(Items.CANDLE);
        context.succeed();
    }

    @GameTest
    public void candlesSurviveSavingAndLoading(GameTestHelper context) {
        placeCandlePumpkin(context, List.of(red(), Items.CANDLE, red()), true);
        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);

        CompoundTag tag = pumpkin.saveWithoutMetadata(context.getLevel().registryAccess());
        DecoratedPumpkinBlockEntity copy = new DecoratedPumpkinBlockEntity(context.absolutePos(TARGET), context.getBlockState(TARGET));
        copy.loadWithComponents(TagValueInput.create(
            ProblemReporter.DISCARDING,
            context.getLevel().registryAccess(),
            tag
        ));

        context.assertValueEqual(copy.getCandles(), List.of(red(), Items.CANDLE, red()), "candles after a save/load round trip");
        context.succeed();
    }
}
