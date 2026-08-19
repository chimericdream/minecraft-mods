package com.chimericdream.camelnostrils.advancement;

import com.chimericdream.camelnostrils.ModInfo;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class CamelNostrilsAdvancements {
    public static final Identifier CAMEL_NOSTRILS = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "camel_nostrils");
    public static final Identifier FISH_WALKER = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "fish_walker");
    public static final Identifier FISH_OUT_OF_WATER = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "fish_out_of_water");
    public static final Identifier WHAT_GOES_UP = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "what_goes_up");

    private CamelNostrilsAdvancements() {
    }

    public static void award(ServerPlayer player, Identifier advancementId) {
        MinecraftServer server = player.level().getServer();
        AdvancementHolder advancement = server.getAdvancements().get(advancementId);

        if (advancement != null) {
            player.getAdvancements().award(advancement, "magic");
        }
    }
}
