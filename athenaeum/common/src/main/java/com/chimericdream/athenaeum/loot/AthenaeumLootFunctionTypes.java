package com.chimericdream.athenaeum.loot;

import com.chimericdream.athenaeum.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import static com.chimericdream.athenaeum.AthenaeumMod.MANAGER;

public class AthenaeumLootFunctionTypes {
    public static LootItemFunctionType<GetRandomBookFunction> GET_RANDOM_BOOK = new LootItemFunctionType<>(GetRandomBookFunction.CODEC);

    @SuppressWarnings("unchecked")
    private static void register(String id, LootItemFunctionType<?> lootFunctionType) {
        MANAGER.get()
            .get((ResourceKey<Registry<LootItemFunctionType<?>>>) BuiltInRegistries.LOOT_FUNCTION_TYPE.key())
            .register(
                ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, id),
                () -> lootFunctionType
            );
    }

    public static void register() {
        register("get_random_book", GET_RANDOM_BOOK);
    }
}
