package com.chimericdream.cobblicious.block;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.blocks.family.BlockFamily;
import com.chimericdream.lib.blocks.family.BlockFamilyVariant;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.cobblicious.CobbliciousMod.REGISTRY_HELPER;

/**
 * The stairs, slab, and wall of each full block in {@link ModBlocks}. Chiseled basalt bricks have
 * no family.
 */
public class BlockFamilies {
    // No tab here: CreativeTabs places these items at specific points in the vanilla tabs.
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties();

    public static final BlockFamily BASALT_BRICKS = family("basalt_bricks", "Basalt Brick", ModBlocks.BASALT_BRICKS, false);
    public static final BlockFamily CRACKED_BASALT_BRICKS = family("cracked_basalt_bricks", "Cracked Basalt Brick", ModBlocks.CRACKED_BASALT_BRICKS, false);
    public static final BlockFamily CRIMSON_BASALT_BRICKS = family("crimson_basalt_bricks", "Crimson Basalt Brick", ModBlocks.CRIMSON_BASALT_BRICKS, false);
    public static final BlockFamily MOSSY_BASALT_BRICKS = family("mossy_basalt_bricks", "Mossy Basalt Brick", ModBlocks.MOSSY_BASALT_BRICKS, false);
    public static final BlockFamily WARPED_BASALT_BRICKS = family("warped_basalt_bricks", "Warped Basalt Brick", ModBlocks.WARPED_BASALT_BRICKS, false);
    public static final BlockFamily WARPED_NETHER_BRICKS = family("warped_nether_bricks", "Warped Nether Brick", ModBlocks.WARPED_NETHER_BRICKS, false);

    public static final List<BlockFamily> ALL = List.of(
        BASALT_BRICKS,
        CRACKED_BASALT_BRICKS,
        CRIMSON_BASALT_BRICKS,
        MOSSY_BASALT_BRICKS,
        WARPED_BASALT_BRICKS,
        WARPED_NETHER_BRICKS
    );

    /**
     * @param topTexture true if the block has a {@code _top} texture. The stairs and slab use it for
     *                   their top and bottom faces.
     */
    static BlockFamily family(String material, String materialName, RegistrySupplier<Block> ingredient, boolean topTexture) {
        Identifier blockId = ingredient.getId();
        BlockConfig config = new BlockConfig()
            .materialName(materialName)
            .ingredient(ingredient)
            .tool(Tool.PICKAXE)
            .texture(TextureUtils.block(blockId));

        if (topTexture) {
            config.texture("top", TextureUtils.block(blockId, "_top"));
        }

        return BlockFamily.builder(REGISTRY_HELPER, material, config)
            .variants(BlockFamilyVariant.STAIRS, BlockFamilyVariant.SLAB, BlockFamilyVariant.WALL)
            .itemSettings(DEFAULT_SETTINGS)
            .build();
    }

    public static void init() {
    }
}
