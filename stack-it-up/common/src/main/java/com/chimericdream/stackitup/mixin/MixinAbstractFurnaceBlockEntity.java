package com.chimericdream.stackitup.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceBlockEntity {
    // As of 26.1.2, serverTick's fuel.shrink(1) call moved into a private helper, consumeFuel,
    // so the shrink(I)V INVOKE this used to target no longer exists directly in serverTick's own
    // bytecode - inject after the consumeFuel(...) call itself instead, which still runs inside
    // serverTick and keeps world/pos/blockEntity all in scope.
    @Inject(method = "serverTick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;consumeFuel(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void popBuckets(ServerLevel world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        ItemStack itemStack = ((AccessorFurnaceInventory) blockEntity).getItems().get(1);
        // >= 1 because it is decreased by 1 before our code execution
        if (ItemsHelper.isModified(itemStack) && itemStack.getCount() >= 1) {
            if (itemStack.is(Items.LAVA_BUCKET)) {
                Containers.dropItemStack(world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), new ItemStack(Items.BUCKET));
            }
        }
    }
}
