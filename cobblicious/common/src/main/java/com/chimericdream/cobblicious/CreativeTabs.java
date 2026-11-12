package com.chimericdream.cobblicious;

import com.chimericdream.cobblicious.block.BlockFamilies;
import com.chimericdream.cobblicious.block.ModBlocks;
import com.chimericdream.cobblicious.crop.ModCrops;
import com.chimericdream.lib.blocks.family.BlockFamily;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.CreativeTabOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * Places each block at a fixed spot in a vanilla creative tab, instead of at the end of the tab.
 */
public final class CreativeTabs {
    private CreativeTabs() {
    }

    public static void init() {
        CreativeTabRegistry.modify(CreativeTabRegistry.defer(CreativeModeTabs.BUILDING_BLOCKS), (flags, output, op) -> {
            List<ItemLike> basalt = new ArrayList<>();
            basalt.add(ModBlocks.CHISELED_BASALT_BRICKS.get());
            addFamily(basalt, ModBlocks.BASALT_BRICKS, BlockFamilies.BASALT_BRICKS);
            addFamily(basalt, ModBlocks.CRACKED_BASALT_BRICKS, BlockFamilies.CRACKED_BASALT_BRICKS);
            addFamily(basalt, ModBlocks.MOSSY_BASALT_BRICKS, BlockFamilies.MOSSY_BASALT_BRICKS);
            addFamily(basalt, ModBlocks.CRIMSON_BASALT_BRICKS, BlockFamilies.CRIMSON_BASALT_BRICKS);
            addFamily(basalt, ModBlocks.WARPED_BASALT_BRICKS, BlockFamilies.WARPED_BASALT_BRICKS);
            insertChain(output, Items.POLISHED_BASALT, basalt);

            List<ItemLike> netherBricks = new ArrayList<>();
            addFamily(netherBricks, ModBlocks.WARPED_NETHER_BRICKS, BlockFamilies.WARPED_NETHER_BRICKS);
            insertChain(output, Items.RED_NETHER_BRICK_WALL, netherBricks);
        });

        CreativeTabRegistry.modify(CreativeTabRegistry.defer(CreativeModeTabs.NATURAL_BLOCKS), (flags, output, op) ->
            output.acceptAfter(Items.NETHER_WART, ModCrops.WARPED_WART_ITEM.get())
        );
    }

    /**
     * Adds the base block, then its stairs, slab, and wall.
     */
    private static void addFamily(List<ItemLike> list, Supplier<Block> base, BlockFamily family) {
        list.add(base.get());
        family.getStairs().ifPresent(b -> list.add(b.get()));
        family.getSlab().ifPresent(b -> list.add(b.get()));
        family.getWall().ifPresent(b -> list.add(b.get()));
    }

    /**
     * Inserts the items in order, each one directly after the previous one. Inserting them all
     * after the same anchor would reverse them.
     */
    private static void insertChain(CreativeTabOutput output, ItemLike anchor, List<ItemLike> items) {
        ItemLike previous = anchor;
        for (ItemLike item : items) {
            output.acceptAfter(new ItemStack(previous), new ItemStack(item));
            previous = item;
        }
    }
}
