package com.chimericdream.archaeologytweaks.tag;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.VillagerTrade;

public class ModTags {
    public static final TagKey<VillagerTrade> WANDERING_TRADER_SHERDS = TagKey.create(Registries.VILLAGER_TRADE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wandering_trader_sherds"));
}
