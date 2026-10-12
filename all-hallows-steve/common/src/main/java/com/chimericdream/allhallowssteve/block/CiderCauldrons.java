package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.item.MulledCider;
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
 * The hand interactions for the cider and mulled-cider cauldrons: pouring cider in, adding spices,
 * ladling out with a glass bottle. Hoppers and dispensers never touch these, same as vanilla cauldrons.
 */
public final class CiderCauldrons {
    public static final BooleanProperty CINNAMON = BooleanProperty.create("cinnamon");
    public static final BooleanProperty CLOVES = BooleanProperty.create("cloves");
    public static final BooleanProperty NUTMEG = BooleanProperty.create("nutmeg");

    public static final CauldronInteraction.Dispatcher CIDER_BEHAVIORS = new CauldronInteraction.Dispatcher();
    public static final CauldronInteraction.Dispatcher MULLED_BEHAVIORS = new CauldronInteraction.Dispatcher();

    private CiderCauldrons() {
    }

    /** Registers the item interactions. Needs the registry objects, so it runs from {@code postInit}. */
    public static void init() {
        Item cider = ModItems.CIDER.get();

        CauldronInteractions.EMPTY.put(cider, CiderCauldrons::fillEmptyCauldron);
        CIDER_BEHAVIORS.put(cider, CiderCauldrons::addCider);
        CIDER_BEHAVIORS.put(Items.GLASS_BOTTLE, CiderCauldrons::takeCider);

        CIDER_BEHAVIORS.put(ModItems.CINNAMON.get(), spice(CINNAMON));
        CIDER_BEHAVIORS.put(ModItems.CLOVES.get(), spice(CLOVES));
        CIDER_BEHAVIORS.put(ModItems.NUTMEG.get(), spice(NUTMEG));

        MULLED_BEHAVIORS.put(Items.GLASS_BOTTLE, CiderCauldrons::ladle);
    }

    private static InteractionResult fillEmptyCauldron(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            swapForBottle(player, hand, stack, new ItemStack(Items.GLASS_BOTTLE));
            level.setBlockAndUpdate(pos, ModBlocks.CIDER_CAULDRON.get().defaultBlockState());
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
                level.scheduleTick(pos, next.getBlock(), CiderCauldronBlock.TICK_INTERVAL);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult takeCider(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            swapForBottle(player, hand, stack, new ItemStack(ModItems.CIDER.get()));
            lowerLevel(state, level, pos);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult ladle(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            ItemStack serving = MulledCider.serving(state.getValue(CINNAMON), state.getValue(CLOVES), state.getValue(NUTMEG));

            swapForBottle(player, hand, stack, serving);
            lowerLevel(state, level, pos);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static CauldronInteraction spice(BooleanProperty property) {
        return (state, level, pos, player, hand, stack) -> {
            if (state.getValue(LayeredCauldronBlock.LEVEL) < LayeredCauldronBlock.MAX_FILL_LEVEL || state.getValue(property)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            if (!level.isClientSide()) {
                stack.consume(1, player);
                level.setBlockAndUpdate(pos, state.setValue(property, true));
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                player.awardStat(Stats.USE_CAULDRON);
            }

            return InteractionResult.SUCCESS;
        };
    }

    private static void swapForBottle(Player player, InteractionHand hand, ItemStack used, ItemStack result) {
        Item item = used.getItem();

        player.setItemInHand(hand, ItemUtils.createFilledResult(used, player, result));
        player.awardStat(Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(item));
    }

    /** Takes one serving out. The cauldron goes back to a vanilla empty one at zero; a part-empty cider batch stops simmering. */
    private static void lowerLevel(BlockState state, Level level, BlockPos pos) {
        if (state.hasProperty(CiderCauldronBlock.SIMMER)) {
            state = state.setValue(CiderCauldronBlock.SIMMER, 0);
        }

        LayeredCauldronBlock.lowerFillLevel(state, level, pos);
    }
}
