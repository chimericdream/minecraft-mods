package com.chimericdream.lib.util;

import com.chimericdream.lib.tags.CommonBlockTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public enum Tool {
    AXE,
    HOE,
    PICKAXE,
    SHOVEL,
    SHEARS,
    NONE;

    public @Nullable TagKey<Item> getItemTag() {
        return switch (this) {
            case AXE -> ItemTags.AXES;
            case HOE -> ItemTags.HOES;
            case PICKAXE -> ItemTags.PICKAXES;
            case SHOVEL -> ItemTags.SHOVELS;
            case SHEARS, NONE -> null;
        };
    }

    public @Nullable TagKey<Block> getMineableTag() {
        return switch (this) {
            case AXE -> BlockTags.MINEABLE_WITH_AXE;
            case HOE -> BlockTags.MINEABLE_WITH_HOE;
            case PICKAXE -> BlockTags.MINEABLE_WITH_PICKAXE;
            case SHOVEL -> BlockTags.MINEABLE_WITH_SHOVEL;
            case SHEARS -> CommonBlockTags.SHEARS_MINEABLE;
            case NONE -> null;
        };
    }
}
