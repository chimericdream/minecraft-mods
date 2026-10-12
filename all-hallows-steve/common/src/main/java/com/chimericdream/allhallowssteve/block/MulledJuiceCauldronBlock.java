package com.chimericdream.allhallowssteve.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

/** A finished batch of mulled cider or juice. It stays hot until emptied; every serving is the same mulled drink. */
public class MulledJuiceCauldronBlock extends LayeredCauldronBlock {
    public MulledJuiceCauldronBlock(JuiceCauldronType type) {
        super(
            Biome.Precipitation.NONE,
            type.mulledBehaviors(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).setId(REGISTRY_HELPER.makeBlockRegistryKey(type.mulledCauldronId()))
        );

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LEVEL, 1)
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) == 0) {
            level.addParticle(
                ParticleTypes.CLOUD,
                pos.getX() + 0.3 + random.nextDouble() * 0.4,
                pos.getY() + getContentHeight(state),
                pos.getZ() + 0.3 + random.nextDouble() * 0.4,
                0.0, 0.03, 0.0
            );
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean isPrecise) {
        handler.apply(InsideBlockEffectType.EXTINGUISH);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return Items.CAULDRON.getDefaultInstance();
    }
}
