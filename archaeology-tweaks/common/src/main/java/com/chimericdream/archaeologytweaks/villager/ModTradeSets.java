package com.chimericdream.archaeologytweaks.villager;

import com.chimericdream.archaeologytweaks.ModInfo;
import com.chimericdream.archaeologytweaks.tag.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Optional;

public class ModTradeSets {
    public static final ResourceKey<TradeSet> WANDERING_TRADER_TRIMS = ResourceKey.create(
        Registries.TRADE_SET,
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wandering_trader_trims")
    );

    public static void init() {
    }

    public static void bootstrap(BootstrapContext<TradeSet> context) {
        context.register(
            WANDERING_TRADER_TRIMS,
            new TradeSet(
                context.lookup(Registries.VILLAGER_TRADE).getOrThrow(ModTags.WANDERING_TRADER_TRIMS),
                BinomialDistributionGenerator.binomial(1, 0.2f),
                false,
                Optional.of(WANDERING_TRADER_TRIMS.identifier().withPrefix("trade_set/"))
            )
        );
    }
}
