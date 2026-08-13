package com.chimericdream.stackitup.neoforge.mixin;

import java.util.function.Consumer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.chimericdream.stackitup.util.ItemsHelper;

// NeoForge 26.2.0.15-beta splits vanilla's single ItemStack.applyDamage(int, ServerPlayer, Consumer)
// (confirmed via decompile) into a thin delegating overload of that same descriptor plus a new
// applyDamage(int, LivingEntity, Consumer) that is the one which actually calls setDamageValue - the
// ServerPlayer-descriptor overload no longer contains that call in its own bytecode, so a redirect
// targeting it (as the fabric-side copy of this mixin does) finds zero injection targets on NeoForge
// and fails to boot. Target the LivingEntity overload here instead, and only apply the
// split-stacked-tools behavior when the entity is actually a ServerPlayer (matching the original,
// narrower ServerPlayer-only behavior).
@Mixin(ItemStack.class)
public class MixinItemStackDamage {
    @Redirect(method = "applyDamage(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
    private void splitStackedTools(ItemStack instance, int damage, int newDamage, LivingEntity player, Consumer<Item> onBreak) {
        ItemStack rest = null;
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;
        if (instance.getCount() > 1 && ItemsHelper.isModified(instance) && serverPlayer != null) {
            rest = instance.copy();
            rest.shrink(1);
            instance.setCount(1);
        }
        instance.setDamageValue(damage);
        if (rest != null) {
            ItemsHelper.insertNewItem(serverPlayer, rest);
        }
    }
}
