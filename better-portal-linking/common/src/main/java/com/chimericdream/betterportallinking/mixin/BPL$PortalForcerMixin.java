package com.chimericdream.betterportallinking.mixin;

import com.chimericdream.betterportallinking.portal.EntryPortalContext;
import com.chimericdream.betterportallinking.portal.PortalAddress;
import com.chimericdream.betterportallinking.portal.PortalAddressLinker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Overrides vanilla's nearest-portal exit search with the address-linking winner, when the entry
 * portal recorded an address via {@link EntryPortalContext}.
 */
@Mixin(PortalForcer.class)
public abstract class BPL$PortalForcerMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "findClosestPortalPosition", at = @At("HEAD"), cancellable = true)
    private void bpl$overrideExitPortal(BlockPos approximateExitPos, boolean toNether, WorldBorder worldBorder, CallbackInfoReturnable<Optional<BlockPos>> cir) {
        // Take-once: always consume the context, even if it turns out to be empty or unused, so a
        // stale address can never leak into a later, unrelated search.
        PortalAddress entry = EntryPortalContext.take();
        if (entry.isEmpty()) {
            return;
        }

        PortalAddressLinker.findBestMatch(this.level, approximateExitPos, toNether, worldBorder, entry)
            .ifPresent(pos -> cir.setReturnValue(Optional.of(pos)));
    }
}
