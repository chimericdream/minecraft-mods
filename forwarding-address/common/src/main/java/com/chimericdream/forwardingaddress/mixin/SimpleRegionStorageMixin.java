package com.chimericdream.forwardingaddress.mixin;

import com.chimericdream.forwardingaddress.migration.SavedDataRewrites;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Chunk, entity and POI data all pass through {@code upgradeChunkTag}, which returns early without
 * calling the data fixer when the data is already current. That is the case a mod update leaves
 * behind, so the rename has to happen here and not in {@code DataFixTypes}.
 */
@Mixin(SimpleRegionStorage.class)
public abstract class SimpleRegionStorageMixin {
    @Inject(
        method = "upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;ILnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;",
        at = @At("HEAD")
    )
    private void forwardingaddress$renameRetiredIds(CompoundTag tag, int defaultVersion, CompoundTag context, int targetVersion, CallbackInfoReturnable<CompoundTag> cir) {
        SavedDataRewrites.apply("region", tag);
    }
}
