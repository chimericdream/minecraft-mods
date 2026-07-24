package com.chimericdream.miniblockmerchants.neoforge.registry;

import com.chimericdream.lib.neoforge.loot.LootModifierHelper;
import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.neoforge.loot.VillagerConversionItemsLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootModifierRegistry {
    public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = LootModifierHelper.createRegister(ModInfo.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<VillagerConversionItemsLootModifier>> VILLAGER_CONVERSION_ITEMS_MODIFIER = LOOT_MODIFIERS.register("villager_conversion_items", VillagerConversionItemsLootModifier.CODEC);
}
