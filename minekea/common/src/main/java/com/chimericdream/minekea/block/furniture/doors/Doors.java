package com.chimericdream.minekea.block.furniture.doors;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.block.furniture.bookshelves.Bookshelves;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class Doors implements ModThingGroup {
    @SuppressWarnings("UnstableApiUsage")
    public static final Item.Settings DEFAULT_DOOR_SETTINGS = new Item.Settings().arch$tab(ItemGroups.REDSTONE);

    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();
    public static final List<RegistrySupplier<Block>> BOOKSHELF_DOOR_BLOCKS = new ArrayList<>();

    static {
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("acacia"), () -> new BookshelfDoorBlock(BlockSetType.ACACIA, new BlockConfig().material("acacia").materialName("Acacia").ingredient(Bookshelves.BOOKSHELVES.get("acacia")).ingredient("planks", Blocks.ACACIA_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("bamboo"), () -> new BookshelfDoorBlock(BlockSetType.BAMBOO, new BlockConfig().material("bamboo").materialName("Bamboo").ingredient(Bookshelves.BOOKSHELVES.get("bamboo")).ingredient("planks", Blocks.BAMBOO_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("birch"), () -> new BookshelfDoorBlock(BlockSetType.BIRCH, new BlockConfig().material("birch").materialName("Birch").ingredient(Bookshelves.BOOKSHELVES.get("birch")).ingredient("planks", Blocks.BIRCH_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("cherry"), () -> new BookshelfDoorBlock(BlockSetType.CHERRY, new BlockConfig().material("cherry").materialName("Cherry").ingredient(Bookshelves.BOOKSHELVES.get("cherry")).ingredient("planks", Blocks.CHERRY_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("crimson"), () -> new BookshelfDoorBlock(BlockSetType.CRIMSON, new BlockConfig().material("crimson").materialName("Crimson").ingredient(Bookshelves.BOOKSHELVES.get("crimson")).ingredient("planks", Blocks.CRIMSON_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("dark_oak"), () -> new BookshelfDoorBlock(BlockSetType.DARK_OAK, new BlockConfig().material("dark_oak").materialName("Dark Oak").ingredient(Bookshelves.BOOKSHELVES.get("dark_oak")).ingredient("planks", Blocks.DARK_OAK_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("jungle"), () -> new BookshelfDoorBlock(BlockSetType.JUNGLE, new BlockConfig().material("jungle").materialName("Jungle").ingredient(Bookshelves.BOOKSHELVES.get("jungle")).ingredient("planks", Blocks.JUNGLE_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("mangrove"), () -> new BookshelfDoorBlock(BlockSetType.MANGROVE, new BlockConfig().material("mangrove").materialName("Mangrove").ingredient(Bookshelves.BOOKSHELVES.get("mangrove")).ingredient("planks", Blocks.MANGROVE_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("oak"), () -> new BookshelfDoorBlock(BlockSetType.OAK, new BlockConfig().material("oak").materialName("Oak").ingredient(Blocks.BOOKSHELF).ingredient("planks", Blocks.OAK_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("spruce"), () -> new BookshelfDoorBlock(BlockSetType.SPRUCE, new BlockConfig().material("spruce").materialName("Spruce").ingredient(Bookshelves.BOOKSHELVES.get("spruce")).ingredient("planks", Blocks.SPRUCE_PLANKS)), DEFAULT_DOOR_SETTINGS));
        BOOKSHELF_DOOR_BLOCKS.add(REGISTRY_HELPER.registerWithItem(BookshelfDoorBlock.makeId("warped"), () -> new BookshelfDoorBlock(BlockSetType.WARPED, new BlockConfig().material("warped").materialName("Warped").ingredient(Bookshelves.BOOKSHELVES.get("warped")).ingredient("planks", Blocks.WARPED_PLANKS)), DEFAULT_DOOR_SETTINGS));

        BLOCKS.addAll(BOOKSHELF_DOOR_BLOCKS);
    }
}
