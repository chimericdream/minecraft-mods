package com.chimericdream.lib.mixin;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Exposes {@code DispenserBlock#getDefaultDispenseMethod}, the per-stack fallback vanilla uses for
 * items with no {@link DispenserBlock#DISPENSER_REGISTRY} entry (equippables, sulfur-cube-swallowable
 * items, spawn eggs, else plain "drop the item"). {@link com.chimericdream.lib.dispenser.DispenserBehaviors}
 * delegates to it so wrapping an item that had no registered behavior keeps exactly that vanilla
 * fallback instead of assuming {@code DefaultDispenseItemBehavior}.
 */
@Mixin(DispenserBlock.class)
public interface DispenserBlockAccessor {
    @Invoker("getDefaultDispenseMethod")
    static DispenseItemBehavior chimericlib$getDefaultDispenseMethod(ItemStack itemStack) {
        throw new AssertionError();
    }
}
