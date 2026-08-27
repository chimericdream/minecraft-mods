package com.chimericdream.butwhatabout.block;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.blocks.family.BlockFamily;
import com.chimericdream.lib.blocks.family.BlockFamilyVariant;
import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
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
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties COLORED_BLOCKS_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.COLORED_BLOCKS);

    public static final BlockFamily CALCITE = family("calcite", "Calcite", Blocks.CALCITE);
    public static final BlockFamily CHISELED_COPPER = family("chiseled_copper", "Chiseled Copper", Blocks.CHISELED_COPPER.weathering().unaffected());
    public static final BlockFamily EXPOSED_CHISELED_COPPER = family("exposed_chiseled_copper", "Exposed Chiseled Copper", Blocks.CHISELED_COPPER.weathering().exposed());
    public static final BlockFamily WEATHERED_CHISELED_COPPER = family("weathered_chiseled_copper", "Weathered Chiseled Copper", Blocks.CHISELED_COPPER.weathering().weathered());
    public static final BlockFamily OXIDIZED_CHISELED_COPPER = family("oxidized_chiseled_copper", "Oxidized Chiseled Copper", Blocks.CHISELED_COPPER.weathering().oxidized());
    public static final BlockFamily WAXED_CHISELED_COPPER = family("waxed_chiseled_copper", "Waxed Chiseled Copper", Blocks.CHISELED_COPPER.waxed().unaffected(), Blocks.CHISELED_COPPER.weathering().unaffected());
    public static final BlockFamily WAXED_EXPOSED_CHISELED_COPPER = family("waxed_exposed_chiseled_copper", "Waxed Exposed Chiseled Copper", Blocks.CHISELED_COPPER.waxed().exposed(), Blocks.CHISELED_COPPER.weathering().exposed());
    public static final BlockFamily WAXED_WEATHERED_CHISELED_COPPER = family("waxed_weathered_chiseled_copper", "Waxed Weathered Chiseled Copper", Blocks.CHISELED_COPPER.waxed().weathered(), Blocks.CHISELED_COPPER.weathering().weathered());
    public static final BlockFamily WAXED_OXIDIZED_CHISELED_COPPER = family("waxed_oxidized_chiseled_copper", "Waxed Oxidized Chiseled Copper", Blocks.CHISELED_COPPER.waxed().oxidized(), Blocks.CHISELED_COPPER.weathering().oxidized());
    public static final BlockFamily CHISELED_DEEPSLATE = family("chiseled_deepslate", "Chiseled Deepslate", Blocks.CHISELED_DEEPSLATE);
    public static final BlockFamily CHISELED_NETHER_BRICKS = family("chiseled_nether_bricks", "Chiseled Nether Brick", Blocks.CHISELED_NETHER_BRICKS);
    public static final BlockFamily CHISELED_POLISHED_BLACKSTONE = family("chiseled_polished_blackstone", "Chiseled Polished Blackstone", Blocks.CHISELED_POLISHED_BLACKSTONE);
    /**
     * Vanilla's {@code sandstone_stairs}/{@code sandstone_slab} recipes also accept chiseled sandstone
     * as an ingredient (since Mojang never gave it a dedicated stairs/slab). Now that we register our
     * own {@code chiseled_sandstone_stairs}/{@code chiseled_sandstone_slab} recipes, that vanilla
     * ingredient alternate is redundant and ambiguous. Fabric's recipe datagen API forces every
     * generated recipe id onto this mod's own namespace (see
     * {@code FabricRecipeProvider#getRecipeIdentifier}), so it can't be used to override a
     * {@code minecraft:}-namespaced recipe; instead the two vanilla recipes are overridden directly via
     * hand-authored resources at
     * {@code common/src/main/resources/data/minecraft/recipe/sandstone_{stairs,slab}.json}, which drop
     * chiseled sandstone from the ingredient list (cut sandstone is left in place for stairs, since it
     * doesn't get its own stairs recipe here).
     */
    public static final BlockFamily CHISELED_SANDSTONE = family("chiseled_sandstone", "Chiseled Sandstone", Blocks.CHISELED_SANDSTONE);
    /**
     * Same situation as {@link #CHISELED_SANDSTONE} above, but for
     * {@code red_sandstone_stairs}/{@code red_sandstone_slab} and chiseled red sandstone.
     */
    public static final BlockFamily CHISELED_RED_SANDSTONE = family("chiseled_red_sandstone", "Chiseled Red Sandstone", Blocks.CHISELED_RED_SANDSTONE);
    public static final BlockFamily CHISELED_RESIN_BRICKS = family("chiseled_resin_bricks", "Chiseled Resin Brick", Blocks.CHISELED_RESIN_BRICKS);
    public static final BlockFamily CHISELED_STONE_BRICKS = family("chiseled_stone_bricks", "Chiseled Stone Brick", Blocks.CHISELED_STONE_BRICKS);
    public static final BlockFamily CHISELED_SULFUR = family("chiseled_sulfur", "Chiseled Sulfur", Blocks.CHISELED_SULFUR);
    public static final BlockFamily CRACKED_DEEPSLATE_BRICKS = family("cracked_deepslate_bricks", "Cracked Deepslate Brick", Blocks.CRACKED_DEEPSLATE_BRICKS);
    public static final BlockFamily CRACKED_DEEPSLATE_TILES = family("cracked_deepslate_tiles", "Cracked Deepslate Tile", Blocks.CRACKED_DEEPSLATE_TILES);
    public static final BlockFamily CRACKED_NETHER_BRICKS = family("cracked_nether_bricks", "Cracked Nether Brick", Blocks.CRACKED_NETHER_BRICKS);
    public static final BlockFamily CRACKED_POLISHED_BLACKSTONE_BRICKS = family("cracked_polished_blackstone_bricks", "Cracked Polished Blackstone Brick", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
    public static final BlockFamily CRACKED_STONE_BRICKS = family("cracked_stone_bricks", "Cracked Stone Brick", Blocks.CRACKED_STONE_BRICKS);
    public static final BlockFamily END_STONE = family("end_stone", "End Stone", Blocks.END_STONE);
    public static final BlockFamily NETHERRACK = family("netherrack", "Netherrack", Blocks.NETHERRACK);
    public static final BlockFamily SMOOTH_BASALT = family("smooth_basalt", "Smooth Basalt", Blocks.SMOOTH_BASALT);

    /**
     * Vanilla concrete gets the full stairs/slab/wall trio, like any other solid stone-like block.
     * Placed in the Colored Blocks tab (like vanilla concrete itself) instead of Building Blocks.
     */
    public static final List<BlockFamily> CONCRETE = concreteFamilies();

    public static final List<BlockFamily> ALL = Stream.of(
            List.of(
                CALCITE,
                CHISELED_COPPER,
                EXPOSED_CHISELED_COPPER,
                WEATHERED_CHISELED_COPPER,
                OXIDIZED_CHISELED_COPPER,
                WAXED_CHISELED_COPPER,
                WAXED_EXPOSED_CHISELED_COPPER,
                WAXED_WEATHERED_CHISELED_COPPER,
                WAXED_OXIDIZED_CHISELED_COPPER,
                CHISELED_DEEPSLATE,
                CHISELED_NETHER_BRICKS,
                CHISELED_POLISHED_BLACKSTONE,
                CHISELED_RED_SANDSTONE,
                CHISELED_RESIN_BRICKS,
                CHISELED_SANDSTONE,
                CHISELED_STONE_BRICKS,
                CHISELED_SULFUR,
                CRACKED_DEEPSLATE_BRICKS,
                CRACKED_DEEPSLATE_TILES,
                CRACKED_NETHER_BRICKS,
                CRACKED_POLISHED_BLACKSTONE_BRICKS,
                CRACKED_STONE_BRICKS,
                END_STONE,
                NETHERRACK,
                SMOOTH_BASALT
            ),
            CONCRETE
        )
        .flatMap(List::stream)
        .toList();

    private static List<BlockFamily> concreteFamilies() {
        List<BlockFamily> families = new ArrayList<>();

        for (DyeColor color : DyeColor.values()) {
            families.add(family(
                color.getSerializedName() + "_concrete",
                ColorHelpers.getName(color) + " Concrete",
                Blocks.CONCRETE.pick(color),
                Tool.PICKAXE,
                COLORED_BLOCKS_SETTINGS,
                BlockFamilyVariant.STAIRS, BlockFamilyVariant.SLAB, BlockFamilyVariant.WALL
            ));
        }

        return families;
    }

    private static BlockFamily family(String material, String materialName, Block ingredient) {
        return family(material, materialName, ingredient, TextureUtils.block(ingredient));
    }

    private static BlockFamily family(String material, String materialName, Block ingredient, Block textureBlock) {
        return family(material, materialName, ingredient, TextureUtils.block(textureBlock));
    }

    private static BlockFamily family(String material, String materialName, Block ingredient, Identifier texture) {
        return family(material, materialName, ingredient, texture, Tool.PICKAXE, DEFAULT_SETTINGS, BlockFamilyVariant.STAIRS, BlockFamilyVariant.SLAB, BlockFamilyVariant.WALL);
    }

    private static BlockFamily family(String material, String materialName, Block ingredient, Tool tool, Item.Properties itemSettings, BlockFamilyVariant... variants) {
        return family(material, materialName, ingredient, TextureUtils.block(ingredient), tool, itemSettings, variants);
    }

    private static BlockFamily family(String material, String materialName, Block ingredient, Identifier texture, Tool tool, Item.Properties itemSettings, BlockFamilyVariant... variants) {
        return BlockFamily.builder(REGISTRY_HELPER, material, new BlockConfig()
                .materialName(materialName)
                .ingredient(ingredient)
                .tool(tool)
                .texture(texture))
            .variants(variants)
            .itemSettings(itemSettings)
            .build();
    }

    public static void init() {
    }
}
