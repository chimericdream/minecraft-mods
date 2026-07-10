package com.chimericdream.miniblockmerchants.advancement;

import com.chimericdream.miniblockmerchants.ModInfo;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

public class MMAdvancements {
    public static final Identifier TRADE_100_TIMES = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "trades/100_trades");
    public static final Identifier TRADE_250_TIMES = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "trades/250_trades");
    public static final Identifier TRADE_500_TIMES = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "trades/500_trades");
    public static final Identifier TRADE_1000_TIMES = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "trades/1000_trades");
    public static final Identifier TRADE_5000_TIMES = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "trades/5000_trades");
    public static final Identifier TRADE_10000_TIMES = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "trades/10000_trades");

    public static AdvancementHolder getAdvancement(MinecraftServer server, Identifier id) {
        return server.getAdvancements().get(id);
    }
}
