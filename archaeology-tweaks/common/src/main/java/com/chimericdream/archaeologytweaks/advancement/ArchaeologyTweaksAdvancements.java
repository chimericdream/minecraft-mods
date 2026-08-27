package com.chimericdream.archaeologytweaks.advancement;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ArchaeologyTweaksAdvancements {
    public static final Identifier ROOT = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "root");
    public static final Identifier FIRST_DIG = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "first_dig");
    public static final Identifier INTERDIMENSIONAL_ARCHAEOLOGY = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "interdimensional_archaeology");
    public static final Identifier LUCKY_BLOCK = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "lucky_block");

    private ArchaeologyTweaksAdvancements() {
    }

    public static void award(ServerPlayer player, Identifier advancementId) {
        MinecraftServer server = player.level().getServer();
        AdvancementHolder advancement = server.getAdvancements().get(advancementId);

        if (advancement != null) {
            player.getAdvancements().award(advancement, "magic");
        }
    }
}
