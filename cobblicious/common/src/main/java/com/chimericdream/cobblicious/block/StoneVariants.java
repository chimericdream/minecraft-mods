package com.chimericdream.cobblicious.block;

import com.chimericdream.cobblicious.ModInfo;
import com.chimericdream.lib.blocks.family.BlockFamily;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.cobblicious.CobbliciousMod.REGISTRY_HELPER;

/**
 * The mossy, crimson, and warped versions of vanilla stone blocks. Each one is a full block with a
 * stairs, slab, and wall. A mossy block is its base block and a vine. A crimson block is its base
 * block and weeping vines. A warped block is its base block and twisting vines.
 */
public final class StoneVariants {
    // No tab here: CreativeTabs places these items at specific points in the vanilla tabs.
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties();

    /**
     * @param path       the registry path of the full block
     * @param name       the English name of the full block
     * @param base       the vanilla block that this block copies its properties and recipe from
     * @param vine       the vanilla block that the recipe adds to the base block
     * @param anchor     the vanilla item that this block goes after in the creative tab
     * @param topTexture true if the block has a {@code <path>_top} texture for its top and bottom faces
     */
    public record StoneVariant(
        String path,
        String name,
        Block base,
        Block vine,
        Item anchor,
        boolean topTexture,
        RegistrySupplier<Block> block,
        BlockFamily family
    ) {
    }

    // Blocks that share an anchor go after it in this order.
    public static final List<StoneVariant> ALL = List.of(
        variant("mossy_andesite", "Mossy Andesite", Blocks.ANDESITE, Blocks.VINE, Items.ANDESITE_WALL, false),

        variant("crimson_blackstone", "Crimson Blackstone", Blocks.BLACKSTONE, Blocks.WEEPING_VINES, Items.BLACKSTONE_WALL, true),
        variant("warped_blackstone", "Warped Blackstone", Blocks.BLACKSTONE, Blocks.TWISTING_VINES, Items.BLACKSTONE_WALL, true),

        variant("crimson_polished_blackstone_bricks", "Crimson Polished Blackstone Bricks", Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.WEEPING_VINES, Items.POLISHED_BLACKSTONE_BRICK_WALL, false),
        variant("warped_polished_blackstone_bricks", "Warped Polished Blackstone Bricks", Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.TWISTING_VINES, Items.POLISHED_BLACKSTONE_BRICK_WALL, false),

        variant("crimson_cracked_polished_blackstone_bricks", "Crimson Cracked Polished Blackstone Bricks", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.WEEPING_VINES, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, false),
        variant("warped_cracked_polished_blackstone_bricks", "Warped Cracked Polished Blackstone Bricks", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.TWISTING_VINES, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, false),

        variant("crimson_cracked_nether_bricks", "Crimson Cracked Nether Bricks", Blocks.CRACKED_NETHER_BRICKS, Blocks.WEEPING_VINES, Items.CRACKED_NETHER_BRICKS, false),
        variant("warped_cracked_nether_bricks", "Warped Cracked Nether Bricks", Blocks.CRACKED_NETHER_BRICKS, Blocks.TWISTING_VINES, Items.CRACKED_NETHER_BRICKS, false),

        variant("mossy_bricks", "Mossy Bricks", Blocks.BRICKS, Blocks.VINE, Items.BRICK_WALL, false),
        variant("mossy_cinnabar_bricks", "Mossy Cinnabar Bricks", Blocks.CINNABAR_BRICKS, Blocks.VINE, Items.CINNABAR_BRICK_WALL, false),
        variant("mossy_tuff_bricks", "Mossy Tuff Bricks", Blocks.TUFF_BRICKS, Blocks.VINE, Items.TUFF_BRICK_WALL, false),
        variant("mossy_cracked_stone_bricks", "Mossy Cracked Stone Bricks", Blocks.CRACKED_STONE_BRICKS, Blocks.VINE, Items.CRACKED_STONE_BRICKS, false),

        variant("mossy_cobbled_deepslate", "Mossy Cobbled Deepslate", Blocks.COBBLED_DEEPSLATE, Blocks.VINE, Items.COBBLED_DEEPSLATE_WALL, false),
        variant("mossy_deepslate_bricks", "Mossy Deepslate Bricks", Blocks.DEEPSLATE_BRICKS, Blocks.VINE, Items.DEEPSLATE_BRICK_WALL, false),
        variant("mossy_cracked_deepslate_bricks", "Mossy Cracked Deepslate Bricks", Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.VINE, Items.CRACKED_DEEPSLATE_BRICKS, false),
        variant("mossy_deepslate_tiles", "Mossy Deepslate Tiles", Blocks.DEEPSLATE_TILES, Blocks.VINE, Items.DEEPSLATE_TILE_WALL, false),
        variant("mossy_cracked_deepslate_tiles", "Mossy Cracked Deepslate Tiles", Blocks.CRACKED_DEEPSLATE_TILES, Blocks.VINE, Items.CRACKED_DEEPSLATE_TILES, false)
    );

    private static StoneVariant variant(String path, String name, Block base, Block vine, Item anchor, boolean topTexture) {
        Identifier id = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);

        RegistrySupplier<Block> block = REGISTRY_HELPER.registerWithItem(
            id,
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(base).setId(REGISTRY_HELPER.makeBlockRegistryKey(id))),
            DEFAULT_SETTINGS
        );

        // The English names of the full blocks are plural ("Mossy Bricks"). The family adds its own
        // suffix to a singular name ("Mossy Brick Stairs"), so remove the "s".
        String familyName = name.endsWith("s") ? name.substring(0, name.length() - 1) : name;

        return new StoneVariant(path, name, base, vine, anchor, topTexture, block, BlockFamilies.family(path, familyName, block, topTexture));
    }

    public static void init() {
    }
}
