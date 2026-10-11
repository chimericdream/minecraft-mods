package com.chimericdream.allhallowssteve.villager;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.List;
import java.util.Optional;

/**
 * Villager trades are data-driven in 26.2. This trade only exists as data; it joins the vanilla farmer
 * level 3 (Journeyman) pool through {@code data/minecraft/tags/villager_trade/farmer/level_3.json}.
 */
public class ModVillagerTrades {
    public static final ResourceKey<VillagerTrade> FARMER_EMERALD_NUTMEG = ResourceKey.create(
        Registries.VILLAGER_TRADE,
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "farmer/3/emerald_nutmeg")
    );

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        context.register(
            FARMER_EMERALD_NUTMEG,
            new VillagerTrade(
                new TradeCost(Items.EMERALD, 1),
                new ItemStackTemplate(ModItems.NUTMEG.get(), 2),
                12,
                10,
                0.05f,
                Optional.empty(),
                List.of()
            )
        );
    }
}
