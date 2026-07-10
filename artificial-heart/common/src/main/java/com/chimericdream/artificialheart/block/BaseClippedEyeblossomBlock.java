package com.chimericdream.artificialheart.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

abstract public class BaseClippedEyeblossomBlock extends FlowerBlock {
    public BaseClippedEyeblossomBlock(Holder<MobEffect> stewEffect, float effectLengthInSeconds, Properties settings) {
        super(stewEffect, effectLengthInSeconds, settings);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void onEntityCollision(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide() && world.getDifficulty() != Difficulty.PEACEFUL && entity instanceof Bee beeEntity) {
            if (Bee.attractsBees(state) && !beeEntity.hasEffect(MobEffects.POISON)) {
                beeEntity.addEffect(this.getBeeInteractionEffect());
            }
        }
    }

    public MobEffectInstance getBeeInteractionEffect() {
        return new MobEffectInstance(MobEffects.POISON, 25);
    }
}
