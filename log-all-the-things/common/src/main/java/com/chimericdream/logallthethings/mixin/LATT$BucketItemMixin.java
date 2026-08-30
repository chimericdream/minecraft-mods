package com.chimericdream.logallthethings.mixin;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

/**
 * Vanilla {@link BucketItem#use} hardcodes {@code this.content == Fluids.WATER} to decide whether a
 * bucket is allowed to fill a {@code LiquidBlockContainer} in place (vs. placing a loose fluid block
 * one block over). Redirecting what that field read returns (only when this bucket's content is
 * actually lava) lets a lava bucket target the clicked lava-loggable block itself, the same way a
 * water bucket already does.
 *
 * <p>{@code BucketItem#emptyContents} has the same hardcoded water check on Fabric, fixed the same way
 * by {@code LATT$BucketItemFabricMixin} — but NOT here, and not for both platforms: NeoForge patches
 * {@code emptyContents} into a 4-arg wrapper around a new 5-arg overload
 * ({@code IDispensibleContainerItemExtension}) that already calls {@code container.placeLiquid(...)}
 * generically for any fluid a {@code LiquidBlockContainer} accepts, with no water-only gate at all —
 * so NeoForge needs no fix there, and this class doesn't attempt one. See
 * {@code LATT$BucketItemFabricMixin}'s doc comment for how this was confirmed.
 *
 * <p>{@code Fluids.WATER}/{@code Fluids.LAVA} are declared as {@link FlowingFluid}, not the wider
 * {@link Fluid} — the field read this modifies is typed accordingly, even though {@code content}
 * itself is a plain {@code Fluid}.
 */
@Mixin(BucketItem.class)
public abstract class LATT$BucketItemMixin {
    @Shadow
    @Final
    protected Fluid content;

    @ModifyExpressionValue(
        method = "use",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/Fluids;WATER:Lnet/minecraft/world/level/material/FlowingFluid;")
    )
    private FlowingFluid latt$allowLavaInUse(FlowingFluid original) {
        return this.content == Fluids.LAVA ? Fluids.LAVA : original;
    }
}
