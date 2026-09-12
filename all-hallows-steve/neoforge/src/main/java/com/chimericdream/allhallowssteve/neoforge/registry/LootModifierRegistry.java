package com.chimericdream.allhallowssteve.neoforge.registry;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.neoforge.loot.AHSLootModifier;
import com.chimericdream.lib.neoforge.loot.LootModifierHelper;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootModifierRegistry {
    public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = LootModifierHelper.createRegister(ModInfo.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AHSLootModifier>> AHS_LOOT_MODIFIER = LOOT_MODIFIERS.register("loot_modifier", AHSLootModifier.CODEC);
}
