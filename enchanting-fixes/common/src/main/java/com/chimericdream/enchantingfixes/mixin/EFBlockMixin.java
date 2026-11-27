package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.enchantment.AdvancedSilkTouch;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/** Fabric and NeoForge both get the drops of a block that an entity breaks with a tool from this method. */
@Mixin(Block.class)
public abstract class EFBlockMixin {
    @ModifyReturnValue(
        method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;",
        at = @At("RETURN")
    )
    private static List<ItemStack> enchantingfixes$advancedSilkTouchDrops(
        List<ItemStack> original,
        BlockState state,
        ServerLevel level,
        BlockPos pos,
        @Nullable BlockEntity blockEntity,
        @Nullable Entity breaker,
        ItemInstance tool
    ) {
        return AdvancedSilkTouch.drops(original, state, level, blockEntity, tool);
    }
}
