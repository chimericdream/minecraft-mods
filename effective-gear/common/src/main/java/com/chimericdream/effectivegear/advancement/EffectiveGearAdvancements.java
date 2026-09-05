package com.chimericdream.effectivegear.advancement;

import com.chimericdream.effectivegear.ModInfo;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class EffectiveGearAdvancements {
    public static final Identifier ROOT = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "root");
    public static final Identifier COLORS_THAT_POP = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "colors_that_pop");

    private EffectiveGearAdvancements() {
    }

    public static void award(ServerPlayer player, Identifier advancementId) {
        MinecraftServer server = player.level().getServer();
        AdvancementHolder advancement = server.getAdvancements().get(advancementId);

        if (advancement != null) {
            player.getAdvancements().award(advancement, "magic");
        }
    }
}
