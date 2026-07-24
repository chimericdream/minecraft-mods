package com.chimericdream.sponj.blocks;

import com.chimericdream.sponj.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class WetLavaSponjBlock extends AbstractWetSponjBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wet_lava_sponj");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public WetLavaSponjBlock() {
        super(BLOCK_REGISTRY_KEY);
    }

    @Override
    protected Block getDryBlock() {
        return ModBlocks.LAVA_SPONJ_BLOCK.get();
    }

    @Override
    protected ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_LAVA;
    }

    @Override
    protected boolean shouldDryOut(Level world, BlockPos pos) {
        return world.dimension().identifier().equals(Identifier.withDefaultNamespace("the_end"));
    }
}
