package com.chimericdream.pannotiacompanion.tag;

import com.chimericdream.pannotiacompanion.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.VillagerTrade;

public class ModTags {
    public static final TagKey<VillagerTrade> WANDERING_TRADER_MINI_BLOCKS = tradeTag("wandering_trader_mini_blocks");
    public static final TagKey<VillagerTrade> WANDERING_TRADER_STREAMER_HEADS = tradeTag("wandering_trader_streamer_heads");

    private static TagKey<VillagerTrade> tradeTag(String name) {
        return TagKey.create(Registries.VILLAGER_TRADE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name));
    }
}
