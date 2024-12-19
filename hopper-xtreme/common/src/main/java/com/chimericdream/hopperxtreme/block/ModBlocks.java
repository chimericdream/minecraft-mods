package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.client.screen.FilteredGlazedHopperScreenHandler;
import com.chimericdream.hopperxtreme.client.screen.FilteredHopperScreenHandler;
import com.chimericdream.hopperxtreme.client.screen.GlazedHopperScreenHandler;
import com.chimericdream.hopperxtreme.entity.GlazedHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.GlazedMultiHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeHupperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeMultiHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeMultiHupperBlockEntity;
import com.chimericdream.hopperxtreme.registry.ModItemGroups;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

import java.util.Set;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class ModBlocks {
    public static final RegistrySupplier<Block> HONEYED_HOPPER = registerHopper("honeyed_hopper", 20);
    public static final RegistrySupplier<Block> COPPER_HOPPER = registerHopper("copper_hopper", 8);
    public static final RegistrySupplier<Block> GOLDEN_HOPPER = registerHopper("golden_hopper", 4);
    public static final RegistrySupplier<Block> DIAMOND_HOPPER = registerHopper("diamond_hopper", 2);
    public static final RegistrySupplier<Block> NETHERITE_HOPPER = registerHopper("netherite_hopper", 1);

    public static final RegistrySupplier<Block> GLAZED_HOPPER = registerGlazedHopper("glazed_hopper", 8);
    public static final RegistrySupplier<Block> HONEY_GLAZED_HOPPER = registerGlazedHopper("honey_glazed_hopper", 20);
    public static final RegistrySupplier<Block> GLAZED_GOLDEN_HOPPER = registerGlazedHopper("glazed_golden_hopper", 4);
    public static final RegistrySupplier<Block> GLAZED_DIAMOND_HOPPER = registerGlazedHopper("glazed_diamond_hopper", 2);
    public static final RegistrySupplier<Block> GLAZED_NETHERITE_HOPPER = registerGlazedHopper("glazed_netherite_hopper", 1);

    public static final RegistrySupplier<Block> MULTI_HOPPER = registerMultiHopper("multi_hopper", 8);
    public static final RegistrySupplier<Block> GOLDEN_MULTI_HOPPER = registerMultiHopper("golden_multi_hopper", 4);
    public static final RegistrySupplier<Block> DIAMOND_MULTI_HOPPER = registerMultiHopper("diamond_multi_hopper", 2);
    public static final RegistrySupplier<Block> NETHERITE_MULTI_HOPPER = registerMultiHopper("netherite_multi_hopper", 1);

    public static final RegistrySupplier<Block> GLAZED_MULTI_HOPPER = registerGlazedMultiHopper("glazed_multi_hopper", 8);
    public static final RegistrySupplier<Block> GLAZED_GOLDEN_MULTI_HOPPER = registerGlazedMultiHopper("glazed_golden_multi_hopper", 4);
    public static final RegistrySupplier<Block> GLAZED_DIAMOND_MULTI_HOPPER = registerGlazedMultiHopper("glazed_diamond_multi_hopper", 2);
    public static final RegistrySupplier<Block> GLAZED_NETHERITE_MULTI_HOPPER = registerGlazedMultiHopper("glazed_netherite_multi_hopper", 1);

    public static final RegistrySupplier<Block> HUPPER = registerHupper("hupper", 8);
    public static final RegistrySupplier<Block> HONEYED_HUPPER = registerHupper("honeyed_hupper", 20);
    public static final RegistrySupplier<Block> COPPER_HUPPER = registerHupper("copper_hupper", 8);
    public static final RegistrySupplier<Block> GOLDEN_HUPPER = registerHupper("golden_hupper", 4);
    public static final RegistrySupplier<Block> DIAMOND_HUPPER = registerHupper("diamond_hupper", 2);
    public static final RegistrySupplier<Block> NETHERITE_HUPPER = registerHupper("netherite_hupper", 1);

    public static final RegistrySupplier<Block> MULTI_HUPPER = registerMultiHupper("multi_hupper", 8);
    public static final RegistrySupplier<Block> GOLDEN_MULTI_HUPPER = registerMultiHupper("golden_multi_hupper", 4);
    public static final RegistrySupplier<Block> DIAMOND_MULTI_HUPPER = registerMultiHupper("diamond_multi_hupper", 2);
    public static final RegistrySupplier<Block> NETHERITE_MULTI_HUPPER = registerMultiHupper("netherite_multi_hupper", 1);

    public static final RegistrySupplier<Block> FILTERED_GOLDEN_HOPPER = registerHopper("filtered_golden_hopper", 4, true);
    public static final RegistrySupplier<Block> FILTERED_DIAMOND_HOPPER = registerHopper("filtered_diamond_hopper", 2, true);
    public static final RegistrySupplier<Block> FILTERED_NETHERITE_HOPPER = registerHopper("filtered_netherite_hopper", 1, true);

    public static final RegistrySupplier<Block> FILTERED_GLAZED_GOLDEN_HOPPER = registerGlazedHopper("filtered_glazed_golden_hopper", 4, true);
    public static final RegistrySupplier<Block> FILTERED_GLAZED_DIAMOND_HOPPER = registerGlazedHopper("filtered_glazed_diamond_hopper", 2, true);
    public static final RegistrySupplier<Block> FILTERED_GLAZED_NETHERITE_HOPPER = registerGlazedHopper("filtered_glazed_netherite_hopper", 1, true);

    public static final RegistrySupplier<Block> FILTERED_GOLDEN_MULTI_HOPPER = registerMultiHopper("filtered_golden_multi_hopper", 4, true);
    public static final RegistrySupplier<Block> FILTERED_DIAMOND_MULTI_HOPPER = registerMultiHopper("filtered_diamond_multi_hopper", 2, true);
    public static final RegistrySupplier<Block> FILTERED_NETHERITE_MULTI_HOPPER = registerMultiHopper("filtered_netherite_multi_hopper", 1, true);

    public static final RegistrySupplier<Block> FILTERED_GLAZED_GOLDEN_MULTI_HOPPER = registerGlazedMultiHopper("filtered_glazed_golden_multi_hopper", 4, true);
    public static final RegistrySupplier<Block> FILTERED_GLAZED_DIAMOND_MULTI_HOPPER = registerGlazedMultiHopper("filtered_glazed_diamond_multi_hopper", 2, true);
    public static final RegistrySupplier<Block> FILTERED_GLAZED_NETHERITE_MULTI_HOPPER = registerGlazedMultiHopper("filtered_glazed_netherite_multi_hopper", 1, true);

    public static final RegistrySupplier<Block> FILTERED_GOLDEN_HUPPER = registerHupper("filtered_golden_hupper", 4, true);
    public static final RegistrySupplier<Block> FILTERED_DIAMOND_HUPPER = registerHupper("filtered_diamond_hupper", 2, true);
    public static final RegistrySupplier<Block> FILTERED_NETHERITE_HUPPER = registerHupper("filtered_netherite_hupper", 1, true);

    public static final RegistrySupplier<Block> FILTERED_GOLDEN_MULTI_HUPPER = registerMultiHupper("filtered_golden_multi_hupper", 4, true);
    public static final RegistrySupplier<Block> FILTERED_DIAMOND_MULTI_HUPPER = registerMultiHupper("filtered_diamond_multi_hupper", 2, true);
    public static final RegistrySupplier<Block> FILTERED_NETHERITE_MULTI_HUPPER = registerMultiHupper("filtered_netherite_multi_hupper", 1, true);

    public static final RegistrySupplier<BlockEntityType<GlazedHopperBlockEntity>> GLAZED_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "glazed_hopper_block_entity",
        () -> new BlockEntityType<>(
            GlazedHopperBlockEntity::new,
            Set.of(
                GLAZED_HOPPER.get(),
                HONEY_GLAZED_HOPPER.get(),
                GLAZED_GOLDEN_HOPPER.get(),
                GLAZED_DIAMOND_HOPPER.get(),
                GLAZED_NETHERITE_HOPPER.get(),
                FILTERED_GLAZED_GOLDEN_HOPPER.get(),
                FILTERED_GLAZED_DIAMOND_HOPPER.get(),
                FILTERED_GLAZED_NETHERITE_HOPPER.get()
            )
        )
    );

    public static final RegistrySupplier<BlockEntityType<XtremeHopperBlockEntity>> XTREME_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_hopper_block_entity",
        () -> new BlockEntityType<>(
            XtremeHopperBlockEntity::new,
            Set.of(
                HONEYED_HOPPER.get(),
                COPPER_HOPPER.get(),
                GOLDEN_HOPPER.get(),
                DIAMOND_HOPPER.get(),
                NETHERITE_HOPPER.get(),
                FILTERED_GOLDEN_HOPPER.get(),
                FILTERED_DIAMOND_HOPPER.get(),
                FILTERED_NETHERITE_HOPPER.get()
            )
        )
    );

    public static final RegistrySupplier<BlockEntityType<XtremeHupperBlockEntity>> XTREME_HUPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_hupper_block_entity",
        () -> new BlockEntityType<>(
            XtremeHupperBlockEntity::new,
            Set.of(
                HUPPER.get(),
                HONEYED_HUPPER.get(),
                COPPER_HUPPER.get(),
                GOLDEN_HUPPER.get(),
                DIAMOND_HUPPER.get(),
                NETHERITE_HUPPER.get(),
                FILTERED_GOLDEN_HUPPER.get(),
                FILTERED_DIAMOND_HUPPER.get(),
                FILTERED_NETHERITE_HUPPER.get()
            )
        )
    );

    public static final RegistrySupplier<BlockEntityType<GlazedMultiHopperBlockEntity>> GLAZED_MULTI_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "glazed_multi_hopper_block_entity",
        () -> new BlockEntityType<>(
            GlazedMultiHopperBlockEntity::new,
            Set.of(
                GLAZED_MULTI_HOPPER.get(),
                GLAZED_GOLDEN_MULTI_HOPPER.get(),
                GLAZED_DIAMOND_MULTI_HOPPER.get(),
                GLAZED_NETHERITE_MULTI_HOPPER.get(),
                FILTERED_GLAZED_GOLDEN_MULTI_HOPPER.get(),
                FILTERED_GLAZED_DIAMOND_MULTI_HOPPER.get(),
                FILTERED_GLAZED_NETHERITE_MULTI_HOPPER.get()
            )
        )
    );

    public static final RegistrySupplier<BlockEntityType<XtremeMultiHopperBlockEntity>> XTREME_MULTI_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_multi_hopper_block_entity",
        () -> new BlockEntityType<>(
            XtremeMultiHopperBlockEntity::new,
            Set.of(
                MULTI_HOPPER.get(),
                GOLDEN_MULTI_HOPPER.get(),
                DIAMOND_MULTI_HOPPER.get(),
                NETHERITE_MULTI_HOPPER.get(),
                FILTERED_GOLDEN_MULTI_HOPPER.get(),
                FILTERED_DIAMOND_MULTI_HOPPER.get(),
                FILTERED_NETHERITE_MULTI_HOPPER.get()
            )
        )
    );

    public static final RegistrySupplier<BlockEntityType<XtremeMultiHupperBlockEntity>> XTREME_MULTI_HUPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_multi_hupper_block_entity",
        () -> new BlockEntityType<>(
            XtremeMultiHupperBlockEntity::new,
            Set.of(
                MULTI_HUPPER.get(),
                GOLDEN_MULTI_HUPPER.get(),
                DIAMOND_MULTI_HUPPER.get(),
                NETHERITE_MULTI_HUPPER.get(),
                FILTERED_GOLDEN_MULTI_HUPPER.get(),
                FILTERED_DIAMOND_MULTI_HUPPER.get(),
                FILTERED_NETHERITE_MULTI_HUPPER.get()
            )
        )
    );

    public static final RegistrySupplier<ScreenHandlerType<GlazedHopperScreenHandler>> GLAZED_HOPPER_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(
        GlazedHopperScreenHandler.SCREEN_ID,
        () -> new ScreenHandlerType<>(GlazedHopperScreenHandler::new, FeatureSet.empty())
    );

    public static final RegistrySupplier<ScreenHandlerType<FilteredGlazedHopperScreenHandler>> FILTERED_GLAZED_HOPPER_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(
        FilteredGlazedHopperScreenHandler.SCREEN_ID,
        () -> new ScreenHandlerType<>(FilteredGlazedHopperScreenHandler::new, FeatureSet.empty())
    );

    public static final RegistrySupplier<ScreenHandlerType<FilteredHopperScreenHandler>> FILTERED_HOPPER_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(
        FilteredHopperScreenHandler.SCREEN_ID,
        () -> new ScreenHandlerType<>(FilteredHopperScreenHandler::new, FeatureSet.empty())
    );

    public static void init() {
    }

    private static RegistrySupplier<Block> registerHopper(String key, int ticks) {
        return registerHopper(key, ticks, false);
    }

    private static RegistrySupplier<Block> registerHopper(String key, int ticks, boolean withFilter) {
        return REGISTRY_HELPER.registerWithItem(
            REGISTRY_HELPER.makeId(key),
            () -> new XtremeHopperBlock(ticks, key, withFilter),
            getDefaultHopperSettings().registryKey(REGISTRY_HELPER.makeItemRegistryKey(key))
        );
    }

    private static RegistrySupplier<Block> registerGlazedHopper(String key, int ticks) {
        return registerGlazedHopper(key, ticks, false);
    }

    private static RegistrySupplier<Block> registerGlazedHopper(String key, int ticks, boolean withFilter) {
        return REGISTRY_HELPER.registerWithItem(
            REGISTRY_HELPER.makeId(key),
            () -> new GlazedHopperBlock(ticks, key, withFilter),
            getDefaultHopperSettings().registryKey(REGISTRY_HELPER.makeItemRegistryKey(key))
        );
    }

    private static RegistrySupplier<Block> registerMultiHopper(String key, int ticks) {
        return registerMultiHopper(key, ticks, false);
    }

    private static RegistrySupplier<Block> registerMultiHopper(String key, int ticks, boolean withFilter) {
        return REGISTRY_HELPER.registerWithItem(
            REGISTRY_HELPER.makeId(key),
            () -> new XtremeMultiHopperBlock(ticks, key, withFilter),
            getDefaultMultiHopperSettings().registryKey(REGISTRY_HELPER.makeItemRegistryKey(key))
        );
    }

    private static RegistrySupplier<Block> registerGlazedMultiHopper(String key, int ticks) {
        return registerGlazedMultiHopper(key, ticks, false);
    }

    private static RegistrySupplier<Block> registerGlazedMultiHopper(String key, int ticks, boolean withFilter) {
        return REGISTRY_HELPER.registerWithItem(
            REGISTRY_HELPER.makeId(key),
            () -> new GlazedMultiHopperBlock(ticks, key, withFilter),
            getDefaultMultiHopperSettings().registryKey(REGISTRY_HELPER.makeItemRegistryKey(key))
        );
    }

    private static RegistrySupplier<Block> registerHupper(String key, int ticks) {
        return registerHupper(key, ticks, false);
    }

    private static RegistrySupplier<Block> registerHupper(String key, int ticks, boolean withFilter) {
        return REGISTRY_HELPER.registerWithItem(
            REGISTRY_HELPER.makeId(key),
            () -> new XtremeHupperBlock(ticks, key, withFilter),
            getDefaultHupperSettings().registryKey(REGISTRY_HELPER.makeItemRegistryKey(key))
        );
    }

    private static RegistrySupplier<Block> registerMultiHupper(String key, int ticks) {
        return registerMultiHupper(key, ticks, false);
    }

    private static RegistrySupplier<Block> registerMultiHupper(String key, int ticks, boolean withFilter) {
        return REGISTRY_HELPER.registerWithItem(
            REGISTRY_HELPER.makeId(key),
            () -> new XtremeMultiHupperBlock(ticks, key, withFilter),
            getDefaultMultiHupperSettings().registryKey(REGISTRY_HELPER.makeItemRegistryKey(key))
        );
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Item.Settings getDefaultHopperSettings() {
        return new Item.Settings().arch$tab(ModItemGroups.HOPPER_ITEM_GROUP).useBlockPrefixedTranslationKey();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Item.Settings getDefaultMultiHopperSettings() {
        return new Item.Settings().arch$tab(ModItemGroups.MULTI_HOPPER_ITEM_GROUP).useBlockPrefixedTranslationKey();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Item.Settings getDefaultHupperSettings() {
        return new Item.Settings().arch$tab(ModItemGroups.HUPPER_ITEM_GROUP).useBlockPrefixedTranslationKey();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Item.Settings getDefaultMultiHupperSettings() {
        return new Item.Settings().arch$tab(ModItemGroups.MULTI_HUPPER_ITEM_GROUP).useBlockPrefixedTranslationKey();
    }
}
