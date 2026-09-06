package com.chimericdream.effectivegear.tags;

import com.chimericdream.effectivegear.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.VillagerTrade;

public class EffectiveGearVillagerTradeTags {
    public static final TagKey<VillagerTrade> WANDERING_TRADER_TRIMS = TagKey.create(Registries.VILLAGER_TRADE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wandering_trader_trims"));
}
