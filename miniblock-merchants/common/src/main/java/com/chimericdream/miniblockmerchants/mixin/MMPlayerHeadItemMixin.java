package com.chimericdream.miniblockmerchants.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PlayerHeadItem;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerHeadItem.class)
public class MMPlayerHeadItemMixin {
    @Inject(method = "getName(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/text/Text;", at = @At("RETURN"), cancellable = true)
    private void mm$getName(ItemStack stack, CallbackInfoReturnable<Text> cir) {
        String currentName = cir.getReturnValue().getString();

        if (currentName.startsWith("mmminiblock") || currentName.startsWith("mmmobhead")) {
            cir.setReturnValue((Text) stack.getComponents().getOrDefault(DataComponentTypes.ITEM_NAME, ScreenTexts.EMPTY));
        }
    }
}
