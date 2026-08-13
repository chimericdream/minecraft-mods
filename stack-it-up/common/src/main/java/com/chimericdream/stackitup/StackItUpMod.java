package com.chimericdream.stackitup;

import com.chimericdream.lib.commands.ChimericCommands;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import com.chimericdream.stackitup.client.StackItUpClient;
import com.chimericdream.stackitup.command.StackSizeCommand;
import com.chimericdream.stackitup.config.ConfigManager;
import com.chimericdream.stackitup.network.ByteArrayPayload;
import com.chimericdream.stackitup.network.NetworkHelper;

public final class StackItUpMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static MinecraftServer minecraftServer;

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        REGISTRY_HELPER.init();

        // A single registerReceiver call here (common init, runs on both sides) registers both the
        // payload type and the receiver at once - Architectury's NetworkManager handles the
        // client/dedicated-server split internally. Calling registerS2CPayloadType separately here
        // *and* registerReceiver again from client-only init double-registers the same payload type
        // on an actual client (where both the main and client entrypoints run in one JVM) and crashes
        // with "Packet type ... is already registered!" - do not split this across two call sites.
        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                ByteArrayPayload.ID,
                ByteArrayPayload.CODEC,
                (payload, context) -> StackItUpClient.handleConfigPayload(payload.data())
        );

        ChimericCommands.register(new StackSizeCommand());

        LifecycleEvent.SERVER_STARTING.register(StackItUpMod::onServerStarting);
        LifecycleEvent.SERVER_STARTED.register(server -> ConfigManager.getConfigManager().sendConfigToPlayer());
        PlayerEvent.PLAYER_JOIN.register(player ->
                NetworkHelper.sentConfigToPlayer(player, ConfigManager.getConfigManager().getSerializedConfig()));
    }

    private static void onServerStarting(MinecraftServer server) {
        minecraftServer = server;
        ConfigManager.getConfigManager().setupConfig();
        LOGGER.info("[StackItUp] Loaded!");
    }
}
