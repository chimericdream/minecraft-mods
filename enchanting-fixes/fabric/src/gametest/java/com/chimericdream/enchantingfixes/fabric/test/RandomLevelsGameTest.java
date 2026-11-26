package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.RandomLevels.Mode;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchantment;

/**
 * The Random Enchantment Levels setting, with Sharpness at a configured maximum of 50. Vanilla Sharpness has a
 * maximum of V.
 */
@SuppressWarnings("unused")
public class RandomLevelsGameTest {
    private static final int ROLLS = 500;

    private static void withMode(Mode mode, Runnable test) {
        EnchantingFixesConfig config = EnchantingFixesConfig.CONFIG.instance();
        Map<String, Integer> maxLevels = config.maxLevels;
        Mode previous = config.randomLevels;
        config.maxLevels = Map.of("minecraft:sharpness", 50);
        config.randomLevels = mode;
        try {
            test.run();
        } finally {
            config.maxLevels = maxLevels;
            config.randomLevels = previous;
        }
    }

    /** The highest Sharpness level from {@value #ROLLS} rolls of {@code enchant_randomly}. */
    private static int highestLootLevel(GameTestHelper context) {
        Holder<Enchantment> sharpness = enchantment(context, Enchantments.SHARPNESS);
        LootItemFunction function = EnchantRandomlyFunction.randomEnchantment().withEnchantment(sharpness).build();
        LootContext lootContext = new LootContext.Builder(new LootParams.Builder(context.getLevel()).create(LootContextParamSets.EMPTY))
            .create(Optional.empty());

        int highest = 0;
        for (int i = 0; i < ROLLS; i++) {
            ItemStack stack = function.apply(new ItemStack(Items.DIAMOND_SWORD), lootContext);
            highest = Math.max(highest, stack.getEnchantments().getLevel(sharpness));
        }

        return highest;
    }

    /**
     * The Sharpness level that the enchanting table and {@code enchant_with_levels} pick at an enchanting power.
     * Each Sharpness level has a cost range from 1 + 11 * (level - 1) to 21 + 11 * (level - 1). The game picks the
     * highest level up to the cap whose range contains the power.
     */
    private static int costBasedLevel(GameTestHelper context, int power) {
        Holder<Enchantment> sharpness = enchantment(context, Enchantments.SHARPNESS);
        List<EnchantmentInstance> results = EnchantmentHelper.getAvailableEnchantmentResults(power, new ItemStack(Items.DIAMOND_SWORD), Stream.of(sharpness));
        return results.isEmpty() ? 0 : results.getFirst().level();
    }

    @GameTest
    public void offUsesTheVanillaMaximum(GameTestHelper context) {
        withMode(Mode.OFF, () -> {
            context.assertValueEqual(highestLootLevel(context), 5, Component.literal("highest loot level"));
            // Without the cap, a power of 60 gives level VI (56 to 76).
            context.assertValueEqual(costBasedLevel(context, 60), 5, Component.literal("cost-based level"));
        });
        context.succeed();
    }

    @GameTest
    public void balancedAllowsTwoLevelsAboveVanilla(GameTestHelper context) {
        withMode(Mode.BALANCED, () -> {
            context.assertValueEqual(highestLootLevel(context), 7, Component.literal("highest loot level"));
            // Without the cap, a power of 80 gives level VIII (78 to 98).
            context.assertValueEqual(costBasedLevel(context, 80), 7, Component.literal("cost-based level"));
        });
        context.succeed();
    }

    @GameTest
    public void insaneUsesTheConfiguredMaximum(GameTestHelper context) {
        withMode(Mode.INSANE, () -> {
            int highest = highestLootLevel(context);
            context.assertTrue(highest > 7 && highest <= 50, Component.literal("highest loot level " + highest + " should be from VIII to L"));
            // Level XIX costs from 199 to 219.
            context.assertValueEqual(costBasedLevel(context, 200), 19, Component.literal("cost-based level"));
        });
        context.succeed();
    }
}
