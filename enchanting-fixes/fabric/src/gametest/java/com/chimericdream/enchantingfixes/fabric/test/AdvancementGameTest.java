package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.ModInfo;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.book;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchanted;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.player;

/** Each advancement loads with the right parent and frame, and the thing that it describes awards it. */
@SuppressWarnings("unused")
public class AdvancementGameTest {
    private static AdvancementHolder advancement(GameTestHelper context, String id) {
        AdvancementHolder holder = context.getLevel().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id));
        context.assertTrue(holder != null, Component.literal("advancement " + id + " should exist"));
        return holder;
    }

    private static void assertDone(GameTestHelper context, ServerPlayer player, String id, boolean expected) {
        boolean done = player.getAdvancements().getOrStartProgress(advancement(context, id)).isDone();
        context.assertValueEqual(done, expected, id + " done");
    }

    /** Adding to the inventory of a mock player sends a packet, so this fires the trigger directly. */
    private static void pickUp(ServerPlayer player, ItemStack stack) {
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), stack);
    }

    private static void hitWith(GameTestHelper context, ServerPlayer player, ResourceKey<Enchantment> key, int level) {
        player.setItemSlot(EquipmentSlot.MAINHAND, enchanted(context, Items.DIAMOND_SWORD, key, level));
        Cow cow = context.spawn(EntityTypes.COW, 1, 1, 1);
        CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(player, cow, player.damageSources().playerAttack(player), 1.0F, 1.0F, false);
    }

    @GameTest
    public void everyAdvancementLoadsWithTheRightParentAndFrame(GameTestHelper context) {
        Map<String, String> parents = Map.of(
            "overpowered", "root",
            "but_why", "overpowered",
            "waste_of_xp", "but_why",
            "lotta_looting", "overpowered",
            "scrooge_mcduck", "overpowered",
            "deleted", "root",
            "into_orbit", "deleted"
        );

        context.assertTrue(advancement(context, "root").value().parent().isEmpty(), Component.literal("root should have no parent"));
        parents.forEach((id, parent) -> {
            var value = advancement(context, id).value();
            var display = value.display().orElseThrow();
            boolean secret = id.equals("waste_of_xp") || id.equals("into_orbit");

            context.assertValueEqual(value.parent().map(Identifier::getPath).orElse(null), parent, "parent of " + id);
            context.assertValueEqual(display.isHidden(), secret, id + " hidden");
            context.assertValueEqual(display.getType() == AdvancementType.CHALLENGE, secret, id + " is a challenge");
        });
        context.succeed();
    }

    @GameTest
    public void enchantmentLevelsAwardTheLevelAdvancements(GameTestHelper context) {
        ServerPlayer player = player(context);

        pickUp(player, enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 9));
        assertDone(context, player, "overpowered", false);

        pickUp(player, enchanted(context, Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 10));
        assertDone(context, player, "overpowered", true);
        assertDone(context, player, "but_why", false);

        pickUp(player, book(context, Enchantments.PROTECTION, 100));
        assertDone(context, player, "but_why", true);
        assertDone(context, player, "waste_of_xp", false);

        pickUp(player, book(context, Enchantments.UNBREAKING, 255));
        assertDone(context, player, "waste_of_xp", true);
        context.succeed();
    }

    @GameTest
    public void lootingAndFortuneNeedLevel50OnTheItem(GameTestHelper context) {
        ServerPlayer player = player(context);

        pickUp(player, book(context, Enchantments.LOOTING, 50));
        pickUp(player, enchanted(context, Items.DIAMOND_SWORD, Enchantments.LOOTING, 49));
        assertDone(context, player, "lotta_looting", false);

        pickUp(player, enchanted(context, Items.DIAMOND_SWORD, Enchantments.LOOTING, 50));
        assertDone(context, player, "lotta_looting", true);
        assertDone(context, player, "scrooge_mcduck", false);

        pickUp(player, enchanted(context, Items.DIAMOND_PICKAXE, Enchantments.FORTUNE, 50));
        assertDone(context, player, "scrooge_mcduck", true);
        context.succeed();
    }

    @GameTest
    public void knockbackHitsAwardDeletedAndIntoOrbit(GameTestHelper context) {
        ServerPlayer player = player(context);

        hitWith(context, player, Enchantments.KNOCKBACK, 31);
        assertDone(context, player, "deleted", false);

        hitWith(context, player, Enchantments.KNOCKBACK, 32);
        assertDone(context, player, "deleted", true);
        assertDone(context, player, "into_orbit", false);

        hitWith(context, player, Enchantments.KNOCKBACK, 50);
        assertDone(context, player, "into_orbit", true);
        context.succeed();
    }
}
