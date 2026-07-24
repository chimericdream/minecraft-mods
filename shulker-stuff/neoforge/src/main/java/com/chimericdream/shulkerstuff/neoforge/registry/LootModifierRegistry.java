package com.chimericdream.shulkerstuff.neoforge.registry;

import com.chimericdream.lib.neoforge.loot.LootModifierHelper;
import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.neoforge.loot.ShulkerStuffLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootModifierRegistry {
    public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = LootModifierHelper.createRegister(ModInfo.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ShulkerStuffLootModifier>> SHULKER_STUFF_LOOT_MODIFIER = LOOT_MODIFIERS.register("loot_modifier", ShulkerStuffLootModifier.CODEC);
}
