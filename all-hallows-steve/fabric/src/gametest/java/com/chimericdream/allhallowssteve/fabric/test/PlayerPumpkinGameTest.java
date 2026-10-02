package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.block.PumpkinContents;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;

import java.util.List;

/**
 * Player-side coverage for lighting (shift + torch, via {@code AHS$TorchBlockItemMixin}) and
 * un-lighting (shears) both decorated and vanilla pumpkins. {@link GameTestHelper#useBlock} runs the
 * block's {@code useItemOn} first and falls through to {@code ItemStack#useOn}, which is exactly where
 * the torch and shears mixins hook in.
 */
@SuppressWarnings("unused")
public class PlayerPumpkinGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);

    private static Player playerHolding(GameTestHelper context, Item item, int count, boolean sneaking) {
        PumpkinMessageRecorder.install();
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, count));
        player.setShiftKeyDown(sneaking);

        return player;
    }

    private static void placePumpkin(GameTestHelper context, Block block) {
        context.setBlock(TARGET, block.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, Direction.SOUTH));
    }

    @GameTest
    public void sneakingTorchLightsCarvedPumpkin(GameTestHelper context) {
        placePumpkin(context, Blocks.CARVED_PUMPKIN);
        Player player = playerHolding(context, Items.TORCH, 2, true);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(Blocks.JACK_O_LANTERN, TARGET);
        context.assertValueEqual(context.getBlockState(TARGET).getValue(CarvedPumpkinBlock.FACING), Direction.SOUTH, "jack o'lantern facing");
        context.assertValueEqual(player.getMainHandItem().getCount(), 1, "torches left in hand");
        context.assertValueEqual(PumpkinMessageRecorder.keys(player), List.of(), "messages sent");
        context.succeed();
    }

    @GameTest
    public void sneakingSoulTorchDoesNotLightCarvedPumpkin(GameTestHelper context) {
        placePumpkin(context, Blocks.CARVED_PUMPKIN);
        Player player = playerHolding(context, Items.SOUL_TORCH, 2, true);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(Blocks.CARVED_PUMPKIN, TARGET);
        context.succeed();
    }

    @GameTest
    public void plainTorchClickDoesNotLightCarvedPumpkin(GameTestHelper context) {
        placePumpkin(context, Blocks.CARVED_PUMPKIN);
        Player player = playerHolding(context, Items.TORCH, 2, false);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(Blocks.CARVED_PUMPKIN, TARGET);
        context.succeed();
    }

    @GameTest
    public void shearsTurnJackOLanternBackIntoCarvedPumpkin(GameTestHelper context) {
        placePumpkin(context, Blocks.JACK_O_LANTERN);
        Player player = playerHolding(context, Items.SHEARS, 1, false);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(Blocks.CARVED_PUMPKIN, TARGET);
        context.assertValueEqual(context.getBlockState(TARGET).getValue(CarvedPumpkinBlock.FACING), Direction.SOUTH, "carved pumpkin facing");
        context.assertItemEntityPresent(Items.TORCH);
        context.assertValueEqual(player.getMainHandItem().getDamageValue(), 1, "shears damage");
        context.assertValueEqual(PumpkinMessageRecorder.keys(player), List.of(), "messages sent");
        context.succeed();
    }

    @GameTest
    public void sneakingTorchLightsDecoratedPumpkin(GameTestHelper context) {
        context.setBlock(TARGET, ModBlocks.DECORATED_PUMPKIN.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, Direction.SOUTH));
        context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).setColor(0x3366CC);
        Player player = playerHolding(context, Items.REDSTONE_TORCH, 2, true);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.LIT_DECORATED_PUMPKIN_RED.get(), TARGET);
        context.assertValueEqual(context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).getColor(), 0x3366CC, "pumpkin color");
        context.assertValueEqual(player.getMainHandItem().getCount(), 1, "torches left in hand");
        context.assertValueEqual(PumpkinMessageRecorder.keys(player), List.of(Items.REDSTONE_TORCH.getDescriptionId()), "messages sent");
        context.succeed();
    }

    @GameTest
    public void shearsUnlightDecoratedPumpkin(GameTestHelper context) {
        context.setBlock(TARGET, ModBlocks.LIT_DECORATED_PUMPKIN.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, Direction.SOUTH));
        context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).setColor(0x3366CC);
        Player player = playerHolding(context, Items.SHEARS, 1, false);

        context.useBlock(TARGET, player);

        context.assertBlockPresent(ModBlocks.DECORATED_PUMPKIN.get(), TARGET);
        context.assertValueEqual(context.getBlockEntity(TARGET, DecoratedPumpkinBlockEntity.class).getColor(), 0x3366CC, "pumpkin color");
        context.assertItemEntityPresent(Items.TORCH);
        context.assertValueEqual(player.getMainHandItem().getDamageValue(), 1, "shears damage");
        context.assertValueEqual(PumpkinMessageRecorder.keys(player), List.of(PumpkinContents.EMPTY_KEY), "messages sent");
        context.succeed();
    }
}
