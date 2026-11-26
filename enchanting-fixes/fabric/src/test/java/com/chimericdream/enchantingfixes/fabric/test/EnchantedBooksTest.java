package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.book.EnchantedBooks;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnchantedBooksTest extends BootstrapMinecraft {
    private static HolderGetter<Enchantment> enchantments;

    /** A registry that has no enchantments, as if a mod enchantment was removed. */
    private static final HolderGetter<Enchantment> NO_ENCHANTMENTS = new HolderGetter<>() {
        @Override
        public Optional<Holder.Reference<Enchantment>> get(ResourceKey<Enchantment> key) {
            return Optional.empty();
        }

        @Override
        public Optional<HolderSet.Named<Enchantment>> get(TagKey<Enchantment> tag) {
            return Optional.empty();
        }
    };

    @BeforeAll
    static void loadEnchantments() {
        enchantments = VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT);
    }

    private static ItemStack book(int repairCost, Object... enchantmentsAndLevels) {
        ItemEnchantments.Mutable stored = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (int i = 0; i < enchantmentsAndLevels.length; i += 2) {
            @SuppressWarnings("unchecked")
            ResourceKey<Enchantment> key = (ResourceKey<Enchantment>) enchantmentsAndLevels[i];
            stored.set(enchantments.getOrThrow(key), (int) enchantmentsAndLevels[i + 1]);
        }

        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        stack.set(DataComponents.STORED_ENCHANTMENTS, stored.toImmutable());
        if (repairCost != 0) {
            stack.set(DataComponents.REPAIR_COST, repairCost);
        }

        return stack;
    }

    @Test
    void fixedBooksStackWithNewBooks() {
        ItemStack cheap = book(1, Enchantments.MENDING, 1);
        ItemStack expensive = book(127, Enchantments.MENDING, 1);
        assertFalse(ItemStack.isSameItemSameComponents(cheap, expensive));

        assertTrue(EnchantedBooks.fixRepairCost(cheap, enchantments));
        assertTrue(EnchantedBooks.fixRepairCost(expensive, enchantments));

        ItemStack fresh = book(0, Enchantments.MENDING, 1);
        assertTrue(ItemStack.isSameItemSameComponents(cheap, expensive));
        assertTrue(ItemStack.isSameItemSameComponents(cheap, fresh));
        assertEquals(fresh.getComponentsPatch(), cheap.getComponentsPatch());
    }

    @Test
    void fixesLevelsAboveTheMaximum() {
        ItemStack stack = book(7, Enchantments.UNBREAKING, 5);

        assertTrue(EnchantedBooks.fixRepairCost(stack, enchantments));
        assertEquals(5, stack.get(DataComponents.STORED_ENCHANTMENTS).getLevel(enchantments.getOrThrow(Enchantments.UNBREAKING)));
    }

    @Test
    void fixesBooksWithMoreThanOneEnchantment() {
        ItemStack stack = book(3, Enchantments.EFFICIENCY, 3, Enchantments.UNBREAKING, 2);

        assertTrue(EnchantedBooks.fixRepairCost(stack, enchantments));
        assertTrue(ItemStack.isSameItemSameComponents(stack, book(0, Enchantments.EFFICIENCY, 3, Enchantments.UNBREAKING, 2)));
    }

    @Test
    void ignoresBooksWithoutARepairCost() {
        assertFalse(EnchantedBooks.fixRepairCost(book(0, Enchantments.MENDING, 1), enchantments));
    }

    @Test
    void ignoresBooksWithoutEnchantments() {
        assertFalse(EnchantedBooks.fixRepairCost(book(7), enchantments));
    }

    @Test
    void ignoresBooksWithUnknownEnchantments() {
        ItemStack stack = book(7, Enchantments.MENDING, 1);

        assertFalse(EnchantedBooks.fixRepairCost(stack, NO_ENCHANTMENTS));
        assertEquals(7, stack.get(DataComponents.REPAIR_COST));
    }

    @Test
    void ignoresBooksWithOtherComponents() {
        ItemStack named = book(7, Enchantments.MENDING, 1);
        named.set(DataComponents.CUSTOM_NAME, Component.literal("Quest Book"));
        assertFalse(EnchantedBooks.fixRepairCost(named, enchantments));

        ItemStack custom = book(7, Enchantments.MENDING, 1);
        custom.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY.update(tag -> tag.putString("quest", "dragon")));
        assertFalse(EnchantedBooks.fixRepairCost(custom, enchantments));
    }

    @Test
    void ignoresOtherItems() {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(enchantments.getOrThrow(Enchantments.SHARPNESS), 5);
        sword.set(DataComponents.REPAIR_COST, 7);

        assertFalse(EnchantedBooks.fixRepairCost(sword, enchantments));
        assertEquals(7, sword.get(DataComponents.REPAIR_COST));
    }
}
