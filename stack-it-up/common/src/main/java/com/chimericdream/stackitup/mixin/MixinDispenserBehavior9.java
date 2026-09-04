package com.chimericdream.stackitup.mixin;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.stackitup.util.IDispenserBlockEntity;
import com.chimericdream.stackitup.util.ItemsHelper;

// Anonymous class numbering is not a stable id - it shifts on every version bump, even a small
// patch bump, and can even differ between Yarn's and Mojang's view of the exact same physical
// class (verified by decompiling and diffing bytecode: this is the honeycomb-block-waxing
// dispense behavior, DispenserBehavior$9 in old 1.21 Yarn, DispenserBehavior$6 in 1.21.11 Yarn,
// DispenseItemBehavior$14 under 1.21.11 official Mojang mappings, DispenseItemBehavior$13 under
// 26.1.2, and DispenseItemBehavior$12 under 26.2 (Fabric). Re-verify on every version bump and
// every mapping migration - never trust that an unrelated compile+boot success means this is
// still right, since a wrong number here fails loudly at boot only if Mixin can't find a
// matching method at all; if some other slot ever coincidentally has a same-named/same-descriptor
// method, it could silently apply to the wrong behavior instead. NeoForge's own ASM patches to
// dispenser-adjacent classes are a further, independent source of drift from the Fabric index
// above - re-verify against a NeoForge decompile too before assuming this target is shared.
@Mixin(targets = "net/minecraft/core/dispenser/DispenseItemBehavior$12")
public class MixinDispenserBehavior9 {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    public void tryStack(BlockSource pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        // Add the item to the first free slot and get the remaining stack
        ItemStack remainingStack = pointer.blockEntity().insertItem(stack);

        // Check if the item is modified and if there's any remaining item stack after the operation
        if (ItemsHelper.isModified(stack) && !remainingStack.isEmpty()) {
            // Attempt custom logic to stack the remaining item
            boolean success = ((IDispenserBlockEntity) pointer.blockEntity()).tryInsertAndStackItem(remainingStack);

            // If custom stacking succeeds, cancel further execution and return an empty stack
            if (success) {
                cir.setReturnValue(ItemStack.EMPTY);
                cir.cancel();
            }
        }
    }
}
