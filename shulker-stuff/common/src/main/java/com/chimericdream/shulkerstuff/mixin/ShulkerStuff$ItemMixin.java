package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.component.type.ContainerComponentBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ShulkerStuff$ItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.shouldShowName() || !(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        if (this.ssItem$dropFirstItem(stack, player)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "overrideStackedOnOther", at = @At("HEAD"), cancellable = true)
    private void ss$overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickType, Player player, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                return;
            }

            if (clickType != ClickAction.SECONDARY) {
                cir.setReturnValue(false);

                return;
            }

            ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

            ItemStack itemStack = slot.getItem();
            if (itemStack.isEmpty()) {
                ItemStack itemStack2 = builder.removeFirst();
                if (!itemStack2.isEmpty()) {
                    ItemStack itemStack3 = slot.safeInsert(itemStack2);
                    builder.addStack(itemStack3);
                    this.ssItem$playRemoveOneSound(player);
                }
            } else if (itemStack.getItem().canFitInsideContainerItems()) {
                int i = builder.addFromSlot(slot, player);
                if (i > 0) {
                    this.ssItem$playInsertSound(player);
                }
            }

            stack.set(DataComponents.CONTAINER, builder.build());
            cir.setReturnValue(true);
        } catch (Exception e) {
            ShulkerStuffMod.LOGGER.error("An error occurred while processing a shulker box item click.", e);
        }
    }

    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void ss$overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        if (clickType != ClickAction.SECONDARY || !slot.mayPickup(player)) {
            return;
        }

        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

        if (otherStack.isEmpty()) {
            ItemStack itemStack = builder.removeFirst();
            if (itemStack != null) {
                this.ssItem$playRemoveOneSound(player);
                cursorStackReference.set(itemStack);
            }
        } else {
            int startingCount = otherStack.getCount();
            builder.addStack(otherStack);
            if (otherStack.getCount() != startingCount) {
                this.ssItem$playInsertSound(player);
            }
            cursorStackReference.set(otherStack);
        }

        stack.set(DataComponents.CONTAINER, builder.build());
        cir.setReturnValue(true);
    }

    @Unique
    private void ssItem$playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.SHULKER_HURT_CLOSED, 0.2F, 0.95F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    @Unique
    private void ssItem$playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.SHULKER_BULLET_HURT, 0.2F, 0.95F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    @Unique
    private boolean ssItem$dropFirstItem(ItemStack stack, Player player) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

        ItemStack stack2 = builder.removeFirst();
        if (stack2.isEmpty()) {
            return false;
        }

        if (player instanceof ServerPlayer) {
            player.drop(stack2, true);
        }

        this.ssItem$playRemoveOneSound(player);

        stack.set(DataComponents.CONTAINER, builder.build());

        return true;
    }
}
