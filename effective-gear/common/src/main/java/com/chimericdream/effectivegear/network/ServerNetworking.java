package com.chimericdream.effectivegear.network;

import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.effectivegear.ability.TrimAbilities;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerNetworking {
    public static Identifier USE_ABILITY = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "events/abilities/use");

    public static void init() {
    }

    protected static void handleUseAbilityPacket(MinecraftServer server, ServerPlayer player) {
        if (server == null || player == null) {
            return;
        }

        server.execute(() -> TrimAbilities.tryUseAbility(player));
    }
}
