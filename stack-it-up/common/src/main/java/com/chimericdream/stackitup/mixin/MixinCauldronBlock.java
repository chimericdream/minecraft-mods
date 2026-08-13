package com.chimericdream.stackitup.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(AbstractCauldronBlock.class)
public class MixinCauldronBlock {

    @Final
    @Shadow
    protected CauldronInteraction.Dispatcher interactions;

    CauldronInteraction CLEAN_STACKED_SHULKER_BOX = (state, world, pos, player, hand, stack) -> {
        Block block = Block.byItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            if (!world.isClientSide()) {
                ItemStack itemStack = new ItemStack(Blocks.SHULKER_BOX);
                if (!stack.getComponents().isEmpty()) {
                    itemStack.applyComponents(stack.getComponents());
                }
                ItemsHelper.insertNewItem(player, itemStack);
                stack.shrink(1);
                player.awardStat(Stats.CLEAN_SHULKER_BOX);
                LayeredCauldronBlock.lowerFillLevel(state, world, pos);
            }

            return InteractionResult.SUCCESS;
        }
    };

    // As of 26.1.2, useItemOn no longer calls java.util.Map.get directly - the item->behavior
    // lookup moved into CauldronInteraction.Dispatcher.get(ItemStack), a custom method on a new
    // nested type (interactions was CauldronInteraction.InteractionMap, now .Dispatcher).
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/cauldron/CauldronInteraction$Dispatcher;get(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/cauldron/CauldronInteraction;"), cancellable = true)
    private void cleanStackedShulkerBox(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (ItemsHelper.isModified(itemStack) && itemStack.getCount() > 1) {
            // CauldronInteractions.WATER's registered shulker-box-cleaning behavior is a private
            // method reference with no public identity to compare against, so gate on the same
            // conditions vanilla uses to select that behavior.
            if (interactions == CauldronInteractions.WATER && Block.byItem(itemStack.getItem()) instanceof ShulkerBoxBlock) {
                cir.setReturnValue(CLEAN_STACKED_SHULKER_BOX.interact(state, world, pos, player, hand, itemStack));
            }
        }
    }
}
