package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffHardenedComponent;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffPlatedComponent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
abstract public class ShulkerStuff$BlockEntityMixin {
    @Inject(method = "toInitialChunkDataNbt", at = @At("TAIL"), cancellable = true)
    private void ss$toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup, CallbackInfoReturnable<NbtCompound> cir) {
        if (!(((BlockEntity) (Object) this) instanceof ShulkerBoxBlockEntity)) {
            return;
        }

        NbtCompound nbt = cir.getReturnValue();
        NbtCompound components = nbt.getCompound("components");

        ShulkerBoxBlockEntity self = (ShulkerBoxBlockEntity) (Object) this;

        ShulkerStuffDyedColorComponent ssDyedColorComponent = self.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
        if (ssDyedColorComponent != null) {
            components.put(ShulkerStuffDyedColorComponent.COMPONENT_ID.toString(), ssDyedColorComponent.toNbt());
        }

        ShulkerStuffHardenedComponent ssHardenedComponent = self.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get());
        if (ssHardenedComponent != null) {
            components.put(ShulkerStuffHardenedComponent.COMPONENT_ID.toString(), ssHardenedComponent.toNbt());
        }

        ShulkerStuffPlatedComponent ssPlatedComponent = self.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_PLATED_COMPONENT.get());
        if (ssPlatedComponent != null) {
            components.put(ShulkerStuffPlatedComponent.COMPONENT_ID.toString(), ssPlatedComponent.toNbt());
        }

        nbt.put("components", components);

        cir.setReturnValue(nbt);
    }
}
