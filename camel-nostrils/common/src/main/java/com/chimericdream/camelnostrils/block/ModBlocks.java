package com.chimericdream.camelnostrils.block;

import com.chimericdream.camelnostrils.block.entity.UpsideDownChestBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.chimericdream.camelnostrils.CamelNostrilsMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> LIVNA_BLOCK = REGISTRY_HELPER.registerWithItem(
        LivnaBlock.BLOCK_ID,
        LivnaBlock::create,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(LivnaBlock.ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> CHIPPED_LIVNA_BLOCK = REGISTRY_HELPER.registerWithItem(
        LivnaBlock.CHIPPED_BLOCK_ID,
        LivnaBlock::createChipped,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(LivnaBlock.CHIPPED_ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> DAMAGED_LIVNA_BLOCK = REGISTRY_HELPER.registerWithItem(
        LivnaBlock.DAMAGED_BLOCK_ID,
        LivnaBlock::createDamaged,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(LivnaBlock.DAMAGED_ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> GOLDEN_CACTUS = REGISTRY_HELPER.registerWithItem(
        GoldenCactusBlock.BLOCK_ID,
        GoldenCactusBlock::create,
        new Item.Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS).useBlockDescriptionPrefix().setId(GoldenCactusBlock.ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> UPSIDE_DOWN_CRAFTING_TABLE = REGISTRY_HELPER.registerWithItem(
        UpsideDownCraftingTableBlock.BLOCK_ID,
        UpsideDownCraftingTableBlock::create,
        new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).useBlockDescriptionPrefix().setId(UpsideDownCraftingTableBlock.ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> UPSIDE_DOWN_CHEST = REGISTRY_HELPER.registerWithItem(
        UpsideDownChestBlock.BLOCK_ID,
        UpsideDownChestBlock::create,
        new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).useBlockDescriptionPrefix().setId(UpsideDownChestBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<BlockEntityType<UpsideDownChestBlockEntity>> UPSIDE_DOWN_CHEST_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "upside_down_chest_block_entity",
        () -> new BlockEntityType<>(UpsideDownChestBlockEntity::new, Set.of(UPSIDE_DOWN_CHEST.get()))
    );

    public static final Map<DyeColor, RegistrySupplier<Block>> UPSIDE_DOWN_BEDS = registerUpsideDownBeds();

    @SuppressWarnings("UnstableApiUsage")
    private static Map<DyeColor, RegistrySupplier<Block>> registerUpsideDownBeds() {
        Map<DyeColor, RegistrySupplier<Block>> beds = new EnumMap<>(DyeColor.class);

        for (DyeColor color : DyeColor.values()) {
            RegistrySupplier<Block> bed = REGISTRY_HELPER.registerBlock(
                UpsideDownBedBlock.blockId(color),
                () -> UpsideDownBedBlock.create(color)
            );

            REGISTRY_HELPER.registerItem(
                UpsideDownBedBlock.blockId(color),
                () -> new BedItem(
                    bed.get(),
                    new Item.Properties()
                        .arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                        .useBlockDescriptionPrefix()
                        .setId(UpsideDownBedBlock.itemRegistryKey(color))
                        .stacksTo(1)
                )
            );

            beds.put(color, bed);
        }

        return beds;
    }

    public static void init() {
    }
}
