package com.chimericdream.polycraft.fabric.test;

import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.network.SelectRecipePayload;
import com.chimericdream.polycraft.recipe.ConflictService.Offer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Recipes whose ingredients leave something behind when crafted. Taking the crafting result looks the recipe up again to
 * work out what stays in the grid ({@code ResultSlot.getRemainingItems}), so these check that the leftovers are put back
 * correctly whichever conflicting recipe the player chose.
 * <p>
 * A honey bottle leaves a glass bottle and a water or lava bucket leaves a bucket. A water bottle does not leave
 * anything in 26.2 (it only turns into a glass bottle when drunk), so honey bottles stand in for it. Fixture recipes
 * (see {@code CommandGameTest}): a 2x2 square of honey bottles makes honey block (vanilla), diamond or sponge, and a
 * water bucket with a lava bucket makes obsidian or 4 cobblestone.
 */
@SuppressWarnings("unused")
public class RemainderGameTest {
    private static final String HONEY_DIAMOND = "polycraft_test:honey_square_diamond";
    private static final String HONEY_SPONGE = "polycraft_test:honey_square_sponge";
    private static final String HONEY_BLOCK = "minecraft:honey_block";
    private static final String BUCKETS_OBSIDIAN = "polycraft_test:bucket_pair_obsidian";
    private static final String BUCKETS_COBBLESTONE = "polycraft_test:bucket_pair_cobblestone";

    /** The four cells of a 2x2 square in a three-wide grid; slot 0 is the result. */
    private static final int[] SQUARE = {1, 2, 4, 5};

    private static int nextContainerId = 300;

    private static ServerPlayer player(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayerInLevel();
    }

    private static CraftingMenu open(GameTestHelper context, ServerPlayer player) {
        CraftingMenu menu = new CraftingMenu(nextContainerId++, player.getInventory(), ContainerLevelAccess.create(context.getLevel(), BlockPos.ZERO));
        player.containerMenu = menu;
        return menu;
    }

