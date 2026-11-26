package com.chimericdream.enchantingfixes.enchantment;

import com.google.common.collect.MapMaker;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Finds the registry ID of an {@link Enchantment} instance. An enchantment is a data-driven record and
 * does not know its own ID, so {@code HolderReferenceMixin} records the ID when the registry binds the
 * value to its holder.
 *
 * <p>The keys use identity, not {@code equals}: two enchantments with the same definition are different
 * entries. The keys are weak, so the old enchantments go away when the game loads a new registry, for
 * example when the player joins a different world.
 */
public final class EnchantmentIds {
    private static final Map<Enchantment, String> IDS = new MapMaker().weakKeys().makeMap();

    private EnchantmentIds() {
    }

    public static void record(Enchantment enchantment, String id) {
        IDS.put(enchantment, id);
    }

    public static @Nullable String get(Enchantment enchantment) {
        return IDS.get(enchantment);
    }
}
