package com.chimericdream.minekea.tag;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class MinekeaBlockTags {
    public static final TagKey<Block> BEAMS = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "beams"));
    public static final TagKey<Block> DISPLAY_CASES = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "display_cases"));
    public static final TagKey<Block> FRAMED_PLANKS = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "framed_planks"));
    public static final TagKey<Block> LANTERNS = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "lanterns"));
    public static final TagKey<Block> PILLOWS = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "pillows"));
}
