package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffHardenedComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ShulkerStuff$ItemEntityMixin {
    // @TODO: this can be removed after 1.21.2
    // but why?
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void ss$damage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getItem();

        if (stack.isEmpty() || !stack.getComponents().has(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get())) {
            return;
        }

        ShulkerStuffHardenedComponent ssHardenedComponent = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get());
        if (ssHardenedComponent != null && ssHardenedComponent.value()) {
            cir.setReturnValue(false);
        }
    }
}
