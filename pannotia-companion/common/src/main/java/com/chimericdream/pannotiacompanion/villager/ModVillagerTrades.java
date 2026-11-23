package com.chimericdream.pannotiacompanion.villager;

import com.chimericdream.pannotiacompanion.ModInfo;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.List;
import java.util.Optional;

public class ModVillagerTrades {
    private static final int MINI_BLOCK_USES = 1;
    private static final int MINI_BLOCK_HEADS = 8;
    private static final int STREAMER_HEAD_USES = 3;

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        for (WanderingTraderHeadData.HeadTrade head : WanderingTraderHeadData.MINI_BLOCKS) {
            Item block = BuiltInRegistries.ITEM.getValue(Identifier.parse(head.extraCost()));
            register(context, "mini_blocks", head, new TradeCost(block, 1), MINI_BLOCK_HEADS, MINI_BLOCK_USES);
        }

        for (WanderingTraderHeadData.HeadTrade head : WanderingTraderHeadData.STREAMER_HEADS) {
            register(context, "streamer_heads", head, null, 1, STREAMER_HEAD_USES);
        }
    }

    private static void register(
        BootstrapContext<VillagerTrade> context,
        String group,
        WanderingTraderHeadData.HeadTrade head,
        TradeCost extraCost,
        int count,
        int maxUses
    ) {
        context.register(
            ResourceKey.create(
                Registries.VILLAGER_TRADE,
                Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wandering_trader/" + group + "/" + head.key())
            ),
            new VillagerTrade(
                new TradeCost(Items.EMERALD, 1),
                Optional.ofNullable(extraCost),
                new ItemStackTemplate(Items.PLAYER_HEAD.builtInRegistryHolder(), count, headComponents(head)),
                maxUses,
                0,
                0f,
                Optional.empty(),
                List.of()
            )
        );
    }

    private static DataComponentPatch headComponents(WanderingTraderHeadData.HeadTrade head) {
        // The same components as a head from a loot table, so it stacks with those and shows its own name.
        return DataComponentPatch.builder()
            .set(DataComponents.ITEM_NAME, Component.literal(head.name()))
            .set(DataComponents.PROFILE, PannotiaHeads.textureOnlyProfile(head.texture()))
            .build();
    }
}
