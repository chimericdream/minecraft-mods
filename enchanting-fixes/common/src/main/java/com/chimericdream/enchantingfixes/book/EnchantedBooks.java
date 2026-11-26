package com.chimericdream.enchantingfixes.book;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Objects;
import java.util.Set;

public final class EnchantedBooks {
    /**
     * The only components that a plain enchanted book changes. Mods and data packs can use the enchanted book item for
     * other purposes. These books usually have a name, lore, a model, or custom data, so the fix does not touch them.
     */
    private static final Set<DataComponentType<?>> PLAIN_BOOK_COMPONENTS = Set.of(
        DataComponents.STORED_ENCHANTMENTS,
        DataComponents.REPAIR_COST
    );

    private EnchantedBooks() {
    }

    /**
     * Removes the repair cost from a plain enchanted book. An anvil or a grindstone can add a repair cost to a book. Two
     * books with the same enchantments but different repair costs do not stack. A book is fixed only if:
     * <ul>
     * <li>it has a repair cost,</li>
     * <li>it stores one or more enchantments, and each enchantment is in the enchantment registry, and</li>
     * <li>it has no other changed components (see {@link #PLAIN_BOOK_COMPONENTS}).</li>
     * </ul>
     *
     * @param enchantments the enchantment registry of the world
     * @return {@code true} if the book changed
     */
    public static boolean fixRepairCost(ItemStack stack, HolderGetter<Enchantment> enchantments) {
        if (!stack.is(Items.ENCHANTED_BOOK)) {
            return false;
        }

        Integer defaultCost = stack.getPrototype().get(DataComponents.REPAIR_COST);
        if (Objects.equals(stack.get(DataComponents.REPAIR_COST), defaultCost)) {
            return false;
        }

        for (var entry : stack.getComponentsPatch().entrySet()) {
            if (!PLAIN_BOOK_COMPONENTS.contains(entry.getKey())) {
                return false;
            }
        }

        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.isEmpty()) {
            return false;
        }

        for (Holder<Enchantment> enchantment : stored.keySet()) {
            if (enchantment.unwrapKey().flatMap(enchantments::get).isEmpty()) {
                return false;
            }
        }

        // The default value removes the component from the patch, so the book becomes equal to a new book.
        stack.set(DataComponents.REPAIR_COST, defaultCost);
        return true;
    }
}
