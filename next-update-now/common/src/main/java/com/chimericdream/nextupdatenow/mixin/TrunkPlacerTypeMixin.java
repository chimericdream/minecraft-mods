package com.chimericdream.nextupdatenow.mixin;

import com.chimericdream.nextupdatenow.worldgen.ModWorldgenTypes;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * See {@link ModWorldgenTypes} for why this runs here instead of from mod init.
 */
@Mixin(TrunkPlacerType.class)
public class TrunkPlacerTypeMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void nextupdatenow$registerPoplarTrunkPlacer(CallbackInfo ci) {
        ModWorldgenTypes.registerTrunkPlacerType();
    }
}
