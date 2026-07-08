package com.chimericdream.shulkerstuff.block;

import com.chimericdream.shulkerstuff.block.entity.DyeStationBlockEntity;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreenHandler;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    public static final RegistrySupplier<Block> DYE_STATION = REGISTRY_HELPER.registerWithItem(DyeStationBlock.BLOCK_ID, DyeStationBlock::new, DEFAULT_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<DyeStationBlockEntity>> DYE_STATION_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        DyeStationBlockEntity.ENTITY_ID,
        () -> new BlockEntityType<>(
            DyeStationBlockEntity::new,
            Set.of(DYE_STATION.get())
        )
    );

    public static final RegistrySupplier<MenuType<DyeStationScreenHandler>> DYE_STATION_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(
        DyeStationScreenHandler.SCREEN_ID,
        () -> new MenuType<>(DyeStationScreenHandler::new, FeatureFlagSet.of())
    );

    public static void init() {
    }
}
