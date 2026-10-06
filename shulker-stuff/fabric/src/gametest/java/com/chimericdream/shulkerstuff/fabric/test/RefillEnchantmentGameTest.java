package com.chimericdream.shulkerstuff.fabric.test;

import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameType;

import java.util.List;
import java.util.UUID;

/** Refill pulls a matching stack out of an enchanted shulker box when the held stack runs out. */
@SuppressWarnings("unused")
public class RefillEnchantmentGameTest {
    private static ItemStack refillBox(GameTestHelper context, int cobbleInside) {
        Holder<Enchantment> refill = context.getLevel()
            .registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(ModEnchantments.REFILLING);

        ItemStack box = new ItemStack(Items.SHULKER_BOX);
        box.enchant(refill, 1);
        box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(new ItemStack(Items.GRASS_BLOCK, cobbleInside))));

        return box;
    }

    /**
     * Refill ignores creative players, and {@code makeMockServerPlayerInLevel} always builds one, so
     * this is the same helper with survival mode.
     */
    private static ServerPlayer survivalPlayer(GameTestHelper context) {
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        ServerPlayer player = new ServerPlayer(context.getLevel().getServer(), context.getLevel(), cookie.gameProfile(), cookie.clientInformation()) {
            @Override
            public GameType gameMode() {
                return GameType.SURVIVAL;
            }
        };
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(new ChannelHandler[]{connection});
        context.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);

        return player;
    }

    @GameTest
    public void refillsTheHeldSlotFromTheBox(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        player.getInventory().setItem(8, refillBox(context, 63));
        player.getInventory().setItem(0, new ItemStack(Items.GRASS_BLOCK, 1));
        player.getInventory().setSelectedSlot(0);

        player.doTick();

        // "Place" the last block.
        player.getInventory().setItem(0, ItemStack.EMPTY);
        player.doTick();

        ItemStack held = player.getInventory().getItem(0);
        if (!held.is(Items.GRASS_BLOCK) || held.getCount() != 63) {
            context.fail("Expected 63 grass blocks refilled into the held slot, got " + held);
        }

        context.succeed();
    }

    /**
     * A client closing a screen reaches the server as {@code doCloseContainer}, not
     * {@code closeContainer}. The open-screen flag used to reset only in the latter, so after the
     * first menu the player ever opened (like loading the box) Refill never fired again.
     */
    @GameTest
    public void stillRefillsAfterAClientClosedMenu(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        player.getInventory().setItem(8, refillBox(context, 63));
        player.getInventory().setItem(0, new ItemStack(Items.GRASS_BLOCK, 1));
        player.getInventory().setSelectedSlot(0);

        player.openMenu(new SimpleMenuProvider((id, inventory, p) -> ChestMenu.threeRows(id, inventory), Component.empty()));
        player.doCloseContainer();

        player.doTick();
        player.getInventory().setItem(0, ItemStack.EMPTY);
        player.doTick();

        ItemStack held = player.getInventory().getItem(0);
        if (!held.is(Items.GRASS_BLOCK) || held.getCount() != 63) {
            context.fail("Expected 63 grass blocks refilled after a menu was closed, got " + held);
        }

        context.succeed();
    }

    /** The offhand used to be refilled into the wrong slot index, from the main hand's last stack. */
    @GameTest
    public void refillsTheOffhandFromTheBox(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        player.getInventory().setItem(8, refillBox(context, 63));
        player.getInventory().setItem(Inventory.SLOT_OFFHAND, new ItemStack(Items.GRASS_BLOCK, 1));
        player.getInventory().setSelectedSlot(0);

        player.doTick();
        player.getInventory().setItem(Inventory.SLOT_OFFHAND, ItemStack.EMPTY);
        player.doTick();

        ItemStack held = player.getOffhandItem();
        if (!held.is(Items.GRASS_BLOCK) || held.getCount() != 63) {
            context.fail("Expected 63 grass blocks refilled into the offhand, got " + held);
        }

        context.succeed();
    }
}
