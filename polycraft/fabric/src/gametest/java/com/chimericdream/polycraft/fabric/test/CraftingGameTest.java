package com.chimericdream.polycraft.fabric.test;

import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.network.SelectRecipePayload;
import com.chimericdream.polycraft.recipe.ConflictService.Offer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

/**
 * M3: the crafting table. Fixture recipes (see {@code CommandGameTest}): two cactus make 3 green dye (a), 5 green dye
 * (b), or 2 lime dye (c, excluded by the rules file), and the rules prefer b. Two sweet berries make 2 red dye (a) or
 * 4 red dye (b) with no rule, so vanilla's first recipe (a) is the default there.
 */
@SuppressWarnings("unused")
public class CraftingGameTest {
    private static final String CACTUS_A = "polycraft_test:two_cactus_a";

    /** Menu ids only need to be distinct per test run, so count up from a number no real menu would use here. */
    private static int nextContainerId = 100;

    private static ServerPlayer player(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayerInLevel();
    }

    /** Opens a crafting table for {@code player}, the same way the game does, and makes it their open menu. */
    private static CraftingMenu open(GameTestHelper context, ServerPlayer player) {
        CraftingMenu menu = new CraftingMenu(nextContainerId++, player.getInventory(), ContainerLevelAccess.create(context.getLevel(), BlockPos.ZERO));
        player.containerMenu = menu;
        return menu;
    }

    /** Puts {@code count} of {@code item} into grid slots 1 and 2 (the first two cells). */
    private static void placeTwo(CraftingMenu menu, Item item, int count) {
        menu.getSlot(1).set(new ItemStack(item, count));
        menu.getSlot(2).set(new ItemStack(item, count));
    }

    private static ItemStack result(CraftingMenu menu) {
        return menu.getSlot(0).getItem();
    }

