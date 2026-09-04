package com.chimericdream.stackitup.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chimericdream.stackitup.util.IItemMaxCount;
import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(AnvilMenu.class)
public class MixinAnvilScreenHandler {
    /**
     *
     * Decrement the input stacks by one instead of blindly setting them
     * to an empty stack. The decrement is only done on the server to avoid
     * a desync between the server and client that results in renaming breaking.
     * This prevents entire stacks from being deleted at a time.
     *
     **/
    @Redirect(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private void decrementOne(Container inventory, int slot, ItemStack stack, Player player, ItemStack takenStack) {
        ItemStack originalStack = inventory.getItem(slot);
        if (ItemsHelper.isModified(originalStack) && originalStack.getCount() > 1) {
            if (stack.isEmpty()) {
                stack = originalStack;
                if (!player.level().isClientSide()) {
                    stack.shrink(1);
                }
            }
        }

        inventory.setItem(slot, stack);
    }

    /**
     *
     * Update the output every time the output is taken in addition to
     * updating when the inputs are changed.
     * This allows the output to be taken multiple times without needing
     * to change an input in between every take.
     *
     **/
    @Inject(method = "onTake", at = @At("RETURN"))
    private void updateAfterTaking(CallbackInfo ci) {
        ((AnvilMenu) (Object) this).createResult();
    }

    /**
     *
     * Only output a single item at a time when the item was originally capped
     * at 1.
     * This prevents entire stacks from being repaired/enchanted at a time with
     * wrong costs. It assumes that if the item was originally stackable, that
     * its anvil costs are already balanced.
     *
     **/
    @ModifyVariable(method = "createResult", at = @At("STORE"), ordinal = 0)
    private ItemStack copyOne(ItemStack stack) {
        if (ItemsHelper.isModified(stack) && ((IItemMaxCount) stack.getItem()).getVanillaMaxCount() == 1) {
            stack = stack.copy();
            stack.setCount(1);
        }
        return stack;
    }
}
