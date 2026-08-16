package com.chimericdream.artificialheart.loot;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class ArtificialHeartLootTables {
    public static final ResourceKey<LootTable> PASSIVE_CREAKING = ResourceKey.create(
        Registries.LOOT_TABLE,
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "entities/passive_creaking")
    );
}
