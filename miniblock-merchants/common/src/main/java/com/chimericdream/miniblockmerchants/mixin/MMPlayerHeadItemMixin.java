package com.chimericdream.miniblockmerchants.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerHeadItem.class)
public class MMPlayerHeadItemMixin {
    @Inject(method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;", at = @At("RETURN"), cancellable = true)
    private void mm$getName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        String currentName = cir.getReturnValue().getString();

        if (currentName.startsWith("mmminiblock") || currentName.startsWith("mmmobhead")) {
            cir.setReturnValue((Component) stack.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY));
        }
    }
}
