package com.chimericdream.minekea.block.building.beams;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.building.BuildingBlocks;
import com.chimericdream.minekea.block.building.general.BasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.CrackedBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.CrimsonBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.MossyBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.WarpedBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.WarpedNetherBricksBlock;
import com.chimericdream.minekea.registry.ModRegistries;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.minekea.registry.ModRegistries.registerWithItem;

public class Beams implements ModThingGroup {
    public static final Item.Settings DEFAULT_BEAM_SETTINGS = new Item.Settings().arch$tab(ModRegistries.BEAMS_ITEM_GROUP);

    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();

    static {
        BLOCKS.add(registerWithItem(BeamBlock.makeId("amethyst"), () -> new BeamBlock(new BlockConfig().material("amethyst").materialName("Amethyst").ingredient(Blocks.AMETHYST_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("andesite"), () -> new BeamBlock(new BlockConfig().material("andesite").materialName("Andesite").ingredient(Blocks.ANDESITE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("basalt"), () -> new BeamBlock(new BlockConfig().material("basalt").materialName("Basalt").ingredient(Blocks.BASALT).texture(TextureUtils.block(Blocks.BASALT, "_side")).texture("end", TextureUtils.block(Blocks.BASALT, "_top"))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("basalt_brick"), () -> new BeamBlock(new BlockConfig().material("basalt_brick").materialName("Basalt Brick").ingredient(BuildingBlocks.BASALT_BRICKS.get()).texture(TextureUtils.block(BasaltBricksBlock.BLOCK_ID))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("blackstone"), () -> new BeamBlock(new BlockConfig().material("blackstone").materialName("Blackstone").ingredient(Blocks.BLACKSTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("bone"), () -> new BeamBlock(new BlockConfig().material("bone").materialName("Bone").ingredient(Blocks.BONE_BLOCK).texture(TextureUtils.block(Blocks.BONE_BLOCK, "_side")).texture("end", TextureUtils.block(Blocks.BONE_BLOCK, "_top"))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("brick"), () -> new BeamBlock(new BlockConfig().material("brick").materialName("Brick").ingredient(Blocks.BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("calcite"), () -> new BeamBlock(new BlockConfig().material("calcite").materialName("Calcite").ingredient(Blocks.CALCITE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cobbled_deepslate"), () -> new BeamBlock(new BlockConfig().material("cobbled_deepslate").materialName("Cobbled Deepslate").ingredient(Blocks.COBBLED_DEEPSLATE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cobblestone"), () -> new BeamBlock(new BlockConfig().material("cobblestone").materialName("Cobblestone").ingredient(Blocks.COBBLESTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cracked_basalt_brick"), () -> new BeamBlock(new BlockConfig().material("cracked_basalt_brick").materialName("Cracked Basalt Brick").ingredient(BuildingBlocks.CRACKED_BASALT_BRICKS.get()).texture(TextureUtils.block(CrackedBasaltBricksBlock.BLOCK_ID))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cracked_deepslate_brick"), () -> new BeamBlock(new BlockConfig().material("cracked_deepslate_brick").materialName("Cracked Deepslate Brick").ingredient(Blocks.CRACKED_DEEPSLATE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cracked_deepslate_tile"), () -> new BeamBlock(new BlockConfig().material("cracked_deepslate_tile").materialName("Cracked Deepslate Tile").ingredient(Blocks.CRACKED_DEEPSLATE_TILES)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cracked_stone_brick"), () -> new BeamBlock(new BlockConfig().material("cracked_stone_brick").materialName("Cracked Stone Brick").ingredient(Blocks.CRACKED_STONE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("crimson_basalt_brick"), () -> new BeamBlock(new BlockConfig().material("crimson_basalt_brick").materialName("Crimson Basalt Brick").ingredient(BuildingBlocks.CRIMSON_BASALT_BRICKS.get()).texture(TextureUtils.block(CrimsonBasaltBricksBlock.BLOCK_ID))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("crying_obsidian"), () -> new BeamBlock(new BlockConfig().material("crying_obsidian").materialName("Crying Obsidian").ingredient(Blocks.CRYING_OBSIDIAN)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cut_red_sandstone"), () -> new BeamBlock(new BlockConfig().material("cut_red_sandstone").materialName("Cut Red Sandstone").ingredient(Blocks.CUT_RED_SANDSTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cut_sandstone"), () -> new BeamBlock(new BlockConfig().material("cut_sandstone").materialName("Cut Sandstone").ingredient(Blocks.CUT_SANDSTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("dark_prismarine"), () -> new BeamBlock(new BlockConfig().material("dark_prismarine").materialName("Dark Prismarine").ingredient(Blocks.DARK_PRISMARINE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("deepslate"), () -> new BeamBlock(new BlockConfig().material("deepslate").materialName("Deepslate").ingredient(Blocks.DEEPSLATE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("deepslate_brick"), () -> new BeamBlock(new BlockConfig().material("deepslate_brick").materialName("Deepslate Brick").ingredient(Blocks.DEEPSLATE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("deepslate_tile"), () -> new BeamBlock(new BlockConfig().material("deepslate_tile").materialName("Deepslate Tile").ingredient(Blocks.DEEPSLATE_TILES)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("diorite"), () -> new BeamBlock(new BlockConfig().material("diorite").materialName("Diorite").ingredient(Blocks.DIORITE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("end_stone"), () -> new BeamBlock(new BlockConfig().material("end_stone").materialName("End Stone").ingredient(Blocks.END_STONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("end_stone_brick"), () -> new BeamBlock(new BlockConfig().material("end_stone_brick").materialName("End Stone Brick").ingredient(Blocks.END_STONE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("granite"), () -> new BeamBlock(new BlockConfig().material("granite").materialName("Granite").ingredient(Blocks.GRANITE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("mossy_basalt_brick"), () -> new BeamBlock(new BlockConfig().material("mossy_basalt_brick").materialName("Mossy Basalt Brick").ingredient(BuildingBlocks.MOSSY_BASALT_BRICKS.get()).texture(TextureUtils.block(MossyBasaltBricksBlock.BLOCK_ID))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("mossy_cobblestone"), () -> new BeamBlock(new BlockConfig().material("mossy_cobblestone").materialName("Mossy Cobblestone").ingredient(Blocks.MOSSY_COBBLESTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("mossy_stone_brick"), () -> new BeamBlock(new BlockConfig().material("mossy_stone_brick").materialName("Mossy Stone Brick").ingredient(Blocks.MOSSY_STONE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("mud_brick"), () -> new BeamBlock(new BlockConfig().material("mud_brick").materialName("Mud Brick").ingredient(Blocks.MUD_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("netherrack"), () -> new BeamBlock(new BlockConfig().material("netherrack").materialName("Netherrack").ingredient(Blocks.NETHERRACK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("nether_brick"), () -> new BeamBlock(new BlockConfig().material("nether_brick").materialName("Nether Brick").ingredient(Blocks.NETHER_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("obsidian"), () -> new BeamBlock(new BlockConfig().material("obsidian").materialName("Obsidian").ingredient(Blocks.OBSIDIAN)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("packed_mud"), () -> new BeamBlock(new BlockConfig().material("packed_mud").materialName("Packed Mud").ingredient(Blocks.PACKED_MUD)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("polished_andesite"), () -> new BeamBlock(new BlockConfig().material("polished_andesite").materialName("Polished Andesite").ingredient(Blocks.POLISHED_ANDESITE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("polished_basalt"), () -> new BeamBlock(new BlockConfig().material("polished_basalt").materialName("Polished Basalt").ingredient(Blocks.POLISHED_BASALT).texture(TextureUtils.block(Blocks.POLISHED_BASALT, "_side")).texture("end", TextureUtils.block(Blocks.POLISHED_BASALT, "_top"))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("polished_blackstone"), () -> new BeamBlock(new BlockConfig().material("polished_blackstone").materialName("Polished Blackstone").ingredient(Blocks.POLISHED_BLACKSTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("polished_blackstone_brick"), () -> new BeamBlock(new BlockConfig().material("polished_blackstone_brick").materialName("Polished Blackstone Brick").ingredient(Blocks.POLISHED_BLACKSTONE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("polished_deepslate"), () -> new BeamBlock(new BlockConfig().material("polished_deepslate").materialName("Polished Deepslate").ingredient(Blocks.POLISHED_DEEPSLATE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("polished_diorite"), () -> new BeamBlock(new BlockConfig().material("polished_diorite").materialName("Polished Diorite").ingredient(Blocks.POLISHED_DIORITE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("polished_granite"), () -> new BeamBlock(new BlockConfig().material("polished_granite").materialName("Polished Granite").ingredient(Blocks.POLISHED_GRANITE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("prismarine"), () -> new BeamBlock(new BlockConfig().material("prismarine").materialName("Prismarine").ingredient(Blocks.PRISMARINE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("prismarine_brick"), () -> new BeamBlock(new BlockConfig().material("prismarine_brick").materialName("Prismarine Brick").ingredient(Blocks.PRISMARINE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("purpur"), () -> new BeamBlock(new BlockConfig().material("purpur").materialName("Purpur").ingredient(Blocks.PURPUR_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("purpur_pillar"), () -> new BeamBlock(new BlockConfig().material("purpur_pillar").materialName("Purpur Pillar").ingredient(Blocks.PURPUR_PILLAR)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("quartz"), () -> new BeamBlock(new BlockConfig().material("quartz").materialName("Quartz").ingredient(Blocks.QUARTZ_BLOCK).texture(TextureUtils.block(Blocks.QUARTZ_BLOCK, "_top"))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("quartz_brick"), () -> new BeamBlock(new BlockConfig().material("quartz_brick").materialName("Quartz Brick").ingredient(Blocks.QUARTZ_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("red_nether_brick"), () -> new BeamBlock(new BlockConfig().material("red_nether_brick").materialName("Red Nether Brick").ingredient(Blocks.RED_NETHER_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("red_sandstone"), () -> new BeamBlock(new BlockConfig().material("red_sandstone").materialName("Red Sandstone").ingredient(Blocks.RED_SANDSTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("sandstone"), () -> new BeamBlock(new BlockConfig().material("sandstone").materialName("Sandstone").ingredient(Blocks.SANDSTONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("smooth_basalt"), () -> new BeamBlock(new BlockConfig().material("smooth_basalt").materialName("Smooth Basalt").ingredient(Blocks.SMOOTH_BASALT)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("smooth_quartz"), () -> new BeamBlock(new BlockConfig().material("smooth_quartz").materialName("Smooth Quartz").ingredient(Blocks.SMOOTH_QUARTZ).texture(TextureUtils.block(Blocks.QUARTZ_BLOCK, "_bottom"))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("smooth_red_sandstone"), () -> new BeamBlock(new BlockConfig().material("smooth_red_sandstone").materialName("Smooth Red Sandstone").ingredient(Blocks.SMOOTH_RED_SANDSTONE).texture(TextureUtils.block(Blocks.RED_SANDSTONE, "_top"))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("smooth_sandstone"), () -> new BeamBlock(new BlockConfig().material("smooth_sandstone").materialName("Smooth Sandstone").ingredient(Blocks.SMOOTH_SANDSTONE).texture(TextureUtils.block(Blocks.SANDSTONE, "_top"))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("smooth_stone"), () -> new BeamBlock(new BlockConfig().material("smooth_stone").materialName("Smooth Stone").ingredient(Blocks.SMOOTH_STONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("stone"), () -> new BeamBlock(new BlockConfig().material("stone").materialName("Stone").ingredient(Blocks.STONE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("stone_brick"), () -> new BeamBlock(new BlockConfig().material("stone_brick").materialName("Stone Brick").ingredient(Blocks.STONE_BRICKS)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("tuff"), () -> new BeamBlock(new BlockConfig().material("tuff").materialName("Tuff").ingredient(Blocks.TUFF)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("warped_basalt_brick"), () -> new BeamBlock(new BlockConfig().material("warped_basalt_brick").materialName("Warped Basalt Brick").ingredient(BuildingBlocks.WARPED_BASALT_BRICKS.get()).texture(TextureUtils.block(WarpedBasaltBricksBlock.BLOCK_ID))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("warped_nether_brick"), () -> new BeamBlock(new BlockConfig().material("warped_nether_brick").materialName("Warped Nether Brick").ingredient(BuildingBlocks.WARPED_NETHER_BRICKS.get()).texture(TextureUtils.block(WarpedNetherBricksBlock.BLOCK_ID))), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("copper_block"), () -> new BeamBlock(new BlockConfig().material("copper_block").materialName("Copper Block").ingredient(Blocks.COPPER_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("diamond_block"), () -> new BeamBlock(new BlockConfig().material("diamond_block").materialName("Diamond Block").ingredient(Blocks.DIAMOND_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("gold_block"), () -> new BeamBlock(new BlockConfig().material("gold_block").materialName("Gold Block").ingredient(Blocks.GOLD_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("iron_block"), () -> new BeamBlock(new BlockConfig().material("iron_block").materialName("Iron Block").ingredient(Blocks.IRON_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("lapis_block"), () -> new BeamBlock(new BlockConfig().material("lapis_block").materialName("Lapis Block").ingredient(Blocks.LAPIS_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("netherite_block"), () -> new BeamBlock(new BlockConfig().material("netherite_block").materialName("Netherite Block").ingredient(Blocks.NETHERITE_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("redstone_block"), () -> new BeamBlock(new BlockConfig().material("redstone_block").materialName("Redstone Block").ingredient(Blocks.REDSTONE_BLOCK)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cut_copper"), () -> new BeamBlock(new BlockConfig().material("cut_copper").materialName("Cut Copper").ingredient(Blocks.CUT_COPPER)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("exposed_cut_copper"), () -> new BeamBlock(new BlockConfig().material("exposed_cut_copper").materialName("Exposed Cut Copper").ingredient(Blocks.EXPOSED_CUT_COPPER)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("weathered_cut_copper"), () -> new BeamBlock(new BlockConfig().material("weathered_cut_copper").materialName("Weathered Cut Copper").ingredient(Blocks.WEATHERED_CUT_COPPER)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("oxidized_cut_copper"), () -> new BeamBlock(new BlockConfig().material("oxidized_cut_copper").materialName("Oxidized Cut Copper").ingredient(Blocks.OXIDIZED_CUT_COPPER)), DEFAULT_BEAM_SETTINGS));

        BLOCKS.add(registerWithItem(BeamBlock.makeId("white_terracotta"), () -> new BeamBlock(new BlockConfig().material("white_terracotta").materialName("White Terracotta").ingredient(Blocks.WHITE_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("light_gray_terracotta"), () -> new BeamBlock(new BlockConfig().material("light_gray_terracotta").materialName("Light Gray Terracotta").ingredient(Blocks.LIGHT_GRAY_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("gray_terracotta"), () -> new BeamBlock(new BlockConfig().material("gray_terracotta").materialName("Gray Terracotta").ingredient(Blocks.GRAY_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("black_terracotta"), () -> new BeamBlock(new BlockConfig().material("black_terracotta").materialName("Black Terracotta").ingredient(Blocks.BLACK_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("brown_terracotta"), () -> new BeamBlock(new BlockConfig().material("brown_terracotta").materialName("Brown Terracotta").ingredient(Blocks.BROWN_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("red_terracotta"), () -> new BeamBlock(new BlockConfig().material("red_terracotta").materialName("Red Terracotta").ingredient(Blocks.RED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("orange_terracotta"), () -> new BeamBlock(new BlockConfig().material("orange_terracotta").materialName("Orange Terracotta").ingredient(Blocks.ORANGE_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("yellow_terracotta"), () -> new BeamBlock(new BlockConfig().material("yellow_terracotta").materialName("Yellow Terracotta").ingredient(Blocks.YELLOW_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("lime_terracotta"), () -> new BeamBlock(new BlockConfig().material("lime_terracotta").materialName("Lime Terracotta").ingredient(Blocks.LIME_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("green_terracotta"), () -> new BeamBlock(new BlockConfig().material("green_terracotta").materialName("Green Terracotta").ingredient(Blocks.GREEN_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cyan_terracotta"), () -> new BeamBlock(new BlockConfig().material("cyan_terracotta").materialName("Cyan Terracotta").ingredient(Blocks.CYAN_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("light_blue_terracotta"), () -> new BeamBlock(new BlockConfig().material("light_blue_terracotta").materialName("Light Blue Terracotta").ingredient(Blocks.LIGHT_BLUE_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("blue_terracotta"), () -> new BeamBlock(new BlockConfig().material("blue_terracotta").materialName("Blue Terracotta").ingredient(Blocks.BLUE_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("purple_terracotta"), () -> new BeamBlock(new BlockConfig().material("purple_terracotta").materialName("Purple Terracotta").ingredient(Blocks.PURPLE_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("magenta_terracotta"), () -> new BeamBlock(new BlockConfig().material("magenta_terracotta").materialName("Magenta Terracotta").ingredient(Blocks.MAGENTA_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("pink_terracotta"), () -> new BeamBlock(new BlockConfig().material("pink_terracotta").materialName("Pink Terracotta").ingredient(Blocks.PINK_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));

        BLOCKS.add(registerWithItem(BeamBlock.makeId("white_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("white_glazed_terracotta").materialName("White Glazed Terracotta").ingredient(Blocks.WHITE_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("light_gray_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("light_gray_glazed_terracotta").materialName("Light Gray Glazed Terracotta").ingredient(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("gray_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("gray_glazed_terracotta").materialName("Gray Glazed Terracotta").ingredient(Blocks.GRAY_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("black_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("black_glazed_terracotta").materialName("Black Glazed Terracotta").ingredient(Blocks.BLACK_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("brown_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("brown_glazed_terracotta").materialName("Brown Glazed Terracotta").ingredient(Blocks.BROWN_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("red_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("red_glazed_terracotta").materialName("Red Glazed Terracotta").ingredient(Blocks.RED_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("orange_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("orange_glazed_terracotta").materialName("Orange Glazed Terracotta").ingredient(Blocks.ORANGE_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("yellow_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("yellow_glazed_terracotta").materialName("Yellow Glazed Terracotta").ingredient(Blocks.YELLOW_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("lime_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("lime_glazed_terracotta").materialName("Lime Glazed Terracotta").ingredient(Blocks.LIME_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("green_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("green_glazed_terracotta").materialName("Green Glazed Terracotta").ingredient(Blocks.GREEN_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cyan_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("cyan_glazed_terracotta").materialName("Cyan Glazed Terracotta").ingredient(Blocks.CYAN_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("light_blue_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("light_blue_glazed_terracotta").materialName("Light Blue Glazed Terracotta").ingredient(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("blue_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("blue_glazed_terracotta").materialName("Blue Glazed Terracotta").ingredient(Blocks.BLUE_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("purple_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("purple_glazed_terracotta").materialName("Purple Glazed Terracotta").ingredient(Blocks.PURPLE_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("magenta_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("magenta_glazed_terracotta").materialName("Magenta Glazed Terracotta").ingredient(Blocks.MAGENTA_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("pink_glazed_terracotta"), () -> new BeamBlock(new BlockConfig().material("pink_glazed_terracotta").materialName("Pink Glazed Terracotta").ingredient(Blocks.PINK_GLAZED_TERRACOTTA)), DEFAULT_BEAM_SETTINGS));

        BLOCKS.add(registerWithItem(BeamBlock.makeId("white_concrete"), () -> new BeamBlock(new BlockConfig().material("white_concrete").materialName("White Concrete").ingredient(Blocks.WHITE_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("light_gray_concrete"), () -> new BeamBlock(new BlockConfig().material("light_gray_concrete").materialName("Light Gray Concrete").ingredient(Blocks.LIGHT_GRAY_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("gray_concrete"), () -> new BeamBlock(new BlockConfig().material("gray_concrete").materialName("Gray Concrete").ingredient(Blocks.GRAY_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("black_concrete"), () -> new BeamBlock(new BlockConfig().material("black_concrete").materialName("Black Concrete").ingredient(Blocks.BLACK_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("brown_concrete"), () -> new BeamBlock(new BlockConfig().material("brown_concrete").materialName("Brown Concrete").ingredient(Blocks.BROWN_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("red_concrete"), () -> new BeamBlock(new BlockConfig().material("red_concrete").materialName("Red Concrete").ingredient(Blocks.RED_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("orange_concrete"), () -> new BeamBlock(new BlockConfig().material("orange_concrete").materialName("Orange Concrete").ingredient(Blocks.ORANGE_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("yellow_concrete"), () -> new BeamBlock(new BlockConfig().material("yellow_concrete").materialName("Yellow Concrete").ingredient(Blocks.YELLOW_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("lime_concrete"), () -> new BeamBlock(new BlockConfig().material("lime_concrete").materialName("Lime Concrete").ingredient(Blocks.LIME_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("green_concrete"), () -> new BeamBlock(new BlockConfig().material("green_concrete").materialName("Green Concrete").ingredient(Blocks.GREEN_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cyan_concrete"), () -> new BeamBlock(new BlockConfig().material("cyan_concrete").materialName("Cyan Concrete").ingredient(Blocks.CYAN_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("light_blue_concrete"), () -> new BeamBlock(new BlockConfig().material("light_blue_concrete").materialName("Light Blue Concrete").ingredient(Blocks.LIGHT_BLUE_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("blue_concrete"), () -> new BeamBlock(new BlockConfig().material("blue_concrete").materialName("Blue Concrete").ingredient(Blocks.BLUE_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("purple_concrete"), () -> new BeamBlock(new BlockConfig().material("purple_concrete").materialName("Purple Concrete").ingredient(Blocks.PURPLE_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("magenta_concrete"), () -> new BeamBlock(new BlockConfig().material("magenta_concrete").materialName("Magenta Concrete").ingredient(Blocks.MAGENTA_CONCRETE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("pink_concrete"), () -> new BeamBlock(new BlockConfig().material("pink_concrete").materialName("Pink Concrete").ingredient(Blocks.PINK_CONCRETE)), DEFAULT_BEAM_SETTINGS));

        BLOCKS.add(registerWithItem(BeamBlock.makeId("acacia"), () -> new BeamBlock(new BlockConfig().material("acacia").materialName("Acacia").ingredient(Blocks.ACACIA_PLANKS).flammable().tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("acacia_log"), () -> new BeamBlock(new BlockConfig().material("acacia_log").materialName("Acacia Log").ingredient(Blocks.ACACIA_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.ACACIA_PLANKS)).texture(TextureUtils.block(Blocks.ACACIA_LOG)).texture("end", TextureUtils.block(Blocks.ACACIA_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("birch"), () -> new BeamBlock(new BlockConfig().material("birch").materialName("Birch").ingredient(Blocks.BIRCH_PLANKS).flammable().tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("birch_log"), () -> new BeamBlock(new BlockConfig().material("birch_log").materialName("Birch Log").ingredient(Blocks.BIRCH_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.BIRCH_PLANKS)).texture(TextureUtils.block(Blocks.BIRCH_LOG)).texture("end", TextureUtils.block(Blocks.BIRCH_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cherry"), () -> new BeamBlock(new BlockConfig().material("cherry").materialName("Cherry").ingredient(Blocks.CHERRY_PLANKS).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("cherry_log"), () -> new BeamBlock(new BlockConfig().material("cherry_log").materialName("Cherry Log").ingredient(Blocks.CHERRY_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.CHERRY_PLANKS)).texture(TextureUtils.block(Blocks.CHERRY_LOG)).texture("end", TextureUtils.block(Blocks.CHERRY_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("crimson"), () -> new BeamBlock(new BlockConfig().material("crimson").materialName("Crimson").ingredient(Blocks.CRIMSON_PLANKS).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("crimson_stem"), () -> new BeamBlock(new BlockConfig().material("crimson_stem").materialName("Crimson Stem").ingredient(Blocks.CRIMSON_STEM).settings(AbstractBlock.Settings.copy(Blocks.CRIMSON_PLANKS)).texture(TextureUtils.block(Blocks.CRIMSON_STEM)).texture("end", TextureUtils.block(Blocks.CRIMSON_STEM, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("dark_oak"), () -> new BeamBlock(new BlockConfig().material("dark_oak").materialName("Dark Oak").ingredient(Blocks.DARK_OAK_PLANKS).flammable().tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("dark_oak_log"), () -> new BeamBlock(new BlockConfig().material("dark_oak_log").materialName("Dark Oak Log").ingredient(Blocks.DARK_OAK_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.DARK_OAK_PLANKS)).texture(TextureUtils.block(Blocks.DARK_OAK_LOG)).texture("end", TextureUtils.block(Blocks.DARK_OAK_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("jungle"), () -> new BeamBlock(new BlockConfig().material("jungle").materialName("Jungle").ingredient(Blocks.JUNGLE_PLANKS).flammable().tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("jungle_log"), () -> new BeamBlock(new BlockConfig().material("jungle_log").materialName("Jungle Log").ingredient(Blocks.JUNGLE_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.JUNGLE_PLANKS)).texture(TextureUtils.block(Blocks.JUNGLE_LOG)).texture("end", TextureUtils.block(Blocks.JUNGLE_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("mangrove"), () -> new BeamBlock(new BlockConfig().material("mangrove").materialName("Mangrove").ingredient(Blocks.MANGROVE_PLANKS).flammable().tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("mangrove_log"), () -> new BeamBlock(new BlockConfig().material("mangrove_log").materialName("Mangrove Log").ingredient(Blocks.MANGROVE_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.MANGROVE_PLANKS)).texture(TextureUtils.block(Blocks.MANGROVE_LOG)).texture("end", TextureUtils.block(Blocks.MANGROVE_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("oak"), () -> new BeamBlock(new BlockConfig().material("oak").materialName("Oak").ingredient(Blocks.OAK_PLANKS).flammable().tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("oak_log"), () -> new BeamBlock(new BlockConfig().material("oak_log").materialName("Oak Log").ingredient(Blocks.OAK_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)).texture(TextureUtils.block(Blocks.OAK_LOG)).texture("end", TextureUtils.block(Blocks.OAK_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("spruce"), () -> new BeamBlock(new BlockConfig().material("spruce").materialName("Spruce").ingredient(Blocks.SPRUCE_PLANKS).flammable().tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("spruce_log"), () -> new BeamBlock(new BlockConfig().material("spruce_log").materialName("Spruce Log").ingredient(Blocks.SPRUCE_LOG).flammable().settings(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)).texture(TextureUtils.block(Blocks.SPRUCE_LOG)).texture("end", TextureUtils.block(Blocks.SPRUCE_LOG, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("warped"), () -> new BeamBlock(new BlockConfig().material("warped").materialName("Warped").ingredient(Blocks.WARPED_PLANKS).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
        BLOCKS.add(registerWithItem(BeamBlock.makeId("warped_stem"), () -> new BeamBlock(new BlockConfig().material("warped_stem").materialName("Warped Stem").ingredient(Blocks.WARPED_STEM).settings(AbstractBlock.Settings.copy(Blocks.WARPED_PLANKS)).texture(TextureUtils.block(Blocks.WARPED_STEM)).texture("end", TextureUtils.block(Blocks.WARPED_STEM, "_top")).tool(Tool.AXE)), DEFAULT_BEAM_SETTINGS));
    }
}