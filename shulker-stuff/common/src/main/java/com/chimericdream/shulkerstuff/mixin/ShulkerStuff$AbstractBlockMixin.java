package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffHardenedComponent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(AbstractBlock.class)
public class ShulkerStuff$AbstractBlockMixin {
    @Inject(method = "onExploded", at = @At("HEAD"), cancellable = true)
    private void getBlastResistance(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo cir) {
        //noinspection ConstantValue
        if (!((Object) this instanceof ShulkerBoxBlock)) {
            return;
        }

        BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof ShulkerBoxBlockEntity shulker)) {
            return;
        }

        ShulkerStuffHardenedComponent ssHardenedComponent = shulker.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get());
        if (ssHardenedComponent == null) {
            return;
        }

        if (ssHardenedComponent.value()) {
            cir.cancel();
        }
    }
}
