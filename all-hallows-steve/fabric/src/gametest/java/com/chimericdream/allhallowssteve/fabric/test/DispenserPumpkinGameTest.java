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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

import java.util.Collections;
import java.util.List;

/**
 * End-to-end coverage for {@code ModDispenserBehaviors} (and, through it, chimeric-lib's
 * {@code DispenserBehaviors} dispatch, which needs a live level and so can't be unit-tested there):
 * a dispenser facing east at {@link #TARGET}, fired by a redstone pulse on its west side.
 */
@SuppressWarnings("unused")
public class DispenserPumpkinGameTest {
    private static final BlockPos DISPENSER = new BlockPos(2, 2, 2);
    private static final BlockPos TARGET = DISPENSER.east();
    private static final BlockPos TRIGGER = DISPENSER.west();

    /** Long enough for the dispenser's 4-tick scheduled activation plus margin. */
    private static final int SETTLE_TICKS = 10;

    private static final int COLOR = 0x3366CC;
    private static final PumpkinStencilsComponent STENCILS = PumpkinStencilsComponent.EMPTY.with(Direction.SOUTH, "creeper");

    private static DispenserBlockEntity placeDispenser(GameTestHelper context, ItemStack contents) {
        context.setBlock(DISPENSER, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.EAST));
        DispenserBlockEntity dispenser = context.getBlockEntity(DISPENSER, DispenserBlockEntity.class);
        dispenser.setItem(0, contents);

