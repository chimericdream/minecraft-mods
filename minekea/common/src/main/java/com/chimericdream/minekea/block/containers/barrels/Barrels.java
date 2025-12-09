package com.chimericdream.minekea.block.containers.barrels;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.entity.block.containers.MinekeaBarrelBlockEntity;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class Barrels implements ModThingGroup {
    @SuppressWarnings("UnstableApiUsage")
    public static final Item.Settings DEFAULT_BARREL_SETTINGS = new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL);

    public static RegistrySupplier<BlockEntityType<MinekeaBarrelBlockEntity>> MINEKEA_BARREL_BLOCK_ENTITY;

    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();

    static {
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("acacia"), () -> new BarrelBlock(new BlockConfig().material("acacia").materialName("Acacia").ingredient(Blocks.ACACIA_PLANKS).ingredient("slab", Blocks.ACACIA_SLAB), "acacia_planks", "stripped_acacia_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("acacia")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("bamboo"), () -> new BarrelBlock(new BlockConfig().material("bamboo").materialName("Bamboo").ingredient(Blocks.BAMBOO_PLANKS).ingredient("slab", Blocks.BAMBOO_SLAB), "bamboo_planks", "stripped_bamboo_block"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("bamboo")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("birch"), () -> new BarrelBlock(new BlockConfig().material("birch").materialName("Birch").ingredient(Blocks.BIRCH_PLANKS).ingredient("slab", Blocks.BIRCH_SLAB), "birch_planks", "stripped_birch_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("birch")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("cherry"), () -> new BarrelBlock(new BlockConfig().material("cherry").materialName("Cherry").ingredient(Blocks.CHERRY_PLANKS).ingredient("slab", Blocks.CHERRY_SLAB), "cherry_planks", "stripped_cherry_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("cherry")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("crimson"), () -> new BarrelBlock(new BlockConfig().material("crimson").materialName("Crimson").ingredient(Blocks.CRIMSON_PLANKS).ingredient("slab", Blocks.CRIMSON_SLAB), "crimson_planks", "stripped_crimson_stem"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("crimson")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("dark_oak"), () -> new BarrelBlock(new BlockConfig().material("dark_oak").materialName("Dark Oak").ingredient(Blocks.DARK_OAK_PLANKS).ingredient("slab", Blocks.DARK_OAK_SLAB), "dark_oak_planks", "stripped_dark_oak_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("dark_oak")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("jungle"), () -> new BarrelBlock(new BlockConfig().material("jungle").materialName("Jungle").ingredient(Blocks.JUNGLE_PLANKS).ingredient("slab", Blocks.JUNGLE_SLAB), "jungle_planks", "stripped_jungle_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("jungle")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("mangrove"), () -> new BarrelBlock(new BlockConfig().material("mangrove").materialName("Mangrove").ingredient(Blocks.MANGROVE_PLANKS).ingredient("slab", Blocks.MANGROVE_SLAB), "mangrove_planks", "stripped_mangrove_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("mangrove")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("pale_oak"), () -> new BarrelBlock(new BlockConfig().material("pale_oak").materialName("Pale Oak").ingredient(Blocks.PALE_OAK_PLANKS).ingredient("slab", Blocks.PALE_OAK_SLAB), "pale_oak_planks", "stripped_pale_oak_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("pale_oak")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("spruce"), () -> new BarrelBlock(new BlockConfig().material("spruce").materialName("Spruce").ingredient(Blocks.SPRUCE_PLANKS).ingredient("slab", Blocks.SPRUCE_SLAB), "spruce_planks", "stripped_spruce_log"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("spruce")))));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(BarrelBlock.makeId("warped"), () -> new BarrelBlock(new BlockConfig().material("warped").materialName("Warped").ingredient(Blocks.WARPED_PLANKS).ingredient("slab", Blocks.WARPED_SLAB), "warped_planks", "stripped_warped_stem"), DEFAULT_BARREL_SETTINGS.registryKey(REGISTRY_HELPER.makeItemRegistryKey(BarrelBlock.makeId("warped")))));

        MINEKEA_BARREL_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
            MinekeaBarrelBlockEntity.ENTITY_ID,
            () -> new BlockEntityType<>(
                MinekeaBarrelBlockEntity::new,
                Set.of(BLOCKS.stream().map(Supplier::get).toList().toArray(Block[]::new))
            )
        );
    }
}
