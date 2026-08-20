package com.chimericdream.camelnostrils.block;

import com.chimericdream.camelnostrils.ModInfo;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

public class GoldenCactusBlock extends CactusBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "golden_cactus");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final MapCodec<CactusBlock> CODEC = simpleCodec(GoldenCactusBlock::new);

    @Override
    public @NonNull MapCodec<CactusBlock> codec() {
        return CODEC;
    }

    public GoldenCactusBlock() {
        this(Properties.ofFullCopy(Blocks.CACTUS).setId(BLOCK_REGISTRY_KEY));
    }

    public GoldenCactusBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static GoldenCactusBlock create() {
        return new GoldenCactusBlock();
    }
}
