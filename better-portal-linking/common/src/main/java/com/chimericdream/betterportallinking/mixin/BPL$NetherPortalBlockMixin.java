package com.chimericdream.betterportallinking.mixin;

import com.chimericdream.betterportallinking.BetterPortalLinkingMod;
import com.chimericdream.betterportallinking.config.BetterPortalLinkingConfig;
import com.chimericdream.betterportallinking.portal.EntryPortalContext;
import com.chimericdream.betterportallinking.portal.PortalAddress;
import com.chimericdream.betterportallinking.portal.PortalAddressLinker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Threads the entry portal's {@link PortalAddress} into
 * {@code PortalForcer#findClosestPortalPosition} via {@link EntryPortalContext}, without changing
 * either method's signature.
 *
 * <p>{@code level} here is the CURRENT (entry) level and {@code pos} is the entry portal block
 * position — {@code getPortalDestination} is called before the current-to-new dimension switch.
 */
@Mixin(NetherPortalBlock.class)
public abstract class BPL$NetherPortalBlockMixin {
    /**
     * Records the entry portal's address for the exit search that is about to happen.
     *
     * <p>Every branch here writes the context — setting an empty address, or clearing it — rather
     * than leaving a previous value in place. That unconditional write is what makes vanilla parity
     * safe: the {@code RETURN} injector below does not run if another mod cancels
     * {@code getPortalDestination} at HEAD, or if the call throws, so a previous transit's address
     * can survive past its own teleport. Overwriting on entry means the worst such a leftover can do
     * is live until the next portal transit reads it — and that read now always sees this transit's
     * own value.
     */
    @Inject(method = "getPortalDestination", at = @At("HEAD"))
    private void bpl$setEntryAddress(ServerLevel level, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTransition> cir) {
        if (!BetterPortalLinkingConfig.HANDLER.instance().enableAddressLinking) {
            EntryPortalContext.clear();
            return;
        }

        PortalAddress address = PortalAddressLinker.addressOf(level, pos);
        EntryPortalContext.set(address);

        if (address.isEmpty() && BetterPortalLinkingConfig.HANDLER.instance().logLinkingDecisions) {
            BetterPortalLinkingMod.LOGGER.info(
                "Entry portal at {} has no address blocks on its frame corners; using vanilla portal linking.", pos
            );
        }
    }

    // Defensive clear on the way out. The exit search's own take() normally consumes the context
    // first; this covers the case where the search is never reached (vanilla can return early).
    @Inject(method = "getPortalDestination", at = @At("RETURN"))
    private void bpl$clearEntryAddress(ServerLevel level, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTransition> cir) {
        EntryPortalContext.clear();
    }
}
