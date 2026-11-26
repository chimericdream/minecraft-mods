package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.book;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchanted;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchantment;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.player;

/** The Remove Incompatibilities setting. The exclusive sets are tags, so this needs the real registries. */
@SuppressWarnings("unused")
public class CompatibilityGameTest {
    private static void withSetting(boolean removeIncompatibilities, Runnable test) {
        EnchantingFixesConfig config = EnchantingFixesConfig.CONFIG.instance();
        boolean previous = config.removeIncompatibilities;
        config.removeIncompatibilities = removeIncompatibilities;
        try {
            test.run();
        } finally {
            config.removeIncompatibilities = previous;
        }
    }

    private static boolean compatible(GameTestHelper context, ResourceKey<Enchantment> a, ResourceKey<Enchantment> b) {
        return Enchantment.areCompatible(enchantment(context, a), enchantment(context, b));
    }

    private static void assertCompatible(GameTestHelper context, ResourceKey<Enchantment> a, ResourceKey<Enchantment> b, boolean expected) {
        String pair = a.identifier() + " + " + b.identifier();
        context.assertValueEqual(compatible(context, a, b), expected, pair);
        context.assertValueEqual(compatible(context, b, a), expected, pair + " (reversed)");
    }

    @GameTest
    public void removedSetsBecomeCompatible(GameTestHelper context) {
        withSetting(true, () -> {
            assertCompatible(context, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, true);
            assertCompatible(context, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, true);
            assertCompatible(context, Enchantments.DEPTH_STRIDER, Enchantments.FROST_WALKER, true);
            assertCompatible(context, Enchantments.INFINITY, Enchantments.MENDING, true);
            assertCompatible(context, Enchantments.MULTISHOT, Enchantments.PIERCING, true);
            assertCompatible(context, Enchantments.SHARPNESS, Enchantments.SMITE, true);
            assertCompatible(context, Enchantments.DENSITY, Enchantments.BREACH, true);
        });
        context.succeed();
    }

    @GameTest
    public void trulyExclusivePairsStayIncompatible(GameTestHelper context) {
        withSetting(true, () -> {
            assertCompatible(context, Enchantments.SILK_TOUCH, Enchantments.FORTUNE, false);
            assertCompatible(context, Enchantments.RIPTIDE, Enchantments.LOYALTY, false);
            assertCompatible(context, Enchantments.RIPTIDE, Enchantments.CHANNELING, false);
            assertCompatible(context, Enchantments.PROTECTION, Enchantments.PROTECTION, false);
        });
        context.succeed();
    }

    @GameTest
    public void theSettingIsOffByDefault(GameTestHelper context) {
        context.assertFalse(new EnchantingFixesConfig().removeIncompatibilities, Component.literal("the default should be off"));
        withSetting(false, () -> assertCompatible(context, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, false));
        context.succeed();
    }

    @GameTest
    public void theAnvilCombinesProtections(GameTestHelper context) {
        withSetting(true, () -> {
            AnvilMenu menu = new AnvilMenu(0, player(context).getInventory());
            menu.getSlot(0).set(enchanted(context, Items.DIAMOND_CHESTPLATE, Enchantments.PROTECTION, 4));
            menu.getSlot(1).set(book(context, Enchantments.FIRE_PROTECTION, 4));
            menu.createResult();

            ItemEnchantments result = menu.getSlot(2).getItem().getEnchantments();
            context.assertValueEqual(result.getLevel(enchantment(context, Enchantments.PROTECTION)), 4, "Protection");
            context.assertValueEqual(result.getLevel(enchantment(context, Enchantments.FIRE_PROTECTION)), 4, "Fire Protection");
        });
        context.succeed();
    }
}
