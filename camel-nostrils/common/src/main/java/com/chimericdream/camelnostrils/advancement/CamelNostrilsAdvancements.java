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
    public static final Identifier GOLDEN_EGG_FIRST_EATEN = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "golden_egg_first_eaten");
    public static final Identifier NAP_TIME = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "nap_time");
    public static final Identifier MIRACLE_GRO = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "miracle_gro");
    public static final Identifier MIRACLE_CURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "miracle_cure");
    public static final Identifier FASTER_THAN_A_SNAIL = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "faster_than_a_snail");

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
