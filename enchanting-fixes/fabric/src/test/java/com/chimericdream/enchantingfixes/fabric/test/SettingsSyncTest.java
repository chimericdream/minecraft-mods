package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.config.SyncedConfig;
import com.chimericdream.enchantingfixes.network.ServerSettingsPayload;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SettingsSyncTest extends BootstrapMinecraft {
    @AfterEach
    void clearSyncedConfig() {
        SyncedConfig.setFromServer(null);
    }

    private static ServerSettingsPayload roundTrip(ServerSettingsPayload payload) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        ServerSettingsPayload.CODEC.encode(buf, payload);
        return ServerSettingsPayload.CODEC.decode(buf);
    }

    @Test
    void thePayloadKeepsEachSetting() {
        EnchantingFixesConfig server = new EnchantingFixesConfig();
        server.maxLevels = Map.of("minecraft:sharpness", 40, "mymod:zap", 3);
        server.useDefaultMaxLevel = true;
        server.defaultMaxLevel = 20;
        server.defaultAppliesToSingleLevel = true;
        server.removeAnvilLimit = false;

        ServerSettingsPayload received = roundTrip(ServerSettingsPayload.of(server, true));
        EnchantingFixesConfig client = received.toConfig();

        assertEquals(true, received.scalingPack());
        assertEquals(server.maxLevels, client.maxLevels);
        assertEquals(true, client.useDefaultMaxLevel);
        assertEquals(20, client.defaultMaxLevel);
        assertEquals(true, client.defaultAppliesToSingleLevel);
        assertEquals(false, client.removeAnvilLimit);
    }

    @Test
    void withoutServerSettingsTheLocalConfigApplies() {
        assertSame(EnchantingFixesConfig.CONFIG.instance(), SyncedConfig.effective());
    }

    @Test
    void serverSettingsReplaceTheLocalConfigForMaxLevels() {
        Enchantment sharpness = VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS).value();
        assertEquals(10, sharpness.getMaxLevel());

        EnchantingFixesConfig server = new EnchantingFixesConfig();
        server.maxLevels = Map.of("minecraft:sharpness", 40);
        SyncedConfig.setFromServer(roundTrip(ServerSettingsPayload.of(server, false)).toConfig());
        assertEquals(40, sharpness.getMaxLevel());

        SyncedConfig.setFromServer(null);
        assertEquals(10, sharpness.getMaxLevel());
    }
}