    private static String idOf(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static boolean is(ItemStack stack, Item item, int count) {
        return stack.is(item) && stack.getCount() == count;
    }

    private static void fillSquare(CraftingMenu menu, Item item, int count) {
        for (int slot : SQUARE) {
            menu.getSlot(slot).set(new ItemStack(item, count));
        }
    }

    private static void choose(GameTestHelper context, ServerPlayer player, CraftingMenu menu, String recipeId) {
        Offer offer = PolyCraftNetworking.offered(menu).orElseThrow();
        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, offer.groupKey(), recipeId));
    }

    /** Fills a honey-bottle square, chooses {@code recipeId}, takes the result, and checks the glass bottles left behind. */
    private static void honeySquare(GameTestHelper context, String recipeId, Item expected) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        CraftingMenu menu = open(context, player);
        fillSquare(menu, Items.HONEY_BOTTLE, 1);

        choose(context, player, menu, recipeId);
        context.assertTrue(menu.getSlot(0).getItem().is(expected), recipeId + " should show " + idOf(new ItemStack(expected)) + " in the result slot");

        menu.clicked(0, 0, ContainerInput.PICKUP, player);

        context.assertTrue(is(menu.getCarried(), expected, 1), "the player should be holding " + idOf(new ItemStack(expected)) + ", got " + idOf(menu.getCarried()));
        for (int slot : SQUARE) {
            context.assertTrue(is(menu.getSlot(slot).getItem(), Items.GLASS_BOTTLE, 1), "grid slot " + slot + " should hold the empty bottle, got " + idOf(menu.getSlot(slot).getItem()));
        }

        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    @GameTest
    public void chosenRecipeLeavesBottlesDiamond(GameTestHelper context) {
        honeySquare(context, HONEY_DIAMOND, Items.DIAMOND);
    }

    @GameTest
    public void chosenRecipeLeavesBottlesSponge(GameTestHelper context) {
        honeySquare(context, HONEY_SPONGE, Items.SPONGE);
    }

    @GameTest
    public void vanillaRecipeStillLeavesBottles(GameTestHelper context) {
        honeySquare(context, HONEY_BLOCK, Items.HONEY_BLOCK);
    }

    @GameTest
    public void honeySquareOffersVanillaAndFixtureRecipes(GameTestHelper context) {
        CraftingMenu menu = open(context, player(context));
        fillSquare(menu, Items.HONEY_BOTTLE, 1);

        var ids = PolyCraftNetworking.offered(menu).orElseThrow().options().stream().map(option -> option.id()).toList();
        context.assertTrue(ids.containsAll(java.util.List.of(HONEY_BLOCK, HONEY_DIAMOND, HONEY_SPONGE)), "expected honey block, diamond and sponge, got " + ids);
        context.succeed();
    }

    @GameTest
    public void bottlesGoToTheInventoryWhileTheStackLasts(GameTestHelper context) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        CraftingMenu menu = open(context, player);
        // Two bottles per cell, so after one craft each cell still holds a honey bottle and the glass bottle can't go there.
        for (int slot : SQUARE) {
            menu.getSlot(slot).set(new ItemStack(Items.HONEY_BOTTLE, 2));
        }

        choose(context, player, menu, HONEY_DIAMOND);
        menu.clicked(0, 0, ContainerInput.PICKUP, player);

        for (int slot : SQUARE) {
            context.assertTrue(is(menu.getSlot(slot).getItem(), Items.HONEY_BOTTLE, 1), "grid slot " + slot + " should have one honey bottle left");
        }

        int bottles = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.GLASS_BOTTLE)) {
                bottles += stack.getCount();
            }
        }

        context.assertTrue(bottles == 4, "the four empty bottles should be in the player's inventory, got " + bottles);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    private static void buckets(GameTestHelper context, String recipeId, Item expected, int expectedCount) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        CraftingMenu menu = open(context, player);
        menu.getSlot(1).set(new ItemStack(Items.WATER_BUCKET));
        menu.getSlot(2).set(new ItemStack(Items.LAVA_BUCKET));

        choose(context, player, menu, recipeId);
        context.assertTrue(is(menu.getSlot(0).getItem(), expected, expectedCount), recipeId + " should show " + expectedCount + " " + idOf(new ItemStack(expected)));

        menu.clicked(0, 0, ContainerInput.PICKUP, player);

        context.assertTrue(is(menu.getCarried(), expected, expectedCount), "the player should be holding the chosen result, got " + idOf(menu.getCarried()));
        context.assertTrue(is(menu.getSlot(1).getItem(), Items.BUCKET, 1), "the water bucket's cell should hold an empty bucket, got " + idOf(menu.getSlot(1).getItem()));
        context.assertTrue(is(menu.getSlot(2).getItem(), Items.BUCKET, 1), "the lava bucket's cell should hold an empty bucket, got " + idOf(menu.getSlot(2).getItem()));

        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    @GameTest
    public void waterAndLavaBucketsLeaveBucketsObsidian(GameTestHelper context) {
        buckets(context, BUCKETS_OBSIDIAN, Items.OBSIDIAN, 1);
    }

    /** A stack of {@code count} that the item's own limit wouldn't allow, the way a modpack that raises stack sizes does. */
    private static ItemStack bigStack(Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        stack.set(DataComponents.MAX_STACK_SIZE, count);
        return stack;
    }

    @GameTest
    public void stackedBucketsSendEmptyBucketsToTheInventory(GameTestHelper context) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        CraftingMenu menu = open(context, player);
        menu.getSlot(1).set(bigStack(Items.WATER_BUCKET, 8));
        menu.getSlot(2).set(bigStack(Items.LAVA_BUCKET, 8));

        choose(context, player, menu, BUCKETS_OBSIDIAN);
        menu.clicked(0, 0, ContainerInput.PICKUP, player);

        context.assertTrue(is(menu.getCarried(), Items.OBSIDIAN, 1), "the player should be holding the chosen result, got " + idOf(menu.getCarried()));
        context.assertTrue(is(menu.getSlot(1).getItem(), Items.WATER_BUCKET, 7), "seven water buckets should be left in the grid");
        context.assertTrue(is(menu.getSlot(2).getItem(), Items.LAVA_BUCKET, 7), "seven lava buckets should be left in the grid");

        int empty = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.BUCKET)) {
                empty += stack.getCount();
            }
        }

        context.assertTrue(empty == 2, "the two empty buckets should be in the player's inventory, got " + empty);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    @GameTest
    public void waterAndLavaBucketsLeaveBucketsCobblestone(GameTestHelper context) {
        buckets(context, BUCKETS_COBBLESTONE, Items.COBBLESTONE, 4);
    }
}
