package com.chimericdream.pannotiacompanion.villager;

import com.chimericdream.pannotiacompanion.ModInfo;
import com.chimericdream.pannotiacompanion.tag.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Optional;

public class ModTradeSets {
    public static final ResourceKey<TradeSet> WANDERING_TRADER_MINI_BLOCKS = key("wandering_trader_mini_blocks");
    public static final ResourceKey<TradeSet> WANDERING_TRADER_STREAMER_HEADS = key("wandering_trader_streamer_heads");

    public static void bootstrap(BootstrapContext<TradeSet> context) {
        register(context, WANDERING_TRADER_MINI_BLOCKS, ModTags.WANDERING_TRADER_MINI_BLOCKS, 15, 21);
        register(context, WANDERING_TRADER_STREAMER_HEADS, ModTags.WANDERING_TRADER_STREAMER_HEADS, 2, 5);
    }

    private static void register(
        BootstrapContext<TradeSet> context,
        ResourceKey<TradeSet> key,
        TagKey<VillagerTrade> trades,
        int min,
        int max
    ) {
        context.register(
            key,
            new TradeSet(
                context.lookup(Registries.VILLAGER_TRADE).getOrThrow(trades),
                UniformGenerator.between(min, max),
                false,
                Optional.of(key.identifier().withPrefix("trade_set/"))
            )
        );
    }

    private static ResourceKey<TradeSet> key(String name) {
        return ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name));
    }
}
