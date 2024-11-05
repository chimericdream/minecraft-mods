package com.chimericdream.miniblockmerchants.registry;

import com.chimericdream.miniblockmerchants.ModInfo;
import net.minecraft.util.Identifier;

import static com.chimericdream.miniblockmerchants.registry.ModRegistries.registerCustomStat;

public class MMStats {
    public static final Identifier TRADE_FOR_MINIBLOCK_ID = Identifier.of(ModInfo.MOD_ID, "trade_for_miniblock");

    public static void init() {
        registerCustomStat(TRADE_FOR_MINIBLOCK_ID);
    }
}
