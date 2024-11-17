package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.component.type.ContainerComponentBuilder;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ShulkerStuff$ItemMixin {
    @Inject(method = "onStackClicked", at = @At("HEAD"), cancellable = true)
    private void onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                return;
            }

            if (clickType != ClickType.RIGHT) {
                cir.setReturnValue(false);

                return;
            }

            ContainerComponent contents = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
            ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

            ItemStack itemStack = slot.getStack();
            if (itemStack.isEmpty()) {
                ItemStack itemStack2 = builder.removeFirst();
                if (!itemStack2.isEmpty()) {
                    ItemStack itemStack3 = slot.insertStack(itemStack2);
                    builder.addStack(itemStack3);
                    this.ssItem$playRemoveOneSound(player);
                }
            } else if (itemStack.getItem().canBeNested()) {
                int i = builder.addFromSlot(slot, player);
                if (i > 0) {
                    this.ssItem$playInsertSound(player);
                }
            }

            stack.set(DataComponentTypes.CONTAINER, builder.build());
            cir.setReturnValue(true);
        } catch (Exception e) {
            ShulkerStuffMod.LOGGER.error("An error occurred while processing a shulker box item click.", e);
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return;
        }

        ContainerComponent contents = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
        ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

        if (otherStack.isEmpty()) {
            ItemStack itemStack = builder.removeFirst();
            if (itemStack != null) {
                this.ssItem$playRemoveOneSound(player);
                cursorStackReference.set(itemStack);
            }
        } else {
            int startingCount = otherStack.getCount();
            ItemStack remainder = builder.addStack(otherStack);
            if (remainder.getCount() != startingCount) {
                this.ssItem$playInsertSound(player);
            }
            cursorStackReference.set(remainder);
        }

        stack.set(DataComponentTypes.CONTAINER, builder.build());
        cir.setReturnValue(true);
    }

    @Unique
    private void ssItem$playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED, 0.2F, 0.95F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    @Unique
    private void ssItem$playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 0.2F, 0.95F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }
}
