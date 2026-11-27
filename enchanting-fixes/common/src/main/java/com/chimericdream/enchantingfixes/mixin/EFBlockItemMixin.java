package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.enchantment.AdvancedSilkTouch;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Lets a player who is not an operator place a spawner from Advanced Silk Touch X. See {@link AdvancedSilkTouch#placeSpawner}. */
@Mixin(BlockItem.class)
public abstract class EFBlockItemMixin {
    @ModifyReturnValue(
        method = "updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At("RETURN")
    )
    private static boolean enchantingfixes$placeSpawner(boolean original, Level level, @Nullable Player player, BlockPos pos, ItemStack stack) {
        return original || AdvancedSilkTouch.placeSpawner(level, pos, stack);
    }
}
