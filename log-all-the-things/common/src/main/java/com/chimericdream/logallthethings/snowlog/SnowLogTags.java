package com.chimericdream.logallthethings.snowlog;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.chimericdream.logallthethings.ModInfo;

public final class SnowLogTags {
    /** Blocks a compatible snow layer item can be snow-logged onto - same host set as {@code carpetlog.CarpetLogTags#CARPETABLE} (slabs, straight stairs, walls, fences, chains, bars, glass panes). */
    public static final TagKey<Block> SNOWABLE = TagKey.create(Registries.BLOCK, id("snowable"));

    /** Blocks that count as a "snow layer" for snow-logging. Seeded with vanilla's snow layer block. */
    public static final TagKey<Block> SNOW_LAYER = TagKey.create(Registries.BLOCK, id("snow_layer"));

    private SnowLogTags() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }
}
