package com.chimericdream.sponj.blocks;

import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.sponj.SponjMod.REGISTRY_HELPER;

public class ModBlocks {
    /**
     * How many connected sponjes may contribute to a single absorption.
     *
     * <p>This is the knob that keeps sponj from lagging the server, and it is intentionally small.
     * A sponj clears liquid outward with a radius of {@code 6 + 3*(n-1)} and a hard budget of
     * {@code 64 * n} cleared blocks, where {@code n} is the number of connected sponjes. Both grow
     * with {@code n}, so the worst-case work per absorption is roughly {@code 64 * n} block updates
     * in one tick. Left uncapped, a large sponj wall (potentially thousands of blocks) would try to
     * clear tens of thousands of liquid/washable blocks at once.
     *
     * <p>Capping {@code n} here bounds that worst case at about {@code 64 * MAX_CONNECTED_SPONJES}
     * block updates. Raising this makes bigger sponj arrays reach further and drain faster at the
     * cost of a higher per-tick spike; lowering it is safer but limits how much a multi-sponj build
     * helps. A single sponj ({@code n = 1}) still behaves like vanilla (radius 6, 64 blocks).
     */
    public static final int MAX_CONNECTED_SPONJES = 16;

    public static final RegistrySupplier<Block> SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        SponjBlock.BLOCK_ID,
        SponjBlock::new,
        getDefaultItemSettings().setId(SponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> WET_SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        WetSponjBlock.BLOCK_ID,
        WetSponjBlock::new,
        getDefaultItemSettings().setId(WetSponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> LAVA_SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        LavaSponjBlock.BLOCK_ID,
        LavaSponjBlock::new,
        getDefaultItemSettings().setId(LavaSponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> WET_LAVA_SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        WetLavaSponjBlock.BLOCK_ID,
        WetLavaSponjBlock::new,
        getDefaultItemSettings().setId(WetLavaSponjBlock.ITEM_REGISTRY_KEY)
        // Disabled until I can figure out how this works on NeoForge
        // getDefaultItemSettings().recipeRemainder(LAVA_SPONJ_BLOCK.get().asItem()).registryKey(WetLavaSponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final List<RegistrySupplier<Block>> SPONJ_BLOCKS = List.of(
        SPONJ_BLOCK,
        WET_SPONJ_BLOCK
    );

    public static final List<RegistrySupplier<Block>> LAVA_SPONJ_BLOCKS = List.of(
        LAVA_SPONJ_BLOCK,
        WET_LAVA_SPONJ_BLOCK
    );

    public static void init() {
        // Disabled until I can figure out cross-loader fuel registries as well as how to have a recipe remainder in the furnace with a stackable item
        // // Wet lava sponjes can smelt 128 items!
        // FuelRegistry.register(25600, WET_LAVA_SPONJ_BLOCK.get());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static Item.Properties getDefaultItemSettings() {
        return new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).useBlockDescriptionPrefix();
    }

    public static List<Block> getSponjBlocks() {
        return SPONJ_BLOCKS.stream().map(RegistrySupplier::get).toList();
    }

    public static List<Block> getLavaSponjBlocks() {
        return LAVA_SPONJ_BLOCKS.stream().map(RegistrySupplier::get).toList();
    }
}
