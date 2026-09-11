package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.block.entity.CarvingStationBlockEntity;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreenHandler;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties FUNCTIONAL_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties NATURAL_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS);

    public static final RegistrySupplier<Block> CARVING_STATION = REGISTRY_HELPER.registerWithItem(CarvingStationBlock.BLOCK_ID, CarvingStationBlock::new, FUNCTIONAL_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<CarvingStationBlockEntity>> CARVING_STATION_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        CarvingStationBlockEntity.ENTITY_ID,
        () -> new BlockEntityType<>(
            CarvingStationBlockEntity::new,
            Set.of(CARVING_STATION.get())
        )
    );

    public static final RegistrySupplier<MenuType<CarvingStationScreenHandler>> CARVING_STATION_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(
        CarvingStationScreenHandler.SCREEN_ID,
        () -> new MenuType<>(CarvingStationScreenHandler::new, FeatureFlagSet.of())
    );

    public static final RegistrySupplier<Block> DECORATED_PUMPKIN = REGISTRY_HELPER.registerWithItem(DecoratedPumpkinBlock.BLOCK_ID, DecoratedPumpkinBlock::new, NATURAL_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<DecoratedPumpkinBlockEntity>> DECORATED_PUMPKIN_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        DecoratedPumpkinBlockEntity.ENTITY_ID,
        () -> new BlockEntityType<>(
            DecoratedPumpkinBlockEntity::new,
            Set.of(DECORATED_PUMPKIN.get())
        )
    );

    public static void init() {
    }
}
