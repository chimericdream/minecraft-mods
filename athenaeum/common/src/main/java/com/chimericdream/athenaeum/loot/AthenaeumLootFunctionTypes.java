package com.chimericdream.athenaeum.loot;

import com.chimericdream.athenaeum.ModInfo;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import static com.chimericdream.athenaeum.AthenaeumMod.MANAGER;

public class AthenaeumLootFunctionTypes {
    public static LootFunctionType<GetRandomBookFunction> GET_RANDOM_BOOK = new LootFunctionType<>(GetRandomBookFunction.CODEC);

    @SuppressWarnings("unchecked")
    private static void register(String id, LootFunctionType<?> lootFunctionType) {
        MANAGER.get()
            .get((RegistryKey<Registry<LootFunctionType<?>>>) Registries.LOOT_FUNCTION_TYPE.getKey())
            .register(
                Identifier.of(ModInfo.MOD_ID, id),
                () -> lootFunctionType
            );
    }

    public static void register() {
        register("get_random_book", GET_RANDOM_BOOK);
    }
}
