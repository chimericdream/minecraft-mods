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
 * Fabric-only counterpart to {@code LATT$BucketItemMixin}'s {@code use()} fix, for
 * {@link BucketItem#emptyContents}: on vanilla/Fabric this method still hardcodes
 * {@code this.content == Fluids.WATER} before calling {@code container.placeLiquid(...)}, exactly
 * like {@code use()} does. NeoForge patches {@code emptyContents} differently (splits it into a 4-arg
 * wrapper and a new 5-arg overload that already generalizes past water — confirmed via {@code javap -c}
 * against {@code minecraft-merged-official-at-patched.jar} per {@code docs/NEOFORGE.md}'s procedure),
 * so this fix is Fabric-specific and registered only from {@code logallthethings.fabric.mixins.json} /
 * {@code fabric.mod.json} — never from {@code neoforge.mods.toml}.
 */
@Mixin(BucketItem.class)
public abstract class LATT$BucketItemFabricMixin {
    @Shadow
    @Final
    protected Fluid content;

    @ModifyExpressionValue(
        method = "emptyContents",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/Fluids;WATER:Lnet/minecraft/world/level/material/FlowingFluid;")
    )
    private FlowingFluid latt$allowLavaInEmptyContents(FlowingFluid original) {
        return this.content == Fluids.LAVA ? Fluids.LAVA : original;
    }
}