    // 26.2 keeps the dyes in a color collection rather than one constant each, so compare by item id.
    private static String idOf(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static boolean is(ItemStack stack, String id, int count) {
        return idOf(stack).equals(id) && stack.getCount() == count;
    }

    @GameTest
    public void withNoRuleVanillaWins(GameTestHelper context) {
        CraftingMenu menu = open(context, player(context));
        placeTwo(menu, Items.SWEET_BERRIES, 1);

        context.assertTrue(is(result(menu), "minecraft:red_dye", 2), "with no preference the first recipe should win, as in vanilla");
        context.assertTrue(PolyCraftNetworking.offered(menu).map(offer -> offer.options().size()).orElse(0) == 2, "the player should be offered both recipes");
        context.succeed();
    }

    @GameTest
    public void aPackDefaultBeatsVanilla(GameTestHelper context) {
        CraftingMenu menu = open(context, player(context));
        placeTwo(menu, Items.CACTUS, 1);

        context.assertTrue(is(result(menu), "minecraft:green_dye", 5), "the rules file prefers recipe b (5 green dye)");
        context.succeed();
    }

    @GameTest
    public void aPlayerChoiceBeatsThePackDefaultAndIsRemembered(GameTestHelper context) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        CraftingMenu menu = open(context, player);
        placeTwo(menu, Items.CACTUS, 1);
        Offer offer = PolyCraftNetworking.offered(menu).orElseThrow();

        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, offer.groupKey(), CACTUS_A));
        context.assertTrue(is(result(menu), "minecraft:green_dye", 3), "choosing recipe a should switch the result to 3 green dye");

        // Clear the grid and start again in a brand-new menu: the choice is remembered, not just for this menu.
        menu.getSlot(1).set(ItemStack.EMPTY);
        menu.getSlot(2).set(ItemStack.EMPTY);
        CraftingMenu again = open(context, player);
        placeTwo(again, Items.CACTUS, 1);
        context.assertTrue(is(result(again), "minecraft:green_dye", 3), "the choice should still apply in a new menu");

        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    @GameTest
    public void aChoiceBelongsToOnePlayer(GameTestHelper context) {
        ServerPlayer alex = player(context);
        ServerPlayer sam = player(context);
        PolyCraftChoices choices = PolyCraftChoices.get(context.getLevel().getServer());
        choices.reset(alex.getUUID());
        choices.reset(sam.getUUID());

        CraftingMenu alexMenu = open(context, alex);
        placeTwo(alexMenu, Items.CACTUS, 1);
        Offer offer = PolyCraftNetworking.offered(alexMenu).orElseThrow();
        PolyCraftNetworking.applySelection(alex, new SelectRecipePayload(alexMenu.containerId, offer.groupKey(), CACTUS_A));

        CraftingMenu samMenu = open(context, sam);
        placeTwo(samMenu, Items.CACTUS, 1);

        context.assertTrue(is(result(alexMenu), "minecraft:green_dye", 3), "Alex should get the recipe Alex chose");
        context.assertTrue(is(result(samMenu), "minecraft:green_dye", 5), "Sam should still get the pack default");
        choices.reset(alex.getUUID());
        context.succeed();
    }

    @GameTest
    public void forgedSelectionsAreIgnored(GameTestHelper context) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        CraftingMenu menu = open(context, player);
        placeTwo(menu, Items.CACTUS, 1);
        Offer offer = PolyCraftNetworking.offered(menu).orElseThrow();

        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, offer.groupKey(), "minecraft:stick"));
        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, offer.groupKey(), "polycraft_test:two_cactus_c"));
        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, "not|a|group", CACTUS_A));
        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId + 1, offer.groupKey(), CACTUS_A));

        context.assertTrue(is(result(menu), "minecraft:green_dye", 5), "none of the forged selections should change the result");
        context.assertTrue(PolyCraftChoices.get(context.getLevel().getServer()).choiceFor(player.getUUID(), offer.groupKey()).isEmpty(), "nothing should have been saved");
        context.succeed();
    }

    @GameTest
    public void excludedAndDuplicateRecipesAreNotOffered(GameTestHelper context) {
        CraftingMenu menu = open(context, player(context));
        placeTwo(menu, Items.CACTUS, 1);

        List<String> ids = PolyCraftNetworking.offered(menu).orElseThrow().options().stream().map(option -> option.id()).toList();
        context.assertTrue(ids.equals(List.of(CACTUS_A, "polycraft_test:two_cactus_b")), "expected only a and b, got " + ids);
        context.succeed();
    }

    @GameTest
    public void aRecipeWithNoRivalMakesNoOffer(GameTestHelper context) {
        CraftingMenu menu = open(context, player(context));
        // A 2x2 square of planks: grid slots 1, 2, 4 and 5 (the grid is three wide).
        for (int slot : new int[]{1, 2, 4, 5}) {
            menu.getSlot(slot).set(new ItemStack(Items.OAK_PLANKS));
        }

        context.assertTrue(result(menu).is(Items.CRAFTING_TABLE), "four planks should still make a crafting table");
        context.assertTrue(PolyCraftNetworking.offered(menu).equals(Optional.empty()), "a lone recipe is not a choice");
        context.succeed();
    }

    @GameTest
    public void takingTheResultUsesUpTheInputs(GameTestHelper context) {
        ServerPlayer player = player(context);
        CraftingMenu menu = open(context, player);
        placeTwo(menu, Items.CACTUS, 3);

        menu.clicked(0, 0, ContainerInput.PICKUP, player);

        context.assertTrue(is(menu.getCarried(), "minecraft:green_dye", 5), "the player should be holding the chosen recipe's result");
        context.assertTrue(menu.getSlot(1).getItem().getCount() == 2 && menu.getSlot(2).getItem().getCount() == 2, "one of each ingredient should be used up");
        context.succeed();
    }

    @GameTest
    public void shiftClickCraftsEverythingWithTheChosenRecipe(GameTestHelper context) {
        ServerPlayer player = player(context);
        CraftingMenu menu = open(context, player);
        placeTwo(menu, Items.CACTUS, 10);

        menu.clicked(0, 0, ContainerInput.QUICK_MOVE, player);

        int dye = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (idOf(stack).equals("minecraft:green_dye")) {
                dye += stack.getCount();
            }
        }

        context.assertTrue(dye == 50, "ten crafts at 5 green dye each should give 50, got " + dye);
        context.succeed();
    }
}
