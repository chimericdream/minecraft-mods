package com.chimericdream.logallthethings.windowlog;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.chimericdream.logallthethings.ModInfo;

public final class WindowLogTags {
    /** Blocks a compatible pane can be window-logged into. Seeded with slabs and stairs only for now. */
    public static final TagKey<Block> WINDOWABLE = TagKey.create(Registries.BLOCK, id("windowable"));

    /** Blocks that count as a "pane" for window-logging. Seeded with glass panes, iron bars, and copper bars. */
    public static final TagKey<Block> WINDOW = TagKey.create(Registries.BLOCK, id("window"));

    private WindowLogTags() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }
}
