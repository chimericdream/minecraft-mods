package com.chimericdream.enchantingfixes.fabric.test;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;

final class TestStacks {
    private TestStacks() {
    }

    /**
     * A mock player with a connection. Some actions send the player a packet (an advancement toast, an
     * anvil update). A connection with no open channel keeps the packets in a queue.
     */
    static ServerPlayer player(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
        player.connection = new ServerGamePacketListenerImpl(
            context.getLevel().getServer(),
            new Connection(PacketFlow.SERVERBOUND),
            player,
            CommonListenerCookie.createInitial(player.getGameProfile(), false)
        );

        return player;
    }

    static Holder<Enchantment> enchantment(GameTestHelper context, ResourceKey<Enchantment> key) {
        return context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    static ItemStack enchanted(GameTestHelper context, Item item, ResourceKey<Enchantment> key, int level) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.ENCHANTMENTS, levels(context, key, level));
        return stack;
    }

    static ItemStack book(GameTestHelper context, ResourceKey<Enchantment> key, int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        stack.set(DataComponents.STORED_ENCHANTMENTS, levels(context, key, level));
        return stack;
    }

    private static ItemEnchantments levels(GameTestHelper context, ResourceKey<Enchantment> key, int level) {
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(enchantment(context, key), level);
        return enchantments.toImmutable();
    }
}
