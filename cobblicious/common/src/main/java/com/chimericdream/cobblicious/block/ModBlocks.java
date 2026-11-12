package com.chimericdream.cobblicious.block;

import com.chimericdream.cobblicious.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.cobblicious.CobbliciousMod.REGISTRY_HELPER;

/**
 * The full blocks of this mod. {@link BlockFamilies} adds the stairs, slab, and wall of each block.
 */
public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS);

    public static final Identifier BASALT_BRICKS_ID = id("basalt_bricks");
    public static final Identifier CHISELED_BASALT_BRICKS_ID = id("chiseled_basalt_bricks");
    public static final Identifier CRACKED_BASALT_BRICKS_ID = id("cracked_basalt_bricks");
    public static final Identifier CRIMSON_BASALT_BRICKS_ID = id("crimson_basalt_bricks");
    public static final Identifier MOSSY_BASALT_BRICKS_ID = id("mossy_basalt_bricks");
    public static final Identifier WARPED_BASALT_BRICKS_ID = id("warped_basalt_bricks");
    public static final Identifier WARPED_NETHER_BRICKS_ID = id("warped_nether_bricks");

    public static final RegistrySupplier<Block> BASALT_BRICKS = basalt(BASALT_BRICKS_ID);
    public static final RegistrySupplier<Block> CHISELED_BASALT_BRICKS = basalt(CHISELED_BASALT_BRICKS_ID);
    public static final RegistrySupplier<Block> CRACKED_BASALT_BRICKS = basalt(CRACKED_BASALT_BRICKS_ID);
    public static final RegistrySupplier<Block> CRIMSON_BASALT_BRICKS = basalt(CRIMSON_BASALT_BRICKS_ID);
    public static final RegistrySupplier<Block> MOSSY_BASALT_BRICKS = basalt(MOSSY_BASALT_BRICKS_ID);
    public static final RegistrySupplier<Block> WARPED_BASALT_BRICKS = basalt(WARPED_BASALT_BRICKS_ID);
    public static final RegistrySupplier<Block> WARPED_NETHER_BRICKS = register(
        WARPED_NETHER_BRICKS_ID,
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS).setId(REGISTRY_HELPER.makeBlockRegistryKey(WARPED_NETHER_BRICKS_ID)))
    );

    public static final List<RegistrySupplier<Block>> ALL = List.of(
        BASALT_BRICKS,
        CHISELED_BASALT_BRICKS,
        CRACKED_BASALT_BRICKS,
        CRIMSON_BASALT_BRICKS,
        MOSSY_BASALT_BRICKS,
        WARPED_BASALT_BRICKS,
        WARPED_NETHER_BRICKS
    );

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }

    private static RegistrySupplier<Block> basalt(Identifier id) {
        return register(id, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_BASALT).setId(REGISTRY_HELPER.makeBlockRegistryKey(id))));
    }

    private static RegistrySupplier<Block> register(Identifier id, Supplier<Block> supplier) {
        return REGISTRY_HELPER.registerWithItem(id, supplier, DEFAULT_SETTINGS);
    }

    public static void init() {
    }
}
