package com.chimericdream.sponj.blocks;

import com.chimericdream.sponj.ModInfo;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class LavaSponjBlock extends AbstractSponjBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "lava_sponj");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public LavaSponjBlock() {
        super(BLOCK_REGISTRY_KEY);
    }

    @Override
    protected TagKey<Fluid> getFluidTag() {
        return FluidTags.LAVA;
    }

    @Override
    protected Block getWetBlock() {
        return ModBlocks.WET_LAVA_SPONJ_BLOCK.get();
    }

    @Override
    protected Block getDryBlock() {
        return ModBlocks.LAVA_SPONJ_BLOCK.get();
    }

    @Override
    protected List<Block> getConnectedBlockTypes() {
        return ModBlocks.getLavaSponjBlocks();
    }

    @Override
    protected BlockState getAbsorbedFluidState() {
        return Blocks.LAVA.defaultBlockState();
    }

    @Override
    protected boolean absorbsWashableBlocks() {
        return false;
    }
}
