package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * The hand interactions for the cider and mulled-cider cauldrons: pouring cider in, adding mulling spices,
 * ladling out with a glass bottle. Hoppers and dispensers never touch these, same as vanilla cauldrons.
 */
public final class AppleCiderCauldrons {
    /** Whether mulling spices have been added to this batch. */
    public static final BooleanProperty SPICED = BooleanProperty.create("spiced");

    public static final CauldronInteraction.Dispatcher CIDER_BEHAVIORS = new CauldronInteraction.Dispatcher();
    public static final CauldronInteraction.Dispatcher MULLED_BEHAVIORS = new CauldronInteraction.Dispatcher();

    private AppleCiderCauldrons() {
    }

    /** Registers the item interactions. Needs the registry objects, so it runs from {@code postInit}. */
    public static void init() {
        Item cider = ModItems.APPLE_CIDER.get();

        CauldronInteractions.EMPTY.put(cider, AppleCiderCauldrons::fillEmptyCauldron);
        CIDER_BEHAVIORS.put(cider, AppleCiderCauldrons::addCider);
        CIDER_BEHAVIORS.put(Items.GLASS_BOTTLE, AppleCiderCauldrons::takeCider);

        CIDER_BEHAVIORS.put(ModItems.MULLING_SPICES.get(), AppleCiderCauldrons::addSpices);

        MULLED_BEHAVIORS.put(Items.GLASS_BOTTLE, AppleCiderCauldrons::ladle);
    }

    private static InteractionResult fillEmptyCauldron(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            swapForBottle(player, hand, stack, new ItemStack(Items.GLASS_BOTTLE));
            level.setBlockAndUpdate(pos, ModBlocks.APPLE_CIDER_CAULDRON.get().defaultBlockState());
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult addCider(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (state.getValue(LayeredCauldronBlock.LEVEL) >= LayeredCauldronBlock.MAX_FILL_LEVEL) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            swapForBottle(player, hand, stack, new ItemStack(Items.GLASS_BOTTLE));

            BlockState next = state.cycle(LayeredCauldronBlock.LEVEL);
            level.setBlockAndUpdate(pos, next);
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);

            if (next.getValue(LayeredCauldronBlock.LEVEL) == LayeredCauldronBlock.MAX_FILL_LEVEL) {
                level.scheduleTick(pos, next.getBlock(), AppleCiderCauldronBlock.TICK_INTERVAL);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult takeCider(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            swapForBottle(player, hand, stack, new ItemStack(ModItems.APPLE_CIDER.get()));
            lowerLevel(state, level, pos);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult ladle(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            swapForBottle(player, hand, stack, new ItemStack(ModItems.MULLED_APPLE_CIDER.get()));
            lowerLevel(state, level, pos);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult addSpices(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (state.getValue(LayeredCauldronBlock.LEVEL) < LayeredCauldronBlock.MAX_FILL_LEVEL || state.getValue(SPICED)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            stack.consume(1, player);
            BlockState next = state.setValue(SPICED, true);
            level.setBlockAndUpdate(pos, next);
            level.scheduleTick(pos, next.getBlock(), AppleCiderCauldronBlock.TICK_INTERVAL);
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.USE_CAULDRON);
        }

        return InteractionResult.SUCCESS;
    }

    private static void swapForBottle(Player player, InteractionHand hand, ItemStack used, ItemStack result) {
        Item item = used.getItem();

        player.setItemInHand(hand, ItemUtils.createFilledResult(used, player, result));
        player.awardStat(Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(item));
    }

    /** Takes one serving out. The cauldron goes back to a vanilla empty one at zero; a part-empty cider batch stops simmering. */
    private static void lowerLevel(BlockState state, Level level, BlockPos pos) {
        if (state.hasProperty(AppleCiderCauldronBlock.SIMMER)) {
            state = state.setValue(AppleCiderCauldronBlock.SIMMER, 0);
        }

        LayeredCauldronBlock.lowerFillLevel(state, level, pos);
    }
}
