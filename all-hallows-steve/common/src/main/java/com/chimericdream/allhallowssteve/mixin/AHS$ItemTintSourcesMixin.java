package com.chimericdream.allhallowssteve.mixin;

import com.chimericdream.allhallowssteve.client.color.DyedPumpkinItemTintSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MC 26.2's item tints moved to a data-driven {@code ItemTintSource} dispatch registry
 * ({@code ItemTintSources.ID_MAPPER}), which has no mod-facing registration hook (unlike the still
 * Java-callback-based {@code BlockTintSource}/{@code ColorHandlerRegistry}, used for the placed block
 * — see {@link com.chimericdream.allhallowssteve.client.color.DyedPumpkinBlockColors}). Injecting into
 * {@code bootstrap()} lets this mixin add an entry the same way vanilla's own tint sources
 * ("potion", "dye", "firework", ...) are added — the injected code is merged directly into
 * {@code ItemTintSources}, so it can reach the private static {@code ID_MAPPER} field without an
 * accessor mixin.
 */
@Mixin(ItemTintSources.class)
public class AHS$ItemTintSourcesMixin {
    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ItemTintSource>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void ahs$registerDyedPumpkin(CallbackInfo ci) {
        ID_MAPPER.put(DyedPumpkinItemTintSource.ID, DyedPumpkinItemTintSource.MAP_CODEC);
    }
}
