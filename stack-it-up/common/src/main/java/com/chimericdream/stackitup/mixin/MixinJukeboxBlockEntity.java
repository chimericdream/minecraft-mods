package com.chimericdream.stackitup.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(JukeboxBlockEntity.class)
public class MixinJukeboxBlockEntity {

    /**
     *
     * Only store one record instead of the full stack, since that's what
     * MusicDiscItem assumes.
     * This prevents item duplication where MusicDiscItem decrements the player's
     * stack by 1, but the Jukebox stores and drops the full stack instead.
     *
     **/
    @ModifyVariable(method = "setTheItem", at = @At("HEAD"))
    private ItemStack setStack(ItemStack stack) {
        if (ItemsHelper.isModified(stack) && stack.getCount() > 1) {
            stack.setCount(1);
        }
        return stack;
    }
}
