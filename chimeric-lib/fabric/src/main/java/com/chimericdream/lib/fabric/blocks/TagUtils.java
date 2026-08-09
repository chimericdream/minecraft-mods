package com.chimericdream.lib.fabric.blocks;

import com.chimericdream.lib.util.Tool;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class TagUtils {
    /**
     * The {@code getBuilder.apply(tool.getMineableTag()).setReplace(false).add(block...key())}
     * idiom repeated across nearly every block datagen class's {@code configureBlockTags}. A null
     * tool defaults to {@link Tool#PICKAXE}, matching most existing call sites' fallback.
     */
    public static void applyMineableTag(Function<TagKey<Block>, TagAppender<Block>> getBuilder, @Nullable Tool tool, Block block) {
        applyMineableTag(getBuilder, tool, Tool.PICKAXE, block);
    }

    /** Same as {@link #applyMineableTag(Function, Tool, Block)}, with an explicit default tool. */
    public static void applyMineableTag(Function<TagKey<Block>, TagAppender<Block>> getBuilder, @Nullable Tool tool, Tool defaultTool, Block block) {
        Tool resolved = Optional.ofNullable(tool).orElse(defaultTool);

        getBuilder.apply(resolved.getMineableTag())
            .setReplace(false)
            .add(block.builtInRegistryHolder().key());
    }
}
