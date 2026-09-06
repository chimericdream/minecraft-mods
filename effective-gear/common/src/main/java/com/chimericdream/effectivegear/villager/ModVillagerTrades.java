package com.chimericdream.effectivegear.villager;

import com.chimericdream.effectivegear.ModInfo;
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
    public static final ResourceKey<VillagerTrade> NETHERITE_UPGRADE_TEMPLATE = trimKey("netherite_upgrade");
    public static final ResourceKey<VillagerTrade> BOLT_ARMOR_TRIM = trimKey("bolt");
    public static final ResourceKey<VillagerTrade> COAST_ARMOR_TRIM = trimKey("coast");
    public static final ResourceKey<VillagerTrade> DUNE_ARMOR_TRIM = trimKey("dune");
    public static final ResourceKey<VillagerTrade> EYE_ARMOR_TRIM = trimKey("eye");
    public static final ResourceKey<VillagerTrade> FLOW_ARMOR_TRIM = trimKey("flow");
    public static final ResourceKey<VillagerTrade> HOST_ARMOR_TRIM = trimKey("host");
    public static final ResourceKey<VillagerTrade> RAISER_ARMOR_TRIM = trimKey("raiser");
    public static final ResourceKey<VillagerTrade> RIB_ARMOR_TRIM = trimKey("rib");
    public static final ResourceKey<VillagerTrade> SENTRY_ARMOR_TRIM = trimKey("sentry");
    public static final ResourceKey<VillagerTrade> SHAPER_ARMOR_TRIM = trimKey("shaper");
    public static final ResourceKey<VillagerTrade> SILENCE_ARMOR_TRIM = trimKey("silence");
    public static final ResourceKey<VillagerTrade> SNOUT_ARMOR_TRIM = trimKey("snout");
    public static final ResourceKey<VillagerTrade> SPIRE_ARMOR_TRIM = trimKey("spire");
    public static final ResourceKey<VillagerTrade> TIDE_ARMOR_TRIM = trimKey("tide");
    public static final ResourceKey<VillagerTrade> VEX_ARMOR_TRIM = trimKey("vex");
    public static final ResourceKey<VillagerTrade> WARD_ARMOR_TRIM = trimKey("ward");
    public static final ResourceKey<VillagerTrade> WAYFINDER_ARMOR_TRIM = trimKey("wayfinder");
    public static final ResourceKey<VillagerTrade> WILD_ARMOR_TRIM = trimKey("wild");

    public static final List<ResourceKey<VillagerTrade>> WANDERING_TRADER_TRIM_TRADES = List.of(
        NETHERITE_UPGRADE_TEMPLATE,
        BOLT_ARMOR_TRIM,
        COAST_ARMOR_TRIM,
        DUNE_ARMOR_TRIM,
        EYE_ARMOR_TRIM,
        FLOW_ARMOR_TRIM,
        HOST_ARMOR_TRIM,
        RAISER_ARMOR_TRIM,
        RIB_ARMOR_TRIM,
        SENTRY_ARMOR_TRIM,
        SHAPER_ARMOR_TRIM,
        SILENCE_ARMOR_TRIM,
        SNOUT_ARMOR_TRIM,
        SPIRE_ARMOR_TRIM,
        TIDE_ARMOR_TRIM,
        VEX_ARMOR_TRIM,
        WARD_ARMOR_TRIM,
        WAYFINDER_ARMOR_TRIM,
        WILD_ARMOR_TRIM
    );

    public static void init() {
    }

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        registerTrim(context, NETHERITE_UPGRADE_TEMPLATE, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Items.NETHERRACK);
        registerTrim(context, BOLT_ARMOR_TRIM, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COPPER_BLOCK);
        registerTrim(context, COAST_ARMOR_TRIM, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        registerTrim(context, DUNE_ARMOR_TRIM, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SANDSTONE);
        registerTrim(context, EYE_ARMOR_TRIM, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.END_STONE);
        registerTrim(context, FLOW_ARMOR_TRIM, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BREEZE_ROD);
        registerTrim(context, HOST_ARMOR_TRIM, Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        registerTrim(context, RAISER_ARMOR_TRIM, Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        registerTrim(context, RIB_ARMOR_TRIM, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.NETHERRACK);
        registerTrim(context, SENTRY_ARMOR_TRIM, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        registerTrim(context, SHAPER_ARMOR_TRIM, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        registerTrim(context, SILENCE_ARMOR_TRIM, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
        registerTrim(context, SNOUT_ARMOR_TRIM, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BLACKSTONE);
        registerTrim(context, SPIRE_ARMOR_TRIM, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PURPUR_BLOCK);
        registerTrim(context, TIDE_ARMOR_TRIM, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PRISMARINE);
        registerTrim(context, VEX_ARMOR_TRIM, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        registerTrim(context, WARD_ARMOR_TRIM, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
        registerTrim(context, WAYFINDER_ARMOR_TRIM, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        registerTrim(context, WILD_ARMOR_TRIM, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.MOSSY_COBBLESTONE);
    }

    private static void registerTrim(BootstrapContext<VillagerTrade> context, ResourceKey<VillagerTrade> key, Item template, Item ingredient) {
        context.register(
            key,
            new VillagerTrade(
                new TradeCost(Items.DIAMOND, 7),
                Optional.of(new TradeCost(ingredient, 1)),
                new ItemStackTemplate(template),
                1,
                1,
                0f,
                Optional.empty(),
                List.of()
            )
        );
    }

    private static ResourceKey<VillagerTrade> trimKey(String name) {
        return ResourceKey.create(
            Registries.VILLAGER_TRADE,
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wandering_trader/trims/" + name)
        );
    }
}
