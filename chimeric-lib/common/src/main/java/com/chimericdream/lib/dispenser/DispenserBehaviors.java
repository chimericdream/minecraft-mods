package com.chimericdream.lib.dispenser;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.Collection;

/**
 * Entry point for adding "dispenser uses an item on the block in front of it" behavior without
 * clobbering whatever the item already did — see {@link BlockTargetDispenseBehavior}.
 *
 * <p>Typical use:
 * <pre>{@code
 * DispenserBehaviors.wrap(Items.SHEARS, (level, target, state, stack, source) -> {
 *     if (!(state.getBlock() instanceof MyBlock)) return Result.PASS;
 *     MyBlock.doTheThing(level, target, state);
 *     DispenserBehaviors.damageWithoutPlayer(level, stack);
 *     return Result.SUCCESS;
 * });
 * }</pre>
 *
 * <h2>When to call {@code wrap}</h2>
 * {@link DispenserBlock#DISPENSER_REGISTRY} is a plain {@code IdentityHashMap}, and {@code wrap}
 * captures the item's <em>current</em> entry as its fallback, so it must run after vanilla's
 * bootstrap and from a single thread:
 * <ul>
 *     <li><b>Fabric:</b> from {@code ModInitializer#onInitialize}, after the mod's common
 *     {@code init()}.</li>
 *     <li><b>NeoForge:</b> from {@code FMLCommonSetupEvent}, inside {@code event.enqueueWork(...)} —
 *     common setup runs in parallel across mods, so calling it directly from the event can race
 *     another mod writing to the same map. Not from the mod constructor either: a mod's own
 *     {@code DeferredRegister} entries aren't resolvable there yet.</li>
 * </ul>
 * Any behavior registered for the item before the call is preserved as the fallback; a mod that
 * registers a plain (non-wrapping) behavior for the same item <em>afterwards</em> replaces this one,
 * which is an inherent limit of the vanilla registry.
 */
public final class DispenserBehaviors {
    private DispenserBehaviors() {
    }

    /**
     * Layers {@code handler} over {@code item}'s current dispenser behavior (or vanilla's per-stack
     * default when it has none) and registers the result. Wrapping the same item more than once
     * chains: the newest handler runs first and {@link BlockTargetDispenseBehavior.Result#PASS}es down
     * to the older ones.
     */
    public static BlockTargetDispenseBehavior wrap(ItemLike item, BlockTargetDispenseBehavior.Handler handler) {
        DispenseItemBehavior previous = DispenserBlock.DISPENSER_REGISTRY.get(item.asItem());
        BlockTargetDispenseBehavior behavior = new BlockTargetDispenseBehavior(handler, previous);
        DispenserBlock.registerBehavior(item, behavior);

        return behavior;
    }

    /** {@link #wrap(ItemLike, BlockTargetDispenseBehavior.Handler)} for each of {@code items}, sharing one handler. */
    public static void wrap(Collection<? extends ItemLike> items, BlockTargetDispenseBehavior.Handler handler) {
        for (ItemLike item : items) {
            wrap(item, handler);
        }
    }

    /**
     * Damages a tool used by a dispenser (no player to credit), the same way vanilla's shears
     * dispenser behavior does: one point of durability, honoring Unbreaking, and simply removing the
     * stack if it breaks.
     */
    public static void damageWithoutPlayer(ServerLevel level, ItemStack stack) {
        stack.hurtAndBreak(1, level, null, item -> {
        });
    }
}
