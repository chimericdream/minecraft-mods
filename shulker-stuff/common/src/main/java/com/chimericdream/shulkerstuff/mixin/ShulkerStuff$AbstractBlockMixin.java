package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffPlatedComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public class ShulkerStuff$AbstractBlockMixin {
    @Inject(method = "onExplosionHit", at = @At("HEAD"), cancellable = true)
    private void getBlastResistance(BlockState state, ServerLevel world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo cir) {
        //noinspection ConstantValue
        if (!((Object) this instanceof ShulkerBoxBlock)) {
            return;
        }

        BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof ShulkerBoxBlockEntity shulker)) {
            return;
        }

        ShulkerStuffPlatedComponent ssPlatedComponent = shulker.components().get(ShulkerStuffComponentTypes.SHULKER_STUFF_PLATED_COMPONENT.get());
        if (ssPlatedComponent == null) {
            return;
        }

        if (ssPlatedComponent.value()) {
            cir.cancel();
        }
    }
}
