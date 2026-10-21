package com.chimericdream.polycraft.fabric.test;

import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.network.SelectRecipePayload;
import com.chimericdream.polycraft.network.SetPinPayload;
import com.chimericdream.polycraft.recipe.ConflictService.Offer;
import com.chimericdream.polycraft.recipe.PinHolder;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

/**
 * M5: the smithing table, the Crafter, the campfire and the player's own 2x2 grid, plus a check that the stonecutter is
 * left alone. Fixtures: two smithing recipes for stick + flint (torch, or bowl), two campfire recipes for an egg (bone
 * meal, or paper, with paper preferred by the rules file), and the cactus recipes from the crafting tests.
 */
@SuppressWarnings("unused")
public class StationsGameTest {
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final String CACTUS_A = "polycraft_test:two_cactus_a";

    private static int nextContainerId = 300;

    private static ServerPlayer player(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayerInLevel();
    }

    private static String idOf(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static boolean is(ItemStack stack, String id, int count) {
        return idOf(stack).equals(id) && stack.getCount() == count;
    }

    // ---- smithing table ----

    private static SmithingMenu openSmithing(GameTestHelper context, ServerPlayer player) {
        SmithingMenu menu = new SmithingMenu(nextContainerId++, player.getInventory(), ContainerLevelAccess.create(context.getLevel(), BlockPos.ZERO));
        player.containerMenu = menu;
        return menu;
    }

    @GameTest
    public void smithingTableOffersAndAppliesAChoice(GameTestHelper context) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        SmithingMenu menu = openSmithing(context, player);
        menu.getSlot(1).set(new ItemStack(Items.STICK));
        menu.getSlot(2).set(new ItemStack(Items.FLINT));

        context.assertTrue(is(menu.getSlot(3).getItem(), "minecraft:torch", 1), "vanilla's first recipe makes a torch");
        Offer offer = PolyCraftNetworking.offered(menu).orElseThrow();
        context.assertTrue(offer.options().size() == 2 && !offer.pinnable(), "two options, and no pin on a smithing table");

        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, offer.groupKey(), "polycraft_test:smithing_b"));
        context.assertTrue(is(menu.getSlot(3).getItem(), "minecraft:bowl", 1), "choosing the second recipe should make a bowl");

        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    // ---- the player's own 2x2 grid ----

    @GameTest
    public void theInventoryGridIsCoveredToo(GameTestHelper context) {
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        var menu = player.inventoryMenu;
        player.containerMenu = menu;
        menu.getSlot(1).set(new ItemStack(Items.CACTUS));
        menu.getSlot(2).set(new ItemStack(Items.CACTUS));

        context.assertTrue(is(menu.getSlot(0).getItem(), "minecraft:green_dye", 5), "the 2x2 grid should use the pack default");
        Offer offer = PolyCraftNetworking.offered(menu).orElseThrow();
        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, offer.groupKey(), CACTUS_A));
        context.assertTrue(is(menu.getSlot(0).getItem(), "minecraft:green_dye", 3), "choosing recipe a should give 3 green dye");

        menu.getSlot(1).set(ItemStack.EMPTY);
        menu.getSlot(2).set(ItemStack.EMPTY);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    // ---- stonecutter ----

    @GameTest
    public void theStonecutterIsLeftAlone(GameTestHelper context) {
        ServerPlayer player = player(context);
        StonecutterMenu menu = new StonecutterMenu(nextContainerId++, player.getInventory(), ContainerLevelAccess.create(context.getLevel(), BlockPos.ZERO));
        player.containerMenu = menu;
        menu.getSlot(0).set(new ItemStack(Items.STONE, 4));

        context.assertTrue(PolyCraftNetworking.offered(menu).isEmpty(), "the stonecutter is already a picker, so PolyCraft offers nothing there");
        context.succeed();
    }

    // ---- Crafter ----

    private static CrafterBlockEntity placeCrafter(GameTestHelper context) {
        context.setBlock(POS, Blocks.CRAFTER);
        CrafterBlockEntity crafter = context.getBlockEntity(POS, CrafterBlockEntity.class);
        crafter.setItem(0, new ItemStack(Items.CACTUS, 4));
        crafter.setItem(1, new ItemStack(Items.CACTUS, 4));
        return crafter;
    }

    /** Fires the Crafter once, as a redstone pulse would, and returns the green dye (and anything else) it ejected. */
    private static List<ItemEntity> fire(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absolute = context.absolutePos(POS);
        for (ItemEntity old : level.getEntitiesOfClass(ItemEntity.class, new AABB(absolute).inflate(4))) {
            old.discard();
        }

        level.getBlockState(absolute).tick(level, absolute, level.getRandom());
        return level.getEntitiesOfClass(ItemEntity.class, new AABB(absolute).inflate(4));
    }

    private static boolean ejected(List<ItemEntity> items, String id, int count) {
        return items.stream().anyMatch(item -> is(item.getItem(), id, count));
    }

    private static CrafterMenu openCrafter(ServerPlayer player, CrafterBlockEntity crafter) {
        CrafterMenu menu = new CrafterMenu(nextContainerId++, player.getInventory(), crafter, new SimpleContainerData(10));
        player.containerMenu = menu;
        return menu;
    }

    @GameTest
    public void anUnwatchedCrafterUsesThePackDefault(GameTestHelper context) {
        placeCrafter(context);

        context.assertTrue(ejected(fire(context), "minecraft:green_dye", 5), "with nobody watching and no pin, the pack default (5 green dye) is used");
        context.succeed();
    }

    @GameTest
    public void aPinMakesACrafterChoiceStick(GameTestHelper context) {
        CrafterBlockEntity crafter = placeCrafter(context);
        ServerPlayer player = player(context);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        CrafterMenu menu = openCrafter(player, crafter);

        Offer offer = PolyCraftNetworking.offered(menu).orElseThrow();
        context.assertTrue(offer.pinnable() && offer.options().size() == 2, "the viewer should be offered both recipes and the pin");
        context.assertTrue(is(menu.getSlot(menu.slots.size() - 1).getItem(), "minecraft:green_dye", 5), "the preview should show the pack default first");

        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(menu.containerId, offer.groupKey(), CACTUS_A));
        context.assertTrue(is(menu.getSlot(menu.slots.size() - 1).getItem(), "minecraft:green_dye", 3), "the preview should follow the viewer's choice");
        context.assertTrue(ejected(fire(context), "minecraft:green_dye", 3), "while the viewer has it open, a pulse crafts their choice");

        // Close it: with no pin, the choice stops applying.
        player.containerMenu = player.inventoryMenu;
        context.assertTrue(ejected(fire(context), "minecraft:green_dye", 5), "with nobody watching and no pin, the pack default comes back");

        // Pin the viewer's choice and close it again: now it sticks.
        player.containerMenu = menu;
        PolyCraftNetworking.applyPin(player, new SetPinPayload(menu.containerId, offer.groupKey(), true));
        player.containerMenu = player.inventoryMenu;
        context.assertTrue(ejected(fire(context), "minecraft:green_dye", 3), "the pinned recipe should be crafted with nobody watching");

        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        context.succeed();
    }

    @GameTest
    public void aCrafterPinSurvivesSavingAndLoading(GameTestHelper context) {
        CrafterBlockEntity crafter = placeCrafter(context);
        ((PinHolder) crafter).polycraft$pins().pin("some|group", "a:recipe");

        CompoundTag saved = crafter.saveWithFullMetadata(context.getLevel().registryAccess());
        BlockEntity loaded = BlockEntity.loadStatic(context.absolutePos(POS), context.getLevel().getBlockState(context.absolutePos(POS)), saved, context.getLevel().registryAccess());

        context.assertTrue(loaded instanceof PinHolder holder && holder.polycraft$pins().get("some|group").equals(Optional.of("a:recipe")), "the Crafter's pin should survive");
        context.succeed();
    }

    // ---- campfire ----

    @GameTest
    public void aCampfireUsesThePackDefault(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        context.setBlock(POS, Blocks.CAMPFIRE);
        BlockPos absolute = context.absolutePos(POS);
        CampfireBlockEntity campfire = context.getBlockEntity(POS, CampfireBlockEntity.class);
        for (ItemEntity old : level.getEntitiesOfClass(ItemEntity.class, new AABB(absolute).inflate(3))) {
            old.discard();
        }

        context.assertTrue(campfire.placeFood(level, null, new ItemStack(Items.EGG)), "the egg should go on the campfire");
        RecipeManager.CachedCheck<net.minecraft.world.item.crafting.SingleRecipeInput, net.minecraft.world.item.crafting.CampfireCookingRecipe> check =
            RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
        for (int i = 0; i < 25; i++) {
            CampfireBlockEntity.cookTick(level, absolute, level.getBlockState(absolute), campfire, check);
        }

        List<ItemEntity> dropped = level.getEntitiesOfClass(ItemEntity.class, new AABB(absolute).inflate(3));
        context.assertTrue(dropped.stream().anyMatch(item -> idOf(item.getItem()).equals("minecraft:paper")), "the pack default (paper) should be cooked, not vanilla's bone meal");
        context.succeed();
    }
}
