package com.chimericdream.miniblockmerchants.neoforge.loot;

import com.chimericdream.miniblockmerchants.loot.MMLootTables;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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

public class VillagerConversionItemsLootModifier extends LootModifier {
    private static final Map<String, List<LootPool>> LOOT_POOL_CACHE = new HashMap<>();

    public static final Supplier<MapCodec<VillagerConversionItemsLootModifier>> CODEC = Suppliers.memoize(
        () -> RecordCodecBuilder
            .mapCodec(
                instance -> VillagerConversionItemsLootModifier.codecStart(instance)
                    .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                    .apply(instance, VillagerConversionItemsLootModifier::new)
            )
    );
    private final Item item;

    public VillagerConversionItemsLootModifier(LootItemCondition[] conditionsIn, Item item) {
        super(conditionsIn);
        this.item = item;
    }

    protected static List<LootPool> getPoolBuilders(ResourceLocation id, LootContext context) {
        if (LOOT_POOL_CACHE.containsKey(id.toString())) {
            return LOOT_POOL_CACHE.get(id.toString());
        }

        ServerLevel world = context.getLevel();
        MinecraftServer server = world.getServer();
        HolderLookup.Provider wrapperLookup = server.getServerResources().managers().getRegistryLookup();

        List<LootPool.Builder> poolBuilders = MMLootTables.generatePoolbuilders(id, wrapperLookup);
        List<LootPool> lootPools = poolBuilders.stream().map(LootPool.Builder::build).toList();

        LOOT_POOL_CACHE.put(id.toString(), lootPools);

        return lootPools;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation id = context.getQueriedLootTableId();
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
