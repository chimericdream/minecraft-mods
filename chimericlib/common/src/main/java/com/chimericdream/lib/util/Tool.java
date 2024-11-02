package com.chimericdream.lib.util;

import com.chimericdream.lib.tags.CommonBlockTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
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
            case AXE -> BlockTags.AXE_MINEABLE;
            case HOE -> BlockTags.HOE_MINEABLE;
            case PICKAXE -> BlockTags.PICKAXE_MINEABLE;
            case SHOVEL -> BlockTags.SHOVEL_MINEABLE;
            case SHEARS -> CommonBlockTags.SHEARS_MINEABLE;
            case NONE -> null;
        };
    }
}
