package com.chimericdream.shulkerstuff.neoforge.loot;

import com.chimericdream.shulkerstuff.neoforge.ShulkerStuffNeoForge;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
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

public class ShulkerStuffLootModifier extends LootModifier {
    private static final Map<String, List<LootPool>> LOOT_POOL_CACHE = new HashMap<>();

    public static final Supplier<MapCodec<ShulkerStuffLootModifier>> CODEC = Suppliers.memoize(
        () -> RecordCodecBuilder
            .mapCodec(
                instance -> ShulkerStuffLootModifier.codecStart(instance)
                    .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                    .apply(instance, ShulkerStuffLootModifier::new)
            )
    );
    private final Item item;

    public ShulkerStuffLootModifier(LootItemCondition[] conditionsIn, int priority, Item item) {
        super(conditionsIn, priority);
        this.item = item;
    }

    protected static List<LootPool> getPoolBuilders(Identifier id, LootContext context) {
        if (LOOT_POOL_CACHE.containsKey(id.toString())) {
            return LOOT_POOL_CACHE.get(id.toString());
        }

        ServerLevel level = context.getLevel();
        HolderLookup.Provider wrapperLookup = level.registryAccess();

        List<LootPool.Builder> poolBuilders = ShulkerStuffNeoForge.LOOT_TABLE_MODIFIER.generatePoolBuilders(id, wrapperLookup);
        List<LootPool> lootPools = poolBuilders.stream().map(LootPool.Builder::build).toList();

        LOOT_POOL_CACHE.put(id.toString(), lootPools);

        return lootPools;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Identifier id = context.getQueriedLootTableId();
        List<LootPool> lootPools = getPoolBuilders(id, context);

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
