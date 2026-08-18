package com.chimericdream.villagertweaks.advancement;

import com.chimericdream.villagertweaks.ModInfo;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class VillagerTweaksAdvancements {
    public static final Identifier ROOT = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "root");
    public static final Identifier BAG_AND_TAG = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "bag_and_tag");
    public static final Identifier PIED_PIPER = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pied_piper");

    public static final int PIED_PIPER_THRESHOLD = 8;

    private VillagerTweaksAdvancements() {
    }

    public static void award(ServerPlayer player, Identifier advancementId) {
        MinecraftServer server = player.level().getServer();
        AdvancementHolder advancement = server.getAdvancements().get(advancementId);

        if (advancement != null) {
            player.getAdvancements().award(advancement, "magic");
        }
    }
}
