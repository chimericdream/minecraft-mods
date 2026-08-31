package com.chimericdream.logallthethings.carpetlog;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.chimericdream.logallthethings.ModInfo;

public final class CarpetLogTags {
    /** Blocks a compatible carpet can be carpet-logged onto. Seeded with slabs and stairs only for now. */
    public static final TagKey<Block> CARPETABLE = TagKey.create(Registries.BLOCK, id("carpetable"));

    /** Blocks that count as a "carpet" for carpet-logging. Seeded with the wool and moss carpets. */
    public static final TagKey<Block> CARPET = TagKey.create(Registries.BLOCK, id("carpet"));

    private CarpetLogTags() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }
}
