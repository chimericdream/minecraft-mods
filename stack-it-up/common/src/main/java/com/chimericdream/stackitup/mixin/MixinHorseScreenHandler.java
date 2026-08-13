package com.chimericdream.stackitup.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/world/inventory/HorseInventoryMenu$1")
public class MixinHorseScreenHandler extends Slot {
    public MixinHorseScreenHandler(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
