package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffHardenedComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ShulkerStuff$ItemEntityMixin {
    // @TODO: this can be removed after 1.21.2
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void ssItemEntity$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getStack();

        if (stack.isEmpty() || !stack.getComponents().contains(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get())) {
            return;
        }

        ShulkerStuffHardenedComponent ssHardenedComponent = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get());
        if (ssHardenedComponent != null && ssHardenedComponent.value()) {
            cir.setReturnValue(false);
        }
    }
}
