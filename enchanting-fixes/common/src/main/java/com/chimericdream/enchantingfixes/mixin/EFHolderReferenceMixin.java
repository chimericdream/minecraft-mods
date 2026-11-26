package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.enchantment.EnchantmentIds;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code MappedRegistry.register} calls {@code bindValue} for each entry. This happens for the server
 * registries and for the registries that the client gets from the server.
 */
@Mixin(Holder.Reference.class)
public abstract class EFHolderReferenceMixin<T> {
    @Shadow
    private ResourceKey<T> key;

    @Inject(method = "bindValue(Ljava/lang/Object;)V", at = @At("TAIL"))
    private void enchantingfixes$recordEnchantmentId(Object value, CallbackInfo ci) {
        if (value instanceof Enchantment enchantment && this.key != null) {
            EnchantmentIds.record(enchantment, this.key.identifier().toString());
        }
    }
}
