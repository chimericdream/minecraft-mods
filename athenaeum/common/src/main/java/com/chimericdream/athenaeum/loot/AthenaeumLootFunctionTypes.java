package com.chimericdream.athenaeum.loot;

import com.chimericdream.athenaeum.ModInfo;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import static com.chimericdream.athenaeum.AthenaeumMod.MANAGER;

public class AthenaeumLootFunctionTypes {
    public static MapCodec<GetRandomBookFunction> GET_RANDOM_BOOK = GetRandomBookFunction.CODEC;

    @SuppressWarnings("unchecked")
    private static void register(String id, MapCodec<? extends LootItemFunction> lootFunctionType) {
        MANAGER.get()
            .get((ResourceKey<Registry<MapCodec<? extends LootItemFunction>>>) BuiltInRegistries.LOOT_FUNCTION_TYPE.key())
            .register(
                Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id),
                () -> lootFunctionType
            );
    }

    public static void register() {
        register("get_random_book", GET_RANDOM_BOOK);
    }
}
