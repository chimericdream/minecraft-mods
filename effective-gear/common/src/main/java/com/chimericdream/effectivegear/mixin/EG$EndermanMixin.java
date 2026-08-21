package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EG$EndermanMixin {
    @Unique
    private static final EquipmentSlot[] EG$ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @Inject(method = "isBeingStaredBy", at = @At("HEAD"), cancellable = true)
    private void eg$ignoreGazeFromFullEnderPearlTrim(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (eg$isWearingFullEnderPearlTrim(player)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean eg$isWearingFullEnderPearlTrim(Player player) {
        for (EquipmentSlot slot : EG$ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                return false;
            }

            ArmorTrim trim = stack.get(DataComponents.TRIM);

            if (trim == null || !trim.material().is(Trims.ENDER_PEARL_TRIM_ID)) {
                return false;
            }
        }

        return true;
    }
}
