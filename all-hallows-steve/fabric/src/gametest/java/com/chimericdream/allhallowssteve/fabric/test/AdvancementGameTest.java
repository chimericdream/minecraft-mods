package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.advancement.CiderEvent;
import com.chimericdream.allhallowssteve.advancement.ModTriggers;
import com.chimericdream.allhallowssteve.advancement.PumpkinEvent;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.chimericdream.allhallowssteve.item.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.Map;

/**
 * Coverage for the mod's advancement tab: that every advancement loads with the right parent, frame,
 * hiddenness and reward, and that each one is awarded by the thing it describes. The toasts themselves
 * are covered by the manual steps in {@code TEST_PLAN.md}.
 */
@SuppressWarnings("unused")
public class AdvancementGameTest {
    private static AdvancementHolder advancement(GameTestHelper context, String id) {
        AdvancementHolder holder = context.getLevel().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id));
        context.assertTrue(holder != null, Component.literal("advancement " + id + " should exist"));

        return holder;
    }

    /**
     * A mock player with a connection attached. Awarding some advancements (the tab root, experience) sends
     * the player a packet, which a bare mock player has no connection to send; a connection with no open
     * channel just queues what it is given.
     */
    private static ServerPlayer player(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
        player.connection = new ServerGamePacketListenerImpl(
            context.getLevel().getServer(),
            new Connection(PacketFlow.SERVERBOUND),
            player,
            CommonListenerCookie.createInitial(player.getGameProfile(), false)
        );

        return player;
    }

    /** Tells the advancements the player just picked up {@code stack}. Adding to a mock player's inventory would try to send a packet, so this fires the trigger directly. */
    private static void pickUp(ServerPlayer player, ItemStack stack) {
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), stack);
    }

    private static boolean done(ServerPlayer player, AdvancementHolder holder) {
        return player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static void assertDone(GameTestHelper context, ServerPlayer player, String id, boolean expected) {
        context.assertValueEqual(done(player, advancement(context, id)), expected, id + " done");
    }

    @GameTest
    public void everyAdvancementLoadsWithTheRightParent(GameTestHelper context) {
        Map<String, String> parents = Map.ofEntries(
            Map.entry("gourd_workshop", "root"),
            Map.entry("not_just_orange", "gourd_workshop"),
            Map.entry("first_cut", "gourd_workshop"),
            Map.entry("pumpkin_head", "first_cut"),
            Map.entry("lights_out", "pumpkin_head"),
            Map.entry("better_side", "pumpkin_head"),
            Map.entry("rare_cut", "first_cut"),
            Map.entry("face_for_every_occasion", "rare_cut"),
            Map.entry("lit_different", "gourd_workshop"),
            Map.entry("pressing_matters", "root"),
            Map.entry("fresh_pressed", "pressing_matters"),
            Map.entry("berry_good", "fresh_pressed"),
            Map.entry("three_flavors", "fresh_pressed"),
            Map.entry("waste_not", "pressing_matters"),
            Map.entry("spice_up_your_life", "root"),
            Map.entry("mulling_it_over", "spice_up_your_life"),
            Map.entry("big_batch", "mulling_it_over"),
            Map.entry("ladle_it_on", "big_batch"),
            Map.entry("hot_cider_weather", "ladle_it_on"),
            Map.entry("seasons_drinkings", "hot_cider_weather")
        );

        context.assertTrue(advancement(context, "root").value().parent().isEmpty(), Component.literal("root should have no parent"));
        parents.forEach((id, parent) -> context.assertValueEqual(
            advancement(context, id).value().parent().map(Identifier::getPath).orElse(null),
            parent,
            "parent of " + id
        ));
        context.succeed();
    }

    @GameTest
    public void onlyTheThreeSecretOnesAreHiddenAndOnlyTheTwoChallengesPayExperience(GameTestHelper context) {
        for (String id : new String[]{"root", "gourd_workshop", "not_just_orange", "first_cut", "pumpkin_head", "lights_out", "better_side", "rare_cut", "face_for_every_occasion", "lit_different",
            "pressing_matters", "fresh_pressed", "berry_good", "three_flavors", "waste_not", "spice_up_your_life", "mulling_it_over",
            "big_batch", "ladle_it_on", "hot_cider_weather", "seasons_drinkings"}) {
            var advancement = advancement(context, id).value();
            var display = advancement.display().orElseThrow();
            boolean secret = id.equals("lights_out") || id.equals("face_for_every_occasion") || id.equals("seasons_drinkings");
            boolean challenge = id.equals("face_for_every_occasion") || id.equals("seasons_drinkings");

            context.assertValueEqual(display.isHidden(), secret, id + " hidden");
            context.assertValueEqual(display.getType() == AdvancementType.CHALLENGE, challenge, id + " is a challenge");
            context.assertValueEqual(advancement.rewards().experience(), challenge ? 100 : 0, id + " experience");
        }

        context.succeed();
    }

    @GameTest
    public void aPumpkinStartsTheTab(GameTestHelper context) {
        ServerPlayer player = player(context);
        pickUp(player, new ItemStack(Items.PUMPKIN));

        assertDone(context, player, "root", true);
        context.succeed();
    }

    @GameTest
    public void dyeingCarvingAndTurningAreAwardedByTheirEvents(GameTestHelper context) {
        ServerPlayer player = player(context);

        ModTriggers.fire(player, PumpkinEvent.DYED);
        assertDone(context, player, "not_just_orange", true);
        assertDone(context, player, "first_cut", false);

        ModTriggers.fire(player, PumpkinEvent.CARVED);
        assertDone(context, player, "first_cut", true);

        ModTriggers.fire(player, PumpkinEvent.TURNED);
        assertDone(context, player, "better_side", true);

        context.succeed();
    }

    @GameTest
    public void lightingWithAnythingButARegularTorchIsAwarded(GameTestHelper context) {
        ServerPlayer player = player(context);
        assertDone(context, player, "lit_different", false);

        ModTriggers.fire(player, PumpkinEvent.LIT_UNUSUAL);

        assertDone(context, player, "lit_different", true);
        context.succeed();
    }

    @GameTest
    public void wearingACarvedPumpkinIsAwardedButNotTheHiddenOne(GameTestHelper context) {
        ServerPlayer player = player(context);
        ItemStack carved = new ItemStack(ModBlocks.DECORATED_PUMPKIN.get());
        carved.set(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY.with(Direction.EAST, "creeper"));
        player.setItemSlot(EquipmentSlot.HEAD, carved);

        ModTriggers.checkWornPumpkin(player);

        assertDone(context, player, "pumpkin_head", true);
        assertDone(context, player, "lights_out", false);
        context.succeed();
    }

    @GameTest
    public void wearingAnUncarvedPumpkinIsTheHiddenOneOnly(GameTestHelper context) {
        ServerPlayer player = player(context);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModBlocks.DECORATED_PUMPKIN.get()));

        ModTriggers.checkWornPumpkin(player);

        assertDone(context, player, "lights_out", true);
        assertDone(context, player, "pumpkin_head", false);
        context.succeed();
    }

    @GameTest
    public void wearingSomethingElseAwardsNothing(GameTestHelper context) {
        ServerPlayer player = player(context);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModBlocks.LIT_DECORATED_PUMPKIN.get()));

        ModTriggers.checkWornPumpkin(player);

        assertDone(context, player, "pumpkin_head", false);
        assertDone(context, player, "lights_out", false);
        context.succeed();
    }

    @GameTest
    public void oneRareStencilIsEnoughForRareCutButNotTheFullSet(GameTestHelper context) {
        ServerPlayer player = player(context);
        pickUp(player, new ItemStack(ModItems.HEART_STENCIL.get()));

        assertDone(context, player, "rare_cut", true);
        assertDone(context, player, "face_for_every_occasion", false);
        context.succeed();
    }

    @GameTest
    public void everyStencilCompletesTheFullSetAndPaysOut(GameTestHelper context) {
        ServerPlayer player = player(context);
        int before = player.totalExperience;
        for (var stencil : ModItems.PUMPKIN_STENCIL_ITEMS) {
            pickUp(player, new ItemStack((Item) stencil.get()));
        }

        assertDone(context, player, "face_for_every_occasion", true);
        context.assertValueEqual(player.totalExperience - before, 100, "experience from the full set");
        context.succeed();
    }

    @GameTest
    public void aMissingStencilLeavesTheFullSetIncomplete(GameTestHelper context) {
        ServerPlayer player = player(context);
        for (var stencil : ModItems.PUMPKIN_STENCIL_ITEMS) {
            if (stencil.get() != ModItems.BLANK_STENCIL.get()) {
                pickUp(player, new ItemStack((Item) stencil.get()));
            }
        }

        assertDone(context, player, "face_for_every_occasion", false);
        context.succeed();
    }

    private static void drink(ServerPlayer player, Item drink) {
        CriteriaTriggers.CONSUME_ITEM.trigger(player, new ItemStack(drink));
    }

    @GameTest
    public void aPressedJuiceInTheInventoryAwardsFreshPressedAndBerryGoodForBerries(GameTestHelper context) {
        ServerPlayer player = player(context);

        pickUp(player, new ItemStack(ModItems.APPLE_CIDER.get()));
        assertDone(context, player, "fresh_pressed", true);
        assertDone(context, player, "berry_good", false);

        pickUp(player, new ItemStack(ModItems.GLOW_BERRY_JUICE.get()));
        assertDone(context, player, "berry_good", true);
        context.succeed();
    }

    @GameTest
    public void threeFlavorsNeedsAllThreePlainDrinks(GameTestHelper context) {
        ServerPlayer player = player(context);

        drink(player, ModItems.APPLE_CIDER.get());
        drink(player, ModItems.SWEET_BERRY_JUICE.get());
        assertDone(context, player, "three_flavors", false);

        drink(player, ModItems.GLOW_BERRY_JUICE.get());
        assertDone(context, player, "three_flavors", true);
        context.succeed();
    }

    @GameTest
    public void feedingPomaceAndFillingACauldronAreAwardedByTheirEvents(GameTestHelper context) {
        ServerPlayer player = player(context);

        ModTriggers.firePomaceHealed(player);
        assertDone(context, player, "waste_not", true);
        assertDone(context, player, "big_batch", false);

        ModTriggers.fire(player, CiderEvent.CAULDRON_FILLED);
        assertDone(context, player, "big_batch", true);
        context.succeed();
    }

    @GameTest
    public void anySpiceStartsTheSpiceBranchButMullingSpicesDoNot(GameTestHelper context) {
        ServerPlayer player = player(context);

        pickUp(player, new ItemStack(ModItems.MULLING_SPICES.get()));
        assertDone(context, player, "spice_up_your_life", false);

        pickUp(player, new ItemStack(ModItems.NUTMEG.get()));
        assertDone(context, player, "spice_up_your_life", true);
        context.succeed();
    }

    @GameTest
    public void ladlingAndDrinkingAnyMulledDrinkAreAwardedButNotTheSet(GameTestHelper context) {
        ServerPlayer player = player(context);

        pickUp(player, new ItemStack(ModItems.MULLED_GLOW_BERRY_JUICE.get()));
        assertDone(context, player, "ladle_it_on", true);

        drink(player, ModItems.MULLED_APPLE_CIDER.get());
        assertDone(context, player, "hot_cider_weather", true);
        assertDone(context, player, "seasons_drinkings", false);
        context.succeed();
    }

    @GameTest
    public void everyMulledDrinkCompletesSeasonsDrinkingsAndPaysOut(GameTestHelper context) {
        ServerPlayer player = player(context);
        int before = player.totalExperience;

        drink(player, ModItems.MULLED_APPLE_CIDER.get());
        drink(player, ModItems.MULLED_SWEET_BERRY_JUICE.get());
        assertDone(context, player, "seasons_drinkings", false);

        drink(player, ModItems.MULLED_GLOW_BERRY_JUICE.get());
        assertDone(context, player, "seasons_drinkings", true);
        context.assertValueEqual(player.totalExperience - before, 100, "experience from Season's Drinkings");
        context.succeed();
    }
}
