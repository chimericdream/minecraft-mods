package com.chimericdream.butwhatabout.block;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.blocks.family.BlockFamily;
import com.chimericdream.lib.blocks.family.BlockFamilyVariant;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.chimericdream.butwhatabout.ButWhatAboutMod.REGISTRY_HELPER;

/**
 * The stairs/slab/wall trio for each vanilla block Mojang left without them, declared via
 * chimeric-lib's {@link BlockFamily} so hardness/sounds/texture are all copied straight from the
 * vanilla ingredient block instead of being restated by hand.
 */
public class BlockFamilies {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS);

    public static final BlockFamily CALCITE = family("calcite", "Calcite", Blocks.CALCITE);
    public static final BlockFamily CRACKED_DEEPSLATE_BRICKS = family("cracked_deepslate_bricks", "Cracked Deepslate Brick", Blocks.CRACKED_DEEPSLATE_BRICKS);
    public static final BlockFamily CRACKED_DEEPSLATE_TILES = family("cracked_deepslate_tiles", "Cracked Deepslate Tile", Blocks.CRACKED_DEEPSLATE_TILES);
    public static final BlockFamily CRACKED_NETHER_BRICKS = family("cracked_nether_bricks", "Cracked Nether Brick", Blocks.CRACKED_NETHER_BRICKS);
    public static final BlockFamily CRACKED_POLISHED_BLACKSTONE_BRICKS = family("cracked_polished_blackstone_bricks", "Cracked Polished Blackstone Brick", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
    public static final BlockFamily CRACKED_STONE_BRICKS = family("cracked_stone_bricks", "Cracked Stone Brick", Blocks.CRACKED_STONE_BRICKS);
    public static final BlockFamily END_STONE = family("end_stone", "End Stone", Blocks.END_STONE);
    public static final BlockFamily NETHERRACK = family("netherrack", "Netherrack", Blocks.NETHERRACK);
    public static final BlockFamily SMOOTH_BASALT = family("smooth_basalt", "Smooth Basalt", Blocks.SMOOTH_BASALT);

    public static final List<BlockFamily> ALL = List.of(
        CALCITE,
        CRACKED_DEEPSLATE_BRICKS,
        CRACKED_DEEPSLATE_TILES,
        CRACKED_NETHER_BRICKS,
        CRACKED_POLISHED_BLACKSTONE_BRICKS,
        CRACKED_STONE_BRICKS,
        END_STONE,
        NETHERRACK,
        SMOOTH_BASALT
    );

    private static BlockFamily family(String material, String materialName, Block ingredient) {
        return BlockFamily.builder(REGISTRY_HELPER, material, new BlockConfig()
                .materialName(materialName)
                .ingredient(ingredient)
                .tool(Tool.PICKAXE)
                .texture(TextureUtils.block(ingredient)))
            .variants(BlockFamilyVariant.STAIRS, BlockFamilyVariant.SLAB, BlockFamilyVariant.WALL)
            .itemSettings(DEFAULT_SETTINGS)
            .build();
    }

    public static void init() {
    }
}
