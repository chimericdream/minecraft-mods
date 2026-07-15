package com.chimericdream.minekea.block.decorations.candles;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class VotiveCandles implements ModThingGroup {
    @SuppressWarnings("UnstableApiUsage")
    public static final Item.Properties DEFAULT_CANDLE_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();

    static {
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("plain"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.CANDLE), "plain"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("white"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.white()), "white"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("light_gray"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.lightGray()), "light_gray"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("gray"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.gray()), "gray"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("black"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.black()), "black"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("brown"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.brown()), "brown"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("red"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.red()), "red"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("orange"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.orange()), "orange"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("yellow"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.yellow()), "yellow"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("lime"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.lime()), "lime"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("green"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.green()), "green"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("cyan"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.cyan()), "cyan"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("light_blue"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.lightBlue()), "light_blue"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("blue"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.blue()), "blue"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("purple"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.purple()), "purple"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("magenta"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.magenta()), "magenta"), DEFAULT_CANDLE_SETTINGS));
        BLOCKS.add(REGISTRY_HELPER.registerWithItem(VotiveCandleBlock.makeId("pink"), () -> new VotiveCandleBlock(new BlockConfig().ingredient(Blocks.DYED_CANDLE.pink()), "pink"), DEFAULT_CANDLE_SETTINGS));
    }
}
