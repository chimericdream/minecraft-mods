package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.book;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchanted;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchantment;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.player;

/** The anvil limit of 40 levels: removed by default, kept for stacks, and back when the setting is off. */
@SuppressWarnings("unused")
public class AnvilGameTest {
    /** Sharpness 45 + a Sharpness 45 book gives Sharpness 46. From a book, Sharpness costs 1 level a level. */
    private static AnvilMenu combine(GameTestHelper context, ServerPlayer player, ItemStack input) {
        AnvilMenu menu = new AnvilMenu(0, player.getInventory());
        menu.getSlot(0).set(input);
        menu.getSlot(1).set(book(context, Enchantments.SHARPNESS, 45));
        menu.createResult();
        return menu;
    }

    private static ItemStack stackableBook(GameTestHelper context, int count) {
        ItemStack stack = book(context, Enchantments.SHARPNESS, 45);
        stack.set(DataComponents.MAX_STACK_SIZE, 64);
        stack.setCount(count);
        return stack;
    }

    private static void withConfig(boolean removeLimit, Runnable test) {
        EnchantingFixesConfig config = EnchantingFixesConfig.CONFIG.instance();
        Map<String, Integer> maxLevels = config.maxLevels;
        boolean removeAnvilLimit = config.removeAnvilLimit;
        config.maxLevels = Map.of("minecraft:sharpness", 50);
        config.removeAnvilLimit = removeLimit;
        try {
            test.run();
        } finally {
            config.maxLevels = maxLevels;
            config.removeAnvilLimit = removeAnvilLimit;
        }
    }

    @GameTest
    public void costsAbove40GiveAResult(GameTestHelper context) {
        withConfig(true, () -> {
            AnvilMenu menu = combine(context, player(context), enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 45));
            ItemStack result = menu.getSlot(2).getItem();

            context.assertTrue(menu.getCost() >= 40, Component.literal("cost " + menu.getCost() + " should be 40 or more"));
            context.assertValueEqual(result.getEnchantments().getLevel(enchantment(context, Enchantments.SHARPNESS)), 46, "result level");
        });
        context.succeed();
    }

    @GameTest
    public void stacksKeepTheLimit(GameTestHelper context) {
        withConfig(true, () -> {
            // Enchantable items do not stack in vanilla, so this uses enchanted books with a larger stack size.
            // One book gets a result, so the stack fails only because of the limit.
            AnvilMenu single = combine(context, player(context), stackableBook(context, 1));
            context.assertFalse(single.getSlot(2).getItem().isEmpty(), Component.literal("one book should get a result"));

            AnvilMenu stack = combine(context, player(context), stackableBook(context, 2));
            context.assertTrue(stack.getSlot(2).getItem().isEmpty(), Component.literal("a stack should get no result"));
        });
        context.succeed();
    }

    @GameTest
    public void theSettingRestoresTheLimit(GameTestHelper context) {
        withConfig(false, () -> {
            AnvilMenu menu = combine(context, player(context), enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 45));

            context.assertTrue(menu.getSlot(2).getItem().isEmpty(), Component.literal("the limit should apply"));
        });
        context.succeed();
    }
}
