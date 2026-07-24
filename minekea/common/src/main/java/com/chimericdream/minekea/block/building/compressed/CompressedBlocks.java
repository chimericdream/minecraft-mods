package com.chimericdream.minekea.block.building.compressed;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.building.BuildingBlocks;
import com.chimericdream.minekea.block.building.general.BasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.CrackedBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.CrimsonBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.MossyBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.WarpedBasaltBricksBlock;
import com.chimericdream.minekea.block.building.general.WarpedNetherBricksBlock;
import com.chimericdream.minekea.registry.ModItemGroups;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class CompressedBlocks implements ModThingGroup {
    public static final Map<String, List<RegistrySupplier<Block>>> BLOCK_MAP = new LinkedHashMap<>();
    public static final Map<String, RegistrySupplier<Block>> COMPRESSED_BLOCKS_BY_ID = new LinkedHashMap<>();
    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();
    public static final List<RegistrySupplier<Block>> COLUMN_BLOCKS = new ArrayList<>();
    public static final List<RegistrySupplier<Block>> MINEKEA_BLOCKS = new ArrayList<>();

    /** A vanilla block that can be compressed: display name, texture/id base, the block, and the tool. */
    public record CompressedEntry(String name, String id, Block block, @Nullable Tool tool) {}

    /** A vanilla column/pillar block to compress, with distinct side and top texture suffixes. */
    public record ColumnEntry(String name, String id, Block block, String sideSuffix, String topSuffix, @Nullable Tool tool) {}

    /** A minekea-provided block to compress: its supplier plus the base block's id. */
    public record MinekeaEntry(String name, String id, Supplier<Block> block, Identifier baseBlockId, @Nullable Tool tool) {}

    protected static final List<CompressedEntry> BLOCKS_TO_COMPRESS = new ArrayList<>();
    protected static final List<ColumnEntry> COLUMN_BLOCKS_TO_COMPRESS = new ArrayList<>();
    protected static final List<MinekeaEntry> MINEKEA_BLOCKS_TO_COMPRESS = new ArrayList<>();

    static {
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Amethyst", "amethyst_block", Blocks.AMETHYST_BLOCK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Andesite", "andesite", Blocks.ANDESITE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Bricks", "bricks", Blocks.BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Calcite", "calcite", Blocks.CALCITE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Clay", "clay", Blocks.CLAY, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Coarse Dirt", "coarse_dirt", Blocks.COARSE_DIRT, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cobbled Deepslate", "cobbled_deepslate", Blocks.COBBLED_DEEPSLATE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cobblestone", "cobblestone", Blocks.COBBLESTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cracked Deepslate Bricks", "cracked_deepslate_bricks", Blocks.CRACKED_DEEPSLATE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cracked Deepslate Tiles", "cracked_deepslate_tiles", Blocks.CRACKED_DEEPSLATE_TILES, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cracked Stone Bricks", "cracked_stone_bricks", Blocks.CRACKED_STONE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Crying Obsidian", "crying_obsidian", Blocks.CRYING_OBSIDIAN, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cut Red Sandstone", "cut_red_sandstone", Blocks.CUT_RED_SANDSTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cut Sandstone", "cut_sandstone", Blocks.CUT_SANDSTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Dark Prismarine", "dark_prismarine", Blocks.DARK_PRISMARINE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Deepslate Bricks", "deepslate_bricks", Blocks.DEEPSLATE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Deepslate Tiles", "deepslate_tiles", Blocks.DEEPSLATE_TILES, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Diorite", "diorite", Blocks.DIORITE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Dirt", "dirt", Blocks.DIRT, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("End Stone", "end_stone", Blocks.END_STONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("End Stone Bricks", "end_stone_bricks", Blocks.END_STONE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Granite", "granite", Blocks.GRANITE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Gravel", "gravel", Blocks.GRAVEL, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Mossy Cobblestone", "mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Mossy Stone Bricks", "mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Mud", "mud", Blocks.MUD, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Mud Bricks", "mud_bricks", Blocks.MUD_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Netherrack", "netherrack", Blocks.NETHERRACK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Nether Bricks", "nether_bricks", Blocks.NETHER_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Obsidian", "obsidian", Blocks.OBSIDIAN, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Packed Mud", "packed_mud", Blocks.PACKED_MUD, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Polished Andesite", "polished_andesite", Blocks.POLISHED_ANDESITE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Polished Blackstone", "polished_blackstone", Blocks.POLISHED_BLACKSTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Polished Blackstone Bricks", "polished_blackstone_bricks", Blocks.POLISHED_BLACKSTONE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Polished Deepslate", "polished_deepslate", Blocks.POLISHED_DEEPSLATE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Polished Diorite", "polished_diorite", Blocks.POLISHED_DIORITE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Polished Granite", "polished_granite", Blocks.POLISHED_GRANITE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Prismarine", "prismarine", Blocks.PRISMARINE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Prismarine Bricks", "prismarine_bricks", Blocks.PRISMARINE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Purpur", "purpur_block", Blocks.PURPUR_BLOCK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Quartz Bricks", "quartz_bricks", Blocks.QUARTZ_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Red Nether Bricks", "red_nether_bricks", Blocks.RED_NETHER_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Red Sandstone", "red_sandstone", Blocks.RED_SANDSTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Red Sand", "red_sand", Blocks.RED_SAND, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Rooted Dirt", "rooted_dirt", Blocks.ROOTED_DIRT, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Sand", "sand", Blocks.SAND, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Sandstone", "sandstone", Blocks.SANDSTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Smooth Basalt", "smooth_basalt", Blocks.SMOOTH_BASALT, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Smooth Quartz", "quartz_block_bottom", Blocks.SMOOTH_QUARTZ, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Smooth Red Sandstone", "red_sandstone_top", Blocks.SMOOTH_RED_SANDSTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Smooth Sandstone", "sandstone_top", Blocks.SMOOTH_SANDSTONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Smooth Stone", "smooth_stone", Blocks.SMOOTH_STONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Soul Sand", "soul_sand", Blocks.SOUL_SAND, Tool.SHOVEL));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Stone", "stone", Blocks.STONE, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Stone Bricks", "stone_bricks", Blocks.STONE_BRICKS, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Tuff", "tuff", Blocks.TUFF, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Copper Block", "copper_block", Blocks.COPPER_BLOCK.weathering().unaffected(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Diamond Block", "diamond_block", Blocks.DIAMOND_BLOCK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Gold Block", "gold_block", Blocks.GOLD_BLOCK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Iron Block", "iron_block", Blocks.IRON_BLOCK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Lapis Block", "lapis_block", Blocks.LAPIS_BLOCK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Netherite Block", "netherite_block", Blocks.NETHERITE_BLOCK, null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Redstone Block", "redstone_block", Blocks.REDSTONE_BLOCK, null));

        BLOCKS_TO_COMPRESS.add(new CompressedEntry("White Terracotta", "white_terracotta", Blocks.DYED_TERRACOTTA.white(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Light Gray Terracotta", "light_gray_terracotta", Blocks.DYED_TERRACOTTA.lightGray(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Gray Terracotta", "gray_terracotta", Blocks.DYED_TERRACOTTA.gray(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Black Terracotta", "black_terracotta", Blocks.DYED_TERRACOTTA.black(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Brown Terracotta", "brown_terracotta", Blocks.DYED_TERRACOTTA.brown(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Red Terracotta", "red_terracotta", Blocks.DYED_TERRACOTTA.red(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Orange Terracotta", "orange_terracotta", Blocks.DYED_TERRACOTTA.orange(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Yellow Terracotta", "yellow_terracotta", Blocks.DYED_TERRACOTTA.yellow(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Lime Terracotta", "lime_terracotta", Blocks.DYED_TERRACOTTA.lime(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Green Terracotta", "green_terracotta", Blocks.DYED_TERRACOTTA.green(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cyan Terracotta", "cyan_terracotta", Blocks.DYED_TERRACOTTA.cyan(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Light Blue Terracotta", "light_blue_terracotta", Blocks.DYED_TERRACOTTA.lightBlue(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Blue Terracotta", "blue_terracotta", Blocks.DYED_TERRACOTTA.blue(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Purple Terracotta", "purple_terracotta", Blocks.DYED_TERRACOTTA.purple(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Magenta Terracotta", "magenta_terracotta", Blocks.DYED_TERRACOTTA.magenta(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Pink Terracotta", "pink_terracotta", Blocks.DYED_TERRACOTTA.pink(), null));

        BLOCKS_TO_COMPRESS.add(new CompressedEntry("White Glazed Terracotta", "white_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.white(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Light Gray Glazed Terracotta", "light_gray_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.lightGray(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Gray Glazed Terracotta", "gray_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.gray(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Black Glazed Terracotta", "black_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.black(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Brown Glazed Terracotta", "brown_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.brown(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Red Glazed Terracotta", "red_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.red(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Orange Glazed Terracotta", "orange_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.orange(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Yellow Glazed Terracotta", "yellow_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.yellow(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Lime Glazed Terracotta", "lime_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.lime(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Green Glazed Terracotta", "green_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.green(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cyan Glazed Terracotta", "cyan_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.cyan(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Light Blue Glazed Terracotta", "light_blue_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.lightBlue(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Blue Glazed Terracotta", "blue_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.blue(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Purple Glazed Terracotta", "purple_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.purple(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Magenta Glazed Terracotta", "magenta_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.magenta(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Pink Glazed Terracotta", "pink_glazed_terracotta", Blocks.GLAZED_TERRACOTTA.pink(), null));

        BLOCKS_TO_COMPRESS.add(new CompressedEntry("White Concrete", "white_concrete", Blocks.CONCRETE.white(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Light Gray Concrete", "light_gray_concrete", Blocks.CONCRETE.lightGray(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Gray Concrete", "gray_concrete", Blocks.CONCRETE.gray(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Black Concrete", "black_concrete", Blocks.CONCRETE.black(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Brown Concrete", "brown_concrete", Blocks.CONCRETE.brown(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Red Concrete", "red_concrete", Blocks.CONCRETE.red(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Orange Concrete", "orange_concrete", Blocks.CONCRETE.orange(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Yellow Concrete", "yellow_concrete", Blocks.CONCRETE.yellow(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Lime Concrete", "lime_concrete", Blocks.CONCRETE.lime(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Green Concrete", "green_concrete", Blocks.CONCRETE.green(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cyan Concrete", "cyan_concrete", Blocks.CONCRETE.cyan(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Light Blue Concrete", "light_blue_concrete", Blocks.CONCRETE.lightBlue(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Blue Concrete", "blue_concrete", Blocks.CONCRETE.blue(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Purple Concrete", "purple_concrete", Blocks.CONCRETE.purple(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Magenta Concrete", "magenta_concrete", Blocks.CONCRETE.magenta(), null));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Pink Concrete", "pink_concrete", Blocks.CONCRETE.pink(), null));

        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Acacia Planks", "acacia_planks", Blocks.ACACIA_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Birch Planks", "birch_planks", Blocks.BIRCH_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Cherry Planks", "cherry_planks", Blocks.CHERRY_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Crimson Planks", "crimson_planks", Blocks.CRIMSON_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Dark Oak Planks", "dark_oak_planks", Blocks.DARK_OAK_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Jungle Planks", "jungle_planks", Blocks.JUNGLE_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Mangrove Planks", "mangrove_planks", Blocks.MANGROVE_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Oak Planks", "oak_planks", Blocks.OAK_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Pale Oak Planks", "pale_oak_planks", Blocks.PALE_OAK_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Spruce Planks", "spruce_planks", Blocks.SPRUCE_PLANKS, Tool.AXE));
        BLOCKS_TO_COMPRESS.add(new CompressedEntry("Warped Planks", "warped_planks", Blocks.WARPED_PLANKS, Tool.AXE));

        BLOCKS_TO_COMPRESS.forEach(data -> {
            String materialName = data.name();
            String material = data.id();
            Block ingredient = data.block();
            Tool tool = data.tool();

            List<RegistrySupplier<Block>> compressedBlocks = new ArrayList<>();

            for (int i = 1; i <= 9; i += 1) {
                int compressionLevel = i;
                Identifier blockId = CompressedBlock.makeId(material, compressionLevel);
                RegistrySupplier<Block> compressedBlock = REGISTRY_HELPER.registerWithItem(
                    blockId,
                    () -> new CompressedBlock(
                        new BlockConfig()
                            .material(material)
                            .materialName(materialName)
                            .ingredient(ingredient)
                            .tool(tool),
                        compressionLevel
                    ),
                    getItemSettings(material)
                );

                COMPRESSED_BLOCKS_BY_ID.put(blockId.toString(), compressedBlock);
                compressedBlocks.add(compressedBlock);
            }

            BLOCKS.addAll(compressedBlocks);
            BLOCK_MAP.put(materialName, compressedBlocks);
        });

        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Basalt", "basalt", Blocks.BASALT, "_side", "_top", null));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Blackstone", "blackstone", Blocks.BLACKSTONE, "", "_top", null));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Bone", "bone_block", Blocks.BONE_BLOCK, "_side", "_top", null));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Deepslate", "deepslate", Blocks.DEEPSLATE, "", "_top", null));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Polished Basalt", "polished_basalt", Blocks.POLISHED_BASALT, "_side", "_top", null));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Purpur Pillar", "purpur_pillar", Blocks.PURPUR_PILLAR, "", "_top", null));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Quartz", "quartz_block", Blocks.QUARTZ_BLOCK, "_side", "_top", null));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Acacia Log", "acacia_log", Blocks.ACACIA_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Birch Log", "birch_log", Blocks.BIRCH_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Cherry Log", "cherry_log", Blocks.CHERRY_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Crimson Stem", "crimson_stem", Blocks.CRIMSON_STEM, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Dark Oak Log", "dark_oak_log", Blocks.DARK_OAK_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Jungle Log", "jungle_log", Blocks.JUNGLE_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Mangrove Log", "mangrove_log", Blocks.MANGROVE_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Oak Log", "oak_log", Blocks.OAK_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Pale Oak Log", "pale_oak_log", Blocks.PALE_OAK_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Spruce Log", "spruce_log", Blocks.SPRUCE_LOG, "", "_top", Tool.AXE));
        COLUMN_BLOCKS_TO_COMPRESS.add(new ColumnEntry("Warped Stem", "warped_stem", Blocks.WARPED_STEM, "", "_top", Tool.AXE));

        COLUMN_BLOCKS_TO_COMPRESS.forEach(data -> {
            String materialName = data.name();
            String material = data.id();
            Block ingredient = data.block();
            String sideTextureSuffix = data.sideSuffix();
            String endTextureSuffix = data.topSuffix();
            Tool tool = data.tool();

            List<RegistrySupplier<Block>> compressedBlocks = new ArrayList<>();

            for (int i = 1; i <= 9; i += 1) {
                int compressionLevel = i;
                Identifier blockId = CompressedBlock.makeId(material, compressionLevel);
                RegistrySupplier<Block> compressedBlock = REGISTRY_HELPER.registerWithItem(
                    blockId,
                    () -> new CompressedColumnBlock(
                        new BlockConfig()
                            .material(material)
                            .materialName(materialName)
                            .ingredient(ingredient)
                            .tool(tool),
                        compressionLevel,
                        sideTextureSuffix,
                        endTextureSuffix
                    ),
                    getItemSettings(material)
                );

                COMPRESSED_BLOCKS_BY_ID.put(blockId.toString(), compressedBlock);
                compressedBlocks.add(compressedBlock);
            }

            COLUMN_BLOCKS.addAll(compressedBlocks);
            BLOCK_MAP.put(materialName, compressedBlocks);
        });

        MINEKEA_BLOCKS_TO_COMPRESS.add(new MinekeaEntry("Basalt Brick", "basalt_brick", BuildingBlocks.BASALT_BRICKS, BasaltBricksBlock.BLOCK_ID, null));
        MINEKEA_BLOCKS_TO_COMPRESS.add(new MinekeaEntry("Cracked Basalt Bricks", "cracked_basalt_brick", BuildingBlocks.CRACKED_BASALT_BRICKS, CrackedBasaltBricksBlock.BLOCK_ID, null));
        MINEKEA_BLOCKS_TO_COMPRESS.add(new MinekeaEntry("Crimson Basalt Bricks", "crimson_basalt_brick", BuildingBlocks.CRIMSON_BASALT_BRICKS, CrimsonBasaltBricksBlock.BLOCK_ID, null));
        MINEKEA_BLOCKS_TO_COMPRESS.add(new MinekeaEntry("Mossy Basalt Brick", "mossy_basalt_brick", BuildingBlocks.MOSSY_BASALT_BRICKS, MossyBasaltBricksBlock.BLOCK_ID, null));
        MINEKEA_BLOCKS_TO_COMPRESS.add(new MinekeaEntry("Warped Basalt Brick", "warped_basalt_brick", BuildingBlocks.WARPED_BASALT_BRICKS, WarpedBasaltBricksBlock.BLOCK_ID, null));
        MINEKEA_BLOCKS_TO_COMPRESS.add(new MinekeaEntry("Warped Nether Brick", "warped_nether_brick", BuildingBlocks.WARPED_NETHER_BRICKS, WarpedNetherBricksBlock.BLOCK_ID, null));

        MINEKEA_BLOCKS_TO_COMPRESS.forEach(data -> {
            String materialName = data.name();
            String material = data.id();
            Supplier<Block> ingredient = data.block();
            Identifier baseBlockId = data.baseBlockId();
            Tool tool = data.tool();

            List<RegistrySupplier<Block>> compressedBlocks = new ArrayList<>();

            for (int i = 1; i <= 9; i += 1) {
                int compressionLevel = i;
                Identifier blockId = CompressedBlock.makeId(material, compressionLevel);
                RegistrySupplier<Block> compressedBlock = REGISTRY_HELPER.registerWithItem(
                    blockId,
                    () -> new CompressedMinekeaBlock(
                        new BlockConfig()
                            .material(material)
                            .materialName(materialName)
                            .ingredient(ingredient.get())
                            .tool(tool),
                        compressionLevel,
                        baseBlockId
                    ),
                    getItemSettings(material)
                );

                COMPRESSED_BLOCKS_BY_ID.put(blockId.toString(), compressedBlock);
                compressedBlocks.add(compressedBlock);
            }

            MINEKEA_BLOCKS.addAll(compressedBlocks);
            BLOCK_MAP.put(materialName, compressedBlocks);
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Item.Properties getItemSettings(String material) {
        return new Item.Properties().arch$tab(ModItemGroups.COMPRESSED_BLOCK_ITEM_GROUP).overrideDescription(CompressedBlock.makeTranslationKey(material));
    }
}
