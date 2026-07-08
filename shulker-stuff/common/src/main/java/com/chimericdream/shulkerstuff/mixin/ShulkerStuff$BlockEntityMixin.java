package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffHardenedComponent;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffPlatedComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
abstract public class ShulkerStuff$BlockEntityMixin {
    @Inject(method = "getUpdateTag", at = @At("TAIL"), cancellable = true)
    private void ss$getUpdateTag(HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
        if (!(((BlockEntity) (Object) this) instanceof ShulkerBoxBlockEntity)) {
            return;
        }

        CompoundTag nbt = cir.getReturnValue();
        CompoundTag components = nbt.getCompoundOrEmpty("components");

        ShulkerBoxBlockEntity self = (ShulkerBoxBlockEntity) (Object) this;

        ShulkerStuffDyedColorComponent ssDyedColorComponent = self.components().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
        if (ssDyedColorComponent != null) {
            components.put(ShulkerStuffDyedColorComponent.COMPONENT_ID.toString(), ssDyedColorComponent.toNbt());
        }

        ShulkerStuffHardenedComponent ssHardenedComponent = self.components().get(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get());
        if (ssHardenedComponent != null) {
            components.put(ShulkerStuffHardenedComponent.COMPONENT_ID.toString(), ssHardenedComponent.toNbt());
        }

        ShulkerStuffPlatedComponent ssPlatedComponent = self.components().get(ShulkerStuffComponentTypes.SHULKER_STUFF_PLATED_COMPONENT.get());
        if (ssPlatedComponent != null) {
            components.put(ShulkerStuffPlatedComponent.COMPONENT_ID.toString(), ssPlatedComponent.toNbt());
        }

        nbt.put("components", components);

        cir.setReturnValue(nbt);
    }
}
