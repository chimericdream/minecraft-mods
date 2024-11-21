package com.chimericdream.shulkerstuff.neoforge.loot;

import com.chimericdream.shulkerstuff.neoforge.ShulkerStuffNeoForge;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
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
                    .and(Registries.ITEM.getCodec().fieldOf("item").forGetter(m -> m.item))
                    .apply(instance, ShulkerStuffLootModifier::new)
            )
    );
    private final Item item;

    public ShulkerStuffLootModifier(LootCondition[] conditionsIn, Item item) {
        super(conditionsIn);
        this.item = item;
    }

    protected static List<LootPool> getPoolBuilders(Identifier id, LootContext context) {
        if (LOOT_POOL_CACHE.containsKey(id.toString())) {
            return LOOT_POOL_CACHE.get(id.toString());
        }

        ServerWorld world = context.getWorld();
        MinecraftServer server = world.getServer();
        RegistryWrapper.WrapperLookup wrapperLookup = server.getServerResources().dataPackContents().getRegistryLookup();

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
            pool.addGeneratedLoot(generatedLoot::add, context);
        }

        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
