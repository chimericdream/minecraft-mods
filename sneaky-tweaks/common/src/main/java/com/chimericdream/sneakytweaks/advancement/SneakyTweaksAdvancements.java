package com.chimericdream.sneakytweaks.advancement;

import com.chimericdream.sneakytweaks.ModInfo;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class SneakyTweaksAdvancements {
    public static final Identifier SNEAK_THROUGH_BERRIES = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "sneak_through_berries");
    public static final Identifier SNEAK_ON_CAMPFIRE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "sneak_on_campfire");
    public static final Identifier OVERSTAY_YOUR_WELCOME = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "overstay_your_welcome");
    public static final Identifier CROUCH_BRIDGE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "crouch_bridge");
    public static final Identifier LOOKED_DOWN = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "looked_down");

    private SneakyTweaksAdvancements() {
    }

    public static void award(ServerPlayer player, Identifier advancementId) {
        MinecraftServer server = player.level().getServer();
        AdvancementHolder advancement = server.getAdvancements().get(advancementId);

        if (advancement != null) {
            player.getAdvancements().award(advancement, "magic");
        }
    }
}
