package com.chimericdream.logallthethings.windowlog;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.chimericdream.logallthethings.ModInfo;

public final class WindowLoggingTags {
    /** Blocks a compatible pane can be window-logged into. Seeded with slabs and stairs only for now. */
    public static final TagKey<Block> WINDOW_LOGGABLE = TagKey.create(Registries.BLOCK, id("window_loggable"));

    /** Blocks that count as a "pane" for window-logging. Seeded with glass panes only for now. */
    public static final TagKey<Block> WINDOW = TagKey.create(Registries.BLOCK, id("window"));

    private WindowLoggingTags() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }
}
