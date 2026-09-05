package com.chimericdream.stackitup.fabric.mixin;

import java.util.function.Consumer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.chimericdream.stackitup.util.ItemsHelper;

// As of 1.21.11, damage(...)'s setDamage(int) call moved into a private helper,
// onDurabilityChange (now Mojang-named applyDamage), which all damage(...) overloads funnel
// through - redirect there instead. As of 26.1.2, ItemStack no longer tracks its holder entity
// at all (getEntityRepresentation() is gone), so grab the ServerPlayer directly from
// applyDamage's own (@Nullable) parameter instead of asking the stack who's holding it.
//
// Fabric/vanilla's applyDamage(int, ServerPlayer, Consumer) still directly calls setDamageValue in
// its own bytecode (unlike NeoForge - see the neoforge-specific copy of this mixin), so this
// redirect target is valid here.
@Mixin(ItemStack.class)
public class MixinItemStackDamage {
    @Redirect(method = "applyDamage(ILnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
    private void splitStackedTools(ItemStack instance, int damage, int newDamage, ServerPlayer player, Consumer<Item> onBreak) {
        ItemStack rest = null;
        if (instance.getCount() > 1 && ItemsHelper.isModified(instance) && player != null) {
            rest = instance.copy();
            rest.shrink(1);
            instance.setCount(1);
        }
        instance.setDamageValue(damage);
        if (rest != null) {
            ItemsHelper.insertNewItem(player, rest);
        }
    }
}
