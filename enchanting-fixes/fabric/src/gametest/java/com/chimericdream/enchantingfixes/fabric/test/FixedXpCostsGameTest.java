package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.XpCosts;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.book;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchanted;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.player;

/** The Fixed XP Costs setting: a cost of N levels takes the XP from level 0 to level N. */
@SuppressWarnings("unused")
public class FixedXpCostsGameTest {
    private static void withConfig(boolean fixed, Runnable test) {
        EnchantingFixesConfig config = EnchantingFixesConfig.CONFIG.instance();
        boolean fixedXpCosts = config.fixedXpCosts;
        config.fixedXpCosts = fixed;
        try {
            test.run();
        } finally {
            config.fixedXpCosts = fixedXpCosts;
        }
    }

    private static ServerPlayer playerAtLevel(GameTestHelper context, int level) {
        ServerPlayer player = player(context);
        player.setExperienceLevels(level);
        player.setExperiencePoints(0);
        return player;
    }

    private static long totalPoints(ServerPlayer player) {
        return XpCosts.pointsToReach(player.experienceLevel) + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    /** Sharpness II + a Sharpness II book. Returns the cost in levels after the player takes the result. */
    private static int combineAndTake(GameTestHelper context, ServerPlayer player) {
        AnvilMenu menu = new AnvilMenu(0, player.getInventory());
        menu.getSlot(0).set(enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 2));
        menu.getSlot(1).set(book(context, Enchantments.SHARPNESS, 2));
        menu.createResult();

        int cost = menu.getCost();
        context.assertTrue(cost > 0, "the anvil has a cost");
        ItemStack result = menu.getSlot(2).getItem();
        context.assertTrue(menu.getSlot(2).mayPickup(player), "the player can take the result");
        menu.getSlot(2).onTake(player, result);
        return cost;
    }

    @GameTest
    public void theAnvilTakesThePointsOfTheCost(GameTestHelper context) {
        withConfig(true, () -> {
            ServerPlayer player = playerAtLevel(context, 100);
            long before = totalPoints(player);
            int cost = combineAndTake(context, player);

            context.assertValueEqual(totalPoints(player), before - XpCosts.pointsToReach(cost), "points after the anvil");
        });
        context.succeed();
    }

    @GameTest
    public void theEnchantingTableTakesThePointsOfTheCost(GameTestHelper context) {
        withConfig(true, () -> {
            ServerPlayer player = playerAtLevel(context, 30);
            int seed = player.getEnchantmentSeed();
            player.onEnchantmentPerformed(new ItemStack(Items.DIAMOND_SWORD), 3);

            // 3 levels is 27 points: level 30 (1,395 points) goes to level 29 and 80 points.
            context.assertValueEqual(player.experienceLevel, 29, "level");
            context.assertValueEqual(totalPoints(player), 1395L - 27, "points");
            context.assertTrue(player.getEnchantmentSeed() != seed, "the enchantment seed changes");
        });
        context.succeed();
    }

    @GameTest
    public void offRemovesLevels(GameTestHelper context) {
        withConfig(false, () -> {
            ServerPlayer tablePlayer = playerAtLevel(context, 100);
            tablePlayer.onEnchantmentPerformed(new ItemStack(Items.DIAMOND_SWORD), 3);
            context.assertValueEqual(tablePlayer.experienceLevel, 97, "enchanting table");

            ServerPlayer anvilPlayer = playerAtLevel(context, 100);
            int cost = combineAndTake(context, anvilPlayer);
            context.assertValueEqual(anvilPlayer.experienceLevel, 100 - cost, "anvil");
        });
        context.succeed();
    }
}
