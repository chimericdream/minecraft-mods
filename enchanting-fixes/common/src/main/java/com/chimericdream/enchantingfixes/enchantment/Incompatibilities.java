package com.chimericdream.enchantingfixes.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Set;

/**
 * Removes the incompatibilities between enchantments that work correctly together. Each enchantment has an
 * exclusive set, usually a tag. When the setting is on, the exclusive sets in {@link #REMOVED_SETS} do not
 * apply. The other sets still apply:
 * <ul>
 *     <li>{@code exclusive_set/mining}: with Silk Touch, the block drops itself, so Fortune has no effect.</li>
 *     <li>{@code exclusive_set/riptide}: a trident with Riptide cannot be thrown, so Loyalty and Channeling
 *     have no effect.</li>
 *     <li>Exclusive sets from other mods and data packs, which can have their own reasons.</li>
 * </ul>
 * An enchantment from another mod that is in one of the removed tags also becomes compatible.
 */
public final class Incompatibilities {
    public static final Set<String> REMOVED_SETS = Set.of(
        "minecraft:exclusive_set/armor",
        "minecraft:exclusive_set/boots",
        "minecraft:exclusive_set/bow",
        "minecraft:exclusive_set/crossbow",
        "minecraft:exclusive_set/damage"
    );

    private Incompatibilities() {
    }

    public static boolean areCompatible(boolean original, Holder<Enchantment> enchantment, Holder<Enchantment> other, boolean removeIncompatibilities) {
        if (original || !removeIncompatibilities || enchantment.equals(other)) {
            return original;
        }

        return !excludes(enchantment, other) && !excludes(other, enchantment);
    }

    private static boolean excludes(Holder<Enchantment> enchantment, Holder<Enchantment> other) {
        HolderSet<Enchantment> set = enchantment.value().exclusiveSet();
        return set.contains(other) && !isRemovedSet(set);
    }

    private static boolean isRemovedSet(HolderSet<Enchantment> set) {
        return set.unwrapKey().map(tag -> REMOVED_SETS.contains(tag.location().toString())).orElse(false);
    }
}