        return dispenser;
    }

    private static void placeDecoratedPumpkin(GameTestHelper context, Block block) {
        context.setBlock(TARGET, block.defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, Direction.SOUTH));
        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);
        pumpkin.setColor(COLOR);
        pumpkin.setStencils(STENCILS);
    }

    private static void assertDecorationKept(GameTestHelper context, Block expectedBlock) {
        context.assertBlockPresent(expectedBlock, TARGET);
        context.assertBlockState(
            TARGET,
            state -> state.getValue(DecoratedPumpkinBlock.FACING) == Direction.SOUTH,
            state -> Component.literal("pumpkin facing should be preserved, was " + state.getValue(DecoratedPumpkinBlock.FACING))
        );

        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);
        context.assertValueEqual(pumpkin.getColor(), COLOR, "pumpkin color");
        context.assertValueEqual(pumpkin.getStencils(), STENCILS, "pumpkin stencils");
    }

    private static void torchLightsPumpkin(GameTestHelper context, Item torch, Block expectedLitVariant) {
        placeDecoratedPumpkin(context, ModBlocks.DECORATED_PUMPKIN.get());
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(torch, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertDecorationKept(context, expectedLitVariant);
            context.assertValueEqual(dispenser.getItem(0).getCount(), 1, "torches left in the dispenser");
            context.assertItemEntityNotPresent(torch);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void torchLightsDecoratedPumpkin(GameTestHelper context) {
        torchLightsPumpkin(context, Items.TORCH, ModBlocks.LIT_DECORATED_PUMPKIN.get());
    }

    @GameTest(maxTicks = 40)
    public void soulTorchLightsDecoratedPumpkinBlue(GameTestHelper context) {
        torchLightsPumpkin(context, Items.SOUL_TORCH, ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get());
    }

    @GameTest(maxTicks = 40)
    public void copperTorchLightsDecoratedPumpkinGreen(GameTestHelper context) {
        torchLightsPumpkin(context, Items.COPPER_TORCH, ModBlocks.LIT_DECORATED_PUMPKIN_GREEN.get());
    }

    @GameTest(maxTicks = 40)
    public void redstoneTorchLightsDecoratedPumpkinRed(GameTestHelper context) {
        torchLightsPumpkin(context, Items.REDSTONE_TORCH, ModBlocks.LIT_DECORATED_PUMPKIN_RED.get());
    }

    @GameTest(maxTicks = 40)
    public void torchFailsOnAlreadyLitPumpkin(GameTestHelper context) {
        placeDecoratedPumpkin(context, ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get());
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.TORCH, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertDecorationKept(context, ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get());
            context.assertValueEqual(dispenser.getItem(0).getCount(), 2, "torches left in the dispenser");
            context.assertItemEntityNotPresent(Items.TORCH);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void torchAtNonPumpkinFallsBackToDropping(GameTestHelper context) {
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.TORCH, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            context.assertValueEqual(dispenser.getItem(0).getCount(), 1, "torches left in the dispenser");
            context.assertItemEntityPresent(Items.TORCH);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void shearsUnlightPumpkinAndDropTorch(GameTestHelper context) {
        placeDecoratedPumpkin(context, ModBlocks.LIT_DECORATED_PUMPKIN_GREEN.get());
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.SHEARS));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertDecorationKept(context, ModBlocks.DECORATED_PUMPKIN.get());
            context.assertItemEntityPresent(Items.COPPER_TORCH);
            context.assertValueEqual(dispenser.getItem(0).getDamageValue(), 1, "shears damage");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void shearsLeaveUnlitPumpkinAlone(GameTestHelper context) {
        placeDecoratedPumpkin(context, ModBlocks.DECORATED_PUMPKIN.get());
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.SHEARS));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertDecorationKept(context, ModBlocks.DECORATED_PUMPKIN.get());
            context.assertValueEqual(dispenser.getItem(0).getDamageValue(), 0, "shears damage");
            context.succeed();
        });
    }

    private static void placeVanillaPumpkin(GameTestHelper context, Block block) {
        context.setBlock(TARGET, block.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, Direction.SOUTH));
    }

    private static void assertVanillaPumpkin(GameTestHelper context, Block expected) {
        context.assertBlockPresent(expected, TARGET);
        context.assertBlockState(
            TARGET,
            state -> state.getValue(CarvedPumpkinBlock.FACING) == Direction.SOUTH,
            state -> Component.literal("pumpkin facing should be preserved, was " + state.getValue(CarvedPumpkinBlock.FACING))
        );
    }

    @GameTest(maxTicks = 40)
    public void torchLightsCarvedPumpkinIntoJackOLantern(GameTestHelper context) {
        placeVanillaPumpkin(context, Blocks.CARVED_PUMPKIN);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.TORCH, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertVanillaPumpkin(context, Blocks.JACK_O_LANTERN);
            context.assertValueEqual(dispenser.getItem(0).getCount(), 1, "torches left in the dispenser");
            context.assertItemEntityNotPresent(Items.TORCH);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void soulTorchAtCarvedPumpkinFallsBackToDropping(GameTestHelper context) {
        placeVanillaPumpkin(context, Blocks.CARVED_PUMPKIN);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.SOUL_TORCH, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertVanillaPumpkin(context, Blocks.CARVED_PUMPKIN);
            context.assertValueEqual(dispenser.getItem(0).getCount(), 1, "soul torches left in the dispenser");
            context.assertItemEntityPresent(Items.SOUL_TORCH);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void torchFailsOnJackOLantern(GameTestHelper context) {
        placeVanillaPumpkin(context, Blocks.JACK_O_LANTERN);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.TORCH, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertVanillaPumpkin(context, Blocks.JACK_O_LANTERN);
            context.assertValueEqual(dispenser.getItem(0).getCount(), 2, "torches left in the dispenser");
            context.assertItemEntityNotPresent(Items.TORCH);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void shearsTurnJackOLanternBackIntoCarvedPumpkin(GameTestHelper context) {
        placeVanillaPumpkin(context, Blocks.JACK_O_LANTERN);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.SHEARS));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertVanillaPumpkin(context, Blocks.CARVED_PUMPKIN);
            context.assertItemEntityPresent(Items.TORCH);
            context.assertValueEqual(dispenser.getItem(0).getDamageValue(), 1, "shears damage");
            context.succeed();
        });
    }

    private static void placeCandlePumpkin(GameTestHelper context, int candles, boolean lit) {
        context.setBlock(TARGET, ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get().defaultBlockState()
            .setValue(CandleLitDecoratedPumpkinBlock.FACING, Direction.SOUTH)
            .setValue(CandleLitDecoratedPumpkinBlock.CANDLES, candles)
            .setValue(CandleLitDecoratedPumpkinBlock.LIT, lit));
        DecoratedPumpkinBlockEntity pumpkin = context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class);
        pumpkin.setColor(COLOR);
        pumpkin.setStencils(STENCILS);
        pumpkin.setCandles(Collections.nCopies(candles, Items.CANDLE));
    }

    @GameTest(maxTicks = 40)
    public void candleLightsDecoratedPumpkin(GameTestHelper context) {
        placeDecoratedPumpkin(context, ModBlocks.DECORATED_PUMPKIN.get());
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.DYED_CANDLE.red(), 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertDecorationKept(context, ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get());
            context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 1, "candle count");
            context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.LIT), true, "lit");
            context.assertValueEqual(context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).getCandles(), List.of(Items.DYED_CANDLE.red()), "candles held");
            context.assertValueEqual(dispenser.getItem(0).getCount(), 1, "candles left in the dispenser");
            context.assertItemEntityNotPresent(Items.DYED_CANDLE.red());
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void candleAddsToExistingCandles(GameTestHelper context) {
        placeCandlePumpkin(context, 3, true);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.CANDLE, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 4, "candle count");
            context.assertValueEqual(dispenser.getItem(0).getCount(), 1, "candles left in the dispenser");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void candleFailsOnFullPumpkin(GameTestHelper context) {
        placeCandlePumpkin(context, 4, true);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.CANDLE, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 4, "candle count");
            context.assertValueEqual(dispenser.getItem(0).getCount(), 2, "candles left in the dispenser");
            context.assertItemEntityNotPresent(Items.CANDLE);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void candleAtNonPumpkinFallsBackToDropping(GameTestHelper context) {
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.CANDLE, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            context.assertValueEqual(dispenser.getItem(0).getCount(), 1, "candles left in the dispenser");
            context.assertItemEntityPresent(Items.CANDLE);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void torchFailsOnCandlePumpkin(GameTestHelper context) {
        placeCandlePumpkin(context, 2, true);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.TORCH, 2));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            context.assertBlockPresent(ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get(), TARGET);
            context.assertValueEqual(dispenser.getItem(0).getCount(), 2, "torches left in the dispenser");
            context.assertItemEntityNotPresent(Items.TORCH);
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void flintAndSteelRelightsSnuffedCandlePumpkin(GameTestHelper context) {
        placeCandlePumpkin(context, 2, false);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.FLINT_AND_STEEL));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.LIT), true, "lit");
            context.assertValueEqual(context.getBlockState(TARGET).getValue(CandleLitDecoratedPumpkinBlock.CANDLES), 2, "candle count");
            context.assertValueEqual(dispenser.getItem(0).getDamageValue(), 1, "flint and steel damage");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void shearsRemoveCandlesFromPumpkin(GameTestHelper context) {
        placeCandlePumpkin(context, 2, true);
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.SHEARS));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            assertDecorationKept(context, ModBlocks.DECORATED_PUMPKIN.get());
            context.assertItemEntityPresent(Items.CANDLE);
            context.assertValueEqual(dispenser.getItem(0).getDamageValue(), 1, "shears damage");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 40)
    public void shearsStillHarvestBeehives(GameTestHelper context) {
        context.setBlock(TARGET, Blocks.BEEHIVE.defaultBlockState().setValue(BeehiveBlock.HONEY_LEVEL, BeehiveBlock.MAX_HONEY_LEVELS));
        DispenserBlockEntity dispenser = placeDispenser(context, new ItemStack(Items.SHEARS));
        context.pulseRedstone(TRIGGER, 2);

        context.runAfterDelay(SETTLE_TICKS, () -> {
            context.assertItemEntityPresent(Items.HONEYCOMB);
            context.assertValueEqual(dispenser.getItem(0).getDamageValue(), 1, "shears damage");
            context.succeed();
        });
    }
}
