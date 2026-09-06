package com.chimericdream.archaeologytweaks.villager;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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
    public static final ResourceKey<VillagerTrade> ANGLER_POTTERY_SHERD = sherdKey("angler");
    public static final ResourceKey<VillagerTrade> ARCHER_POTTERY_SHERD = sherdKey("archer");
    public static final ResourceKey<VillagerTrade> ARMS_UP_POTTERY_SHERD = sherdKey("arms_up");
    public static final ResourceKey<VillagerTrade> BLADE_POTTERY_SHERD = sherdKey("blade");
    public static final ResourceKey<VillagerTrade> BREWER_POTTERY_SHERD = sherdKey("brewer");
    public static final ResourceKey<VillagerTrade> BURN_POTTERY_SHERD = sherdKey("burn");
    public static final ResourceKey<VillagerTrade> DANGER_POTTERY_SHERD = sherdKey("danger");
    public static final ResourceKey<VillagerTrade> EXPLORER_POTTERY_SHERD = sherdKey("explorer");
    public static final ResourceKey<VillagerTrade> FLOW_POTTERY_SHERD = sherdKey("flow");
    public static final ResourceKey<VillagerTrade> FRIEND_POTTERY_SHERD = sherdKey("friend");
    public static final ResourceKey<VillagerTrade> GUSTER_POTTERY_SHERD = sherdKey("guster");
    public static final ResourceKey<VillagerTrade> HEART_POTTERY_SHERD = sherdKey("heart");
    public static final ResourceKey<VillagerTrade> HEARTBREAK_POTTERY_SHERD = sherdKey("heartbreak");
    public static final ResourceKey<VillagerTrade> HOWL_POTTERY_SHERD = sherdKey("howl");
    public static final ResourceKey<VillagerTrade> MINER_POTTERY_SHERD = sherdKey("miner");
    public static final ResourceKey<VillagerTrade> MOURNER_POTTERY_SHERD = sherdKey("mourner");
    public static final ResourceKey<VillagerTrade> PLENTY_POTTERY_SHERD = sherdKey("plenty");
    public static final ResourceKey<VillagerTrade> PRIZE_POTTERY_SHERD = sherdKey("prize");
    public static final ResourceKey<VillagerTrade> SCRAPE_POTTERY_SHERD = sherdKey("scrape");
    public static final ResourceKey<VillagerTrade> SHEAF_POTTERY_SHERD = sherdKey("sheaf");
    public static final ResourceKey<VillagerTrade> SHELTER_POTTERY_SHERD = sherdKey("shelter");
    public static final ResourceKey<VillagerTrade> SKULL_POTTERY_SHERD = sherdKey("skull");
    public static final ResourceKey<VillagerTrade> SNORT_POTTERY_SHERD = sherdKey("snort");

    public static final List<ResourceKey<VillagerTrade>> WANDERING_TRADER_SHERD_TRADES = List.of(
        ANGLER_POTTERY_SHERD,
        ARCHER_POTTERY_SHERD,
        ARMS_UP_POTTERY_SHERD,
        BLADE_POTTERY_SHERD,
        BREWER_POTTERY_SHERD,
        BURN_POTTERY_SHERD,
        DANGER_POTTERY_SHERD,
        EXPLORER_POTTERY_SHERD,
        FLOW_POTTERY_SHERD,
        FRIEND_POTTERY_SHERD,
        GUSTER_POTTERY_SHERD,
        HEART_POTTERY_SHERD,
        HEARTBREAK_POTTERY_SHERD,
        HOWL_POTTERY_SHERD,
        MINER_POTTERY_SHERD,
        MOURNER_POTTERY_SHERD,
        PLENTY_POTTERY_SHERD,
        PRIZE_POTTERY_SHERD,
        SCRAPE_POTTERY_SHERD,
        SHEAF_POTTERY_SHERD,
        SHELTER_POTTERY_SHERD,
        SKULL_POTTERY_SHERD,
        SNORT_POTTERY_SHERD
    );

    public static void init() {
    }

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        registerSherd(context, ANGLER_POTTERY_SHERD, Items.ANGLER_POTTERY_SHERD);
        registerSherd(context, ARCHER_POTTERY_SHERD, Items.ARCHER_POTTERY_SHERD);
        registerSherd(context, ARMS_UP_POTTERY_SHERD, Items.ARMS_UP_POTTERY_SHERD);
        registerSherd(context, BLADE_POTTERY_SHERD, Items.BLADE_POTTERY_SHERD);
        registerSherd(context, BREWER_POTTERY_SHERD, Items.BREWER_POTTERY_SHERD);
        registerSherd(context, BURN_POTTERY_SHERD, Items.BURN_POTTERY_SHERD);
        registerSherd(context, DANGER_POTTERY_SHERD, Items.DANGER_POTTERY_SHERD);
        registerSherd(context, EXPLORER_POTTERY_SHERD, Items.EXPLORER_POTTERY_SHERD);
        registerSherd(context, FLOW_POTTERY_SHERD, Items.FLOW_POTTERY_SHERD);
        registerSherd(context, FRIEND_POTTERY_SHERD, Items.FRIEND_POTTERY_SHERD);
        registerSherd(context, GUSTER_POTTERY_SHERD, Items.GUSTER_POTTERY_SHERD);
        registerSherd(context, HEART_POTTERY_SHERD, Items.HEART_POTTERY_SHERD);
        registerSherd(context, HEARTBREAK_POTTERY_SHERD, Items.HEARTBREAK_POTTERY_SHERD);
        registerSherd(context, HOWL_POTTERY_SHERD, Items.HOWL_POTTERY_SHERD);
        registerSherd(context, MINER_POTTERY_SHERD, Items.MINER_POTTERY_SHERD);
        registerSherd(context, MOURNER_POTTERY_SHERD, Items.MOURNER_POTTERY_SHERD);
        registerSherd(context, PLENTY_POTTERY_SHERD, Items.PLENTY_POTTERY_SHERD);
        registerSherd(context, PRIZE_POTTERY_SHERD, Items.PRIZE_POTTERY_SHERD);
        registerSherd(context, SCRAPE_POTTERY_SHERD, Items.SCRAPE_POTTERY_SHERD);
        registerSherd(context, SHEAF_POTTERY_SHERD, Items.SHEAF_POTTERY_SHERD);
        registerSherd(context, SHELTER_POTTERY_SHERD, Items.SHELTER_POTTERY_SHERD);
        registerSherd(context, SKULL_POTTERY_SHERD, Items.SKULL_POTTERY_SHERD);
        registerSherd(context, SNORT_POTTERY_SHERD, Items.SNORT_POTTERY_SHERD);
    }

    private static void registerSherd(BootstrapContext<VillagerTrade> context, ResourceKey<VillagerTrade> key, Item sherd) {
        context.register(
            key,
            new VillagerTrade(
                new TradeCost(Items.EMERALD, 8),
                Optional.of(new TradeCost(Items.BRICK, 1)),
                new ItemStackTemplate(sherd),
                1,
                1,
                0f,
                Optional.empty(),
                List.of()
            )
        );
    }

    private static ResourceKey<VillagerTrade> sherdKey(String name) {
        return ResourceKey.create(
            Registries.VILLAGER_TRADE,
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wandering_trader/sherds/" + name)
        );
    }
}
