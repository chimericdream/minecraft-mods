package com.chimericdream.minekea.mixin;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * As of 26.1, {@link CauldronInteraction.Dispatcher#put(Item, CauldronInteraction)} is package-private,
 * so mods can no longer register their own item interactions on a dispatcher directly. This accessor
 * widens that method back to public so custom cauldron blocks (see HoneyCauldronBlock, MilkCauldronBlock)
 * can keep building their own {@link CauldronInteraction.Dispatcher} the same way vanilla does internally.
 */
@Mixin(CauldronInteraction.Dispatcher.class)
public interface CauldronDispatcherAccessor {
    @Invoker("put")
    void minekea$invokePut(Item item, CauldronInteraction interaction);
}
