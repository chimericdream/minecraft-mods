package com.chimericdream.minekea.block.building;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.blocks.family.BlockFamily;
import com.chimericdream.lib.blocks.family.BlockFamilyVariant;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.general.BasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.CrackedBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.CrimsonBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.MossyBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.WarpedBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.WarpedNetherBricksBlock;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

/**
 * The stairs/slab/wall trio for each basalt-brick material, declared once via chimeric-lib's
 * {@link BlockFamily} instead of the three near-identical {@code BlockConfig} declarations these
 * used to need across {@code Stairs}/{@code Slabs}/{@code Walls}. IDs are pinned to the ones those
 * classes already shipped ({@code building/stairs|slabs|walls/<material>}) so existing worlds,
 * recipes, and loot tables are unaffected.
 */
public class BasaltBrickFamilies implements ModThingGroup {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS);

    public static final BlockFamily BASALT_BRICKS = family("basalt_bricks", "Basalt Brick", BuildingBlocks.BASALT_BRICKS, TextureUtils.block(BasaltBricksBlock.BLOCK_ID));
    public static final BlockFamily CRACKED_BASALT_BRICKS = family("cracked_basalt_bricks", "Cracked Basalt Brick", BuildingBlocks.CRACKED_BASALT_BRICKS, TextureUtils.block(CrackedBasaltBricksBlock.BLOCK_ID));
    public static final BlockFamily CRIMSON_BASALT_BRICKS = family("crimson_basalt_bricks", "Crimson Basalt Brick", BuildingBlocks.CRIMSON_BASALT_BRICKS, TextureUtils.block(CrimsonBasaltBricksBlock.BLOCK_ID));
    public static final BlockFamily MOSSY_BASALT_BRICKS = family("mossy_basalt_bricks", "Mossy Basalt Brick", BuildingBlocks.MOSSY_BASALT_BRICKS, TextureUtils.block(MossyBasaltBricksBlock.BLOCK_ID));
    public static final BlockFamily WARPED_BASALT_BRICKS = family("warped_basalt_bricks", "Warped Basalt Brick", BuildingBlocks.WARPED_BASALT_BRICKS, TextureUtils.block(WarpedBasaltBricksBlock.BLOCK_ID));
    public static final BlockFamily WARPED_NETHER_BRICKS = family("warped_nether_bricks", "Warped Nether Brick", BuildingBlocks.WARPED_NETHER_BRICKS, TextureUtils.block(WarpedNetherBricksBlock.BLOCK_ID));

    public static final List<BlockFamily> ALL = List.of(
        BASALT_BRICKS,
        CRACKED_BASALT_BRICKS,
        CRIMSON_BASALT_BRICKS,
        MOSSY_BASALT_BRICKS,
        WARPED_BASALT_BRICKS,
        WARPED_NETHER_BRICKS
    );

    private static BlockFamily family(String material, String materialName, RegistrySupplier<Block> ingredient, Identifier texture) {
        return BlockFamily.builder(REGISTRY_HELPER, material, new BlockConfig()
                .materialName(materialName)
                .ingredient(ingredient)
                .texture(texture))
            .variants(BlockFamilyVariant.STAIRS, BlockFamilyVariant.SLAB, BlockFamilyVariant.WALL)
            .itemSettings(DEFAULT_SETTINGS)
            .stairsId(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/stairs/%s", material)))
            .slabId(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/slabs/%s", material)))
            .wallId(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/walls/%s", material)))
            .build();
    }
}
