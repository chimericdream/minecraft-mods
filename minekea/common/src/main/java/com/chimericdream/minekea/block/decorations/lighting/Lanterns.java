package com.chimericdream.minekea.block.decorations.lighting;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class Lanterns implements ModThingGroup {
    @SuppressWarnings("UnstableApiUsage")
    public static final Item.Properties DEFAULT_LANTERN_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();

    static {
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(LanternBlock.makeId("ancient"), () -> new LanternBlock(new BlockConfig().name("Ancient Lantern").item(Items.ECHO_SHARD), "ancient"), DEFAULT_LANTERN_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(LanternBlock.makeId("doom"), () -> new LanternBlock(new BlockConfig().name("Doom Lantern").item(Items.CRIMSON_FUNGUS), "doom"), DEFAULT_LANTERN_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(LanternBlock.makeId("end"), () -> new LanternBlock(new BlockConfig().name("End Lantern").item(Items.ENDER_PEARL), "end"), DEFAULT_LANTERN_SETTINGS));

        // auto-suggested ideas from copilot
//        BLOCKS.add(registerWithItem(LanternBlock.makeId("crystal"), () -> new LanternBlock(new BlockConfig().name("Crystal Lantern").item(Items.ECHO_SHARD), "crystal"), DEFAULT_LANTERN_SETTINGS));
//        BLOCKS.add(registerWithItem(LanternBlock.makeId("fancy"), () -> new LanternBlock(new BlockConfig().name("Fancy Lantern").item(Items.ECHO_SHARD), "fancy"), DEFAULT_LANTERN_SETTINGS));
//        BLOCKS.add(registerWithItem(LanternBlock.makeId("iron"), () -> new LanternBlock(new BlockConfig().name("Iron Lantern").item(Items.ECHO_SHARD), "iron"), DEFAULT_LANTERN_SETTINGS));
//        BLOCKS.add(registerWithItem(LanternBlock.makeId("rustic"), () -> new LanternBlock(new BlockConfig().name("Rustic Lantern").item(Items.ECHO_SHARD), "rustic"), DEFAULT_LANTERN_SETTINGS));
//        BLOCKS.add(registerWithItem(LanternBlock.makeId("simple"), () -> new LanternBlock(new BlockConfig().name("Simple Lantern").item(Items.ECHO_SHARD), "simple"), DEFAULT_LANTERN_SETTINGS));
//        BLOCKS.add(registerWithItem(LanternBlock.makeId("stone"), () -> new LanternBlock(new BlockConfig().name("Stone Lantern").item(Items.ECHO_SHARD), "stone"), DEFAULT_LANTERN_SETTINGS));
//        BLOCKS.add(registerWithItem(LanternBlock.makeId("wooden"), () -> new LanternBlock(new BlockConfig().name("Wooden Lantern").item(Items.ECHO_SHARD), "wooden"), DEFAULT_LANTERN_SETTINGS));
    }
}
