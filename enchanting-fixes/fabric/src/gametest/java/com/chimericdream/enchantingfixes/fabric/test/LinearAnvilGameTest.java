package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.AnvilUpgrades;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.book;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchanted;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchantment;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.player;

/** The Linear Upgrades setting: the repair cost cap and one level from each item above the vanilla maximum. */
@SuppressWarnings("unused")
public class LinearAnvilGameTest {
    private static AnvilMenu combine(GameTestHelper context, ItemStack input, ItemStack addition) {
        AnvilMenu menu = new AnvilMenu(0, player(context).getInventory());
        menu.getSlot(0).set(input);
        menu.getSlot(1).set(addition);
        menu.createResult();
        return menu;
    }

    private static int sharpness(GameTestHelper context, AnvilMenu menu) {
        // A book keeps its enchantments in a different component.
        return EnchantmentHelper.getEnchantmentsForCrafting(menu.getSlot(2).getItem()).getLevel(enchantment(context, Enchantments.SHARPNESS));
    }

    private static void withConfig(boolean linear, Runnable test) {
        EnchantingFixesConfig config = EnchantingFixesConfig.CONFIG.instance();
        Map<String, Integer> maxLevels = config.maxLevels;
        boolean linearAnvilUpgrades = config.linearAnvilUpgrades;
        boolean removeAnvilLimit = config.removeAnvilLimit;
        config.maxLevels = Map.of("minecraft:sharpness", 100);
        config.linearAnvilUpgrades = linear;
        config.removeAnvilLimit = true;
        try {
            test.run();
        } finally {
            config.maxLevels = maxLevels;
            config.linearAnvilUpgrades = linearAnvilUpgrades;
            config.removeAnvilLimit = removeAnvilLimit;
        }
    }

    @GameTest
    public void aMaxLevelBookAddsOneLevel(GameTestHelper context) {
        withConfig(true, () -> {
            AnvilMenu menu = combine(context, enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 40), book(context, Enchantments.SHARPNESS, 5));
            context.assertValueEqual(sharpness(context, menu), 41, "Sharpness 40 + V");

            // Book onto book also works, in the two directions.
            AnvilMenu books = combine(context, book(context, Enchantments.SHARPNESS, 5), book(context, Enchantments.SHARPNESS, 12));
            context.assertValueEqual(sharpness(context, books), 13, "book V + book 12");
        });
        context.succeed();
    }

    @GameTest
    public void belowTheVanillaMaximumNothingChanges(GameTestHelper context) {
        withConfig(true, () -> {
            AnvilMenu lowBook = combine(context, enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 40), book(context, Enchantments.SHARPNESS, 4));
            context.assertValueEqual(sharpness(context, lowBook), 40, "Sharpness 40 + IV");

            AnvilMenu lowSword = combine(context, enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 3), book(context, Enchantments.SHARPNESS, 5));
            context.assertValueEqual(sharpness(context, lowSword), 5, "Sharpness III + V");

            AnvilMenu same = combine(context, enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 2), book(context, Enchantments.SHARPNESS, 2));
            context.assertValueEqual(sharpness(context, same), 3, "Sharpness II + II");
        });
        context.succeed();
    }

    @GameTest
    public void theRepairCostStopsAtTheCap(GameTestHelper context) {
        withConfig(true, () -> {
            ItemStack sword = enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 40);
            sword.set(DataComponents.REPAIR_COST, 1023);
            AnvilMenu menu = combine(context, sword, book(context, Enchantments.SHARPNESS, 5));

            // Sharpness costs 1 level a level from a book: 41, plus the capped repair cost of the sword.
            context.assertValueEqual(menu.getCost(), 41 + AnvilUpgrades.REPAIR_COST_CAP, "cost");
            context.assertValueEqual(menu.getSlot(2).getItem().get(DataComponents.REPAIR_COST), AnvilUpgrades.REPAIR_COST_CAP, "repair cost of the result");
        });
        context.succeed();
    }

    @GameTest
    public void theSettingIsOffByDefault(GameTestHelper context) {
        context.assertFalse(EnchantingFixesConfig.Defaults.LINEAR_ANVIL_UPGRADES, Component.literal("default"));
        withConfig(false, () -> {
            ItemStack sword = enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 40);
            sword.set(DataComponents.REPAIR_COST, 63);
            AnvilMenu menu = combine(context, sword, book(context, Enchantments.SHARPNESS, 5));

            context.assertValueEqual(sharpness(context, menu), 40, "vanilla keeps the higher level");
            context.assertValueEqual(menu.getCost(), 40 + 63, "vanilla cost");
            context.assertValueEqual(menu.getSlot(2).getItem().get(DataComponents.REPAIR_COST), 127, "vanilla repair cost");
        });
        context.succeed();
    }
}
