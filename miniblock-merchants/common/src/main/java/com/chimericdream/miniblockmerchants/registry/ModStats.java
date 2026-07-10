package com.chimericdream.miniblockmerchants.registry;

import com.chimericdream.miniblockmerchants.ModInfo;
import net.minecraft.resources.Identifier;

import static com.chimericdream.miniblockmerchants.MiniblockMerchantsMod.REGISTRY_HELPER;

public class ModStats {
    public static final Identifier TRADE_FOR_MINIBLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "trade_for_miniblock");

    public static void init() {
        REGISTRY_HELPER.registerCustomStat(TRADE_FOR_MINIBLOCK_ID);
    }
}
