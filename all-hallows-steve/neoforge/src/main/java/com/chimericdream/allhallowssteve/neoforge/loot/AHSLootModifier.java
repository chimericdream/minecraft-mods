package com.chimericdream.allhallowssteve.neoforge.loot;

import com.chimericdream.allhallowssteve.loot.AHSLootTableModifier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AHSLootModifier extends LootModifier {
    private static final AHSLootTableModifier LOOT_TABLE_MODIFIER = new AHSLootTableModifier();
    private static final Map<String, List<LootPool>> LOOT_POOL_CACHE = new HashMap<>();

    public static final Supplier<MapCodec<AHSLootModifier>> CODEC = Suppliers.memoize(
        () -> RecordCodecBuilder.mapCodec(instance -> codecStart(instance).apply(instance, AHSLootModifier::new))
    );

    public AHSLootModifier(LootItemCondition[] conditionsIn, int priority) {
        super(conditionsIn, priority);
    }

    private static List<LootPool> getPools(Identifier id, LootContext context) {
        if (LOOT_POOL_CACHE.containsKey(id.toString())) {
            return LOOT_POOL_CACHE.get(id.toString());
        }

        ServerLevel level = context.getLevel();
        HolderLookup.Provider wrapperLookup = level.registryAccess();

        List<LootPool.Builder> poolBuilders = LOOT_TABLE_MODIFIER.generatePoolBuilders(id, wrapperLookup);
        List<LootPool> lootPools = poolBuilders.stream().map(LootPool.Builder::build).toList();

        LOOT_POOL_CACHE.put(id.toString(), lootPools);

        return lootPools;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Identifier id = context.getQueriedLootTableId();
        List<LootPool> lootPools = getPools(id, context);

        for (LootPool pool : lootPools) {
            pool.addRandomItems(generatedLoot::add, context);
        }

        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
