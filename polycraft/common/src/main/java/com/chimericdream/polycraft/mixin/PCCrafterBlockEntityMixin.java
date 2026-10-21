package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.core.PinMap;
import com.chimericdream.polycraft.recipe.PinHolder;
import com.chimericdream.polycraft.recipe.PinPersistence;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The Crafter can have a recipe pinned to it, and keeps the pins when saved. */
@Mixin(CrafterBlockEntity.class)
public class PCCrafterBlockEntityMixin implements PinHolder {
    @Unique
    private final PinMap polycraft$pins = new PinMap();

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void polycraft$save(ValueOutput output, CallbackInfo ci) {
        PinPersistence.save(output, polycraft$pins);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void polycraft$load(ValueInput input, CallbackInfo ci) {
        PinPersistence.load(input, polycraft$pins);
    }

    @Override
    public PinMap polycraft$pins() {
        return polycraft$pins;
    }

    @Override
    public void polycraft$pinsChanged() {
        ((CrafterBlockEntity) (Object) this).setChanged();
    }
}
