package com.chimericdream.minekea.block.decorations.candles;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CandleBlock;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class VotiveCandleBlock extends CandleBlock {
    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;
    public final String color;

    public VotiveCandleBlock(BlockConfig config, String color) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(color))));

        BLOCK_ID = makeId(color);
        this.config = config;
        this.color = color;
    }

    public static ResourceLocation makeId(String color) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("decorations/candles/%s_votive_candle", color));
    }
}
