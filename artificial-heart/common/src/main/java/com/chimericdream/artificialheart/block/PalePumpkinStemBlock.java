package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import com.chimericdream.artificialheart.item.PalePumpkinSeedsItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;

public class PalePumpkinStemBlock extends StemBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_pumpkin_stem");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);

    public PalePumpkinStemBlock() {
        super(
            PalePumpkinBlock.BLOCK_REGISTRY_KEY,
            AttachedPalePumpkinStemBlock.BLOCK_REGISTRY_KEY,
            PalePumpkinSeedsItem.ITEM_REGISTRY_KEY,
            BlockTags.SUPPORTS_PUMPKIN_STEM,
            BlockTags.SUPPORTS_PUMPKIN_STEM_FRUIT,
            Properties.ofFullCopy(Blocks.PUMPKIN_STEM).setId(BLOCK_REGISTRY_KEY)
        );
    }
}
