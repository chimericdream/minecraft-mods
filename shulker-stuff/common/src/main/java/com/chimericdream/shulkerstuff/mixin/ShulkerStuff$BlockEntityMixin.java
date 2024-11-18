package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDataComponent;
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
        ShulkerStuffDataComponent ssData = self.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DATA.get());
        if (ssData != null) {
            components.put(ShulkerStuffDataComponent.COMPONENT_ID.toString(), ssData.toNbt());
            nbt.put("components", components);
        }

        cir.setReturnValue(nbt);
    }
}
