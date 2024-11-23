package com.chimericdream.shulkerstuff.block;

import com.chimericdream.shulkerstuff.block.entity.DyeStationBlockEntity;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreenHandler;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL);

    public static final RegistrySupplier<Block> DYE_STATION = REGISTRY_HELPER.registerWithItem("dye_station", DyeStationBlock::new, DEFAULT_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<DyeStationBlockEntity>> DYE_STATION_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        DyeStationBlockEntity.ENTITY_ID,
        () -> BlockEntityType.Builder.create(
            DyeStationBlockEntity::new,
            DYE_STATION.get()
        ).build(null)
    );

    public static final RegistrySupplier<ScreenHandlerType<DyeStationScreenHandler>> DYE_STATION_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(
        DyeStationScreenHandler.SCREEN_ID,
        () -> new ScreenHandlerType<>(DyeStationScreenHandler::new, FeatureSet.empty())
    );

    public static void init() {
    }
}
