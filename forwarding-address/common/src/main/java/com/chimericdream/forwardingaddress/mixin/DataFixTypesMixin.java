package com.chimericdream.forwardingaddress.mixin;

import com.chimericdream.forwardingaddress.migration.SavedDataRewrites;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Player data always goes through the data fixer, even when it is already current. Region data does
 * not (see {@link SimpleRegionStorageMixin}), so it is left to that mixin to avoid renaming twice.
 */
@Mixin(DataFixTypes.class)
public abstract class DataFixTypesMixin {
    @Inject(
        method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
        at = @At("RETURN")
    )
    private void forwardingaddress$renameRetiredIds(DataFixer fixer, Dynamic<?> dynamic, int from, int to, CallbackInfoReturnable<Dynamic<?>> cir) {
        if ((Object) this != DataFixTypes.PLAYER) {
            return;
        }

        if (cir.getReturnValue().getValue() instanceof CompoundTag tag) {
            SavedDataRewrites.apply("player", tag);
        }
    }
}
