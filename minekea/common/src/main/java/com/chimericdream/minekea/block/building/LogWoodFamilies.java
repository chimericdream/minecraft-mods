package com.chimericdream.minekea.block.building;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

/**
 * Shared material table for the vanilla log/wood, nether stem/hyphae, and bamboo block families that
 * don't have their own vanilla stairs/slabs. {@code Stairs} and {@code Slabs} both iterate {@link #ALL}
 * to register the regular and vertical stairs/slabs for each material.
 */
public class LogWoodFamilies {
    public static final List<Entry> ALL = new ArrayList<>();

    static {
        ALL.addAll(woodFamily("oak", "Oak", Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD, Blocks.OAK_PLANKS, true));
        ALL.addAll(woodFamily("spruce", "Spruce", Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.SPRUCE_PLANKS, true));
        ALL.addAll(woodFamily("birch", "Birch", Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD, Blocks.BIRCH_PLANKS, true));
        ALL.addAll(woodFamily("jungle", "Jungle", Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.JUNGLE_PLANKS, true));
        ALL.addAll(woodFamily("acacia", "Acacia", Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD, Blocks.ACACIA_PLANKS, true));
        ALL.addAll(woodFamily("dark_oak", "Dark Oak", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.DARK_OAK_PLANKS, true));
        ALL.addAll(woodFamily("mangrove", "Mangrove", Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.MANGROVE_PLANKS, true));
        ALL.addAll(woodFamily("cherry", "Cherry", Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD, Blocks.CHERRY_PLANKS, true));
        ALL.addAll(woodFamily("pale_oak", "Pale Oak", Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_WOOD, Blocks.PALE_OAK_PLANKS, true));

        ALL.addAll(stemFamily("crimson", "Crimson", Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.CRIMSON_PLANKS));
        ALL.addAll(stemFamily("warped", "Warped", Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.WARPED_PLANKS));

        ALL.add(new Entry(
            "bamboo_block", "Bamboo Block",
            Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_PLANKS,
            Identifier.withDefaultNamespace("bamboo_block"), Identifier.withDefaultNamespace("bamboo_block_top"),
            true
        ));
        ALL.add(new Entry(
            "stripped_bamboo_block", "Stripped Bamboo Block",
            Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_PLANKS,
            Identifier.withDefaultNamespace("stripped_bamboo_block"), Identifier.withDefaultNamespace("stripped_bamboo_block_top"),
            true
        ));
    }

    private static List<Entry> woodFamily(String species, String displayName, Block log, Block wood, Block strippedLog, Block strippedWood, Block planks, boolean flammable) {
        Identifier logTexture = Identifier.withDefaultNamespace(species + "_log");
        Identifier logTopTexture = Identifier.withDefaultNamespace(species + "_log_top");
        Identifier strippedLogTexture = Identifier.withDefaultNamespace("stripped_" + species + "_log");
        Identifier strippedLogTopTexture = Identifier.withDefaultNamespace("stripped_" + species + "_log_top");

        return List.of(
            new Entry(species + "_log", displayName + " Log", log, planks, logTexture, logTopTexture, flammable),
            new Entry(species + "_wood", displayName + " Wood", wood, planks, logTexture, null, flammable),
            new Entry("stripped_" + species + "_log", "Stripped " + displayName + " Log", strippedLog, planks, strippedLogTexture, strippedLogTopTexture, flammable),
            new Entry("stripped_" + species + "_wood", "Stripped " + displayName + " Wood", strippedWood, planks, strippedLogTexture, null, flammable)
        );
    }

    private static List<Entry> stemFamily(String species, String displayName, Block stem, Block hyphae, Block strippedStem, Block strippedHyphae, Block planks) {
        Identifier stemTexture = Identifier.withDefaultNamespace(species + "_stem");
        Identifier stemTopTexture = Identifier.withDefaultNamespace(species + "_stem_top");
        Identifier strippedStemTexture = Identifier.withDefaultNamespace("stripped_" + species + "_stem");
        Identifier strippedStemTopTexture = Identifier.withDefaultNamespace("stripped_" + species + "_stem_top");

        return List.of(
            new Entry(species + "_stem", displayName + " Stem", stem, planks, stemTexture, stemTopTexture, false),
            new Entry(species + "_hyphae", displayName + " Hyphae", hyphae, planks, stemTexture, null, false),
            new Entry("stripped_" + species + "_stem", "Stripped " + displayName + " Stem", strippedStem, planks, strippedStemTexture, strippedStemTopTexture, false),
            new Entry("stripped_" + species + "_hyphae", "Stripped " + displayName + " Hyphae", strippedHyphae, planks, strippedStemTexture, null, false)
        );
    }

    public record Entry(
        String material,
        String materialName,
        Block ingredient,
        Block settingsSource,
        Identifier sideTexture,
        @Nullable Identifier topTexture,
        boolean flammable
    ) {
        /**
         * Builds a fresh {@link BlockConfig} for this material. Called once per block type (stairs,
         * vertical stairs, slab, vertical slab) rather than shared, since {@code .settings(...)} pins
         * a single {@code BlockBehaviour.Properties} instance and each block needs its own.
         */
        public BlockConfig newConfig() {
            BlockConfig config = new BlockConfig()
                .material(material)
                .materialName(materialName)
                .ingredient(ingredient)
                .settings(BlockBehaviour.Properties.ofFullCopy(settingsSource))
                .tool(Tool.AXE)
                .texture(TextureUtils.block(sideTexture));

            if (flammable) {
                config.flammable();
            }

            if (topTexture != null) {
                Identifier top = TextureUtils.block(topTexture);
                config.texture("top", top);
                config.texture("bottom", top);
            }

            return config;
        }
    }
}
