package com.chimericdream.stackitup.mixin;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ArmorSlot;
import org.spongepowered.asm.mixin.Mixin;

// HorseInventoryMenu$1 extends ArmorSlot (not Slot directly) on 26.1.2, with ArmorSlot's own
// 7-arg constructor - a mismatched superclass/constructor here fails Mixin's apply-time
// compatibility check and crashes the mod at boot, so this must track ArmorSlot exactly.
@Mixin(targets = "net/minecraft/world/inventory/HorseInventoryMenu$1")
public class MixinHorseScreenHandler extends ArmorSlot {
    public MixinHorseScreenHandler(Container inventory, LivingEntity owner, EquipmentSlot slot, int index, int x, int y, Identifier emptyIcon) {
        super(inventory, owner, slot, index, x, y, emptyIcon);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
