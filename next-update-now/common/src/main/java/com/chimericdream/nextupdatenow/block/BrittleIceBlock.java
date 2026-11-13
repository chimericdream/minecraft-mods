package com.chimericdream.nextupdatenow.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Ice that cracks after an entity steps on it, then breaks. The crack stage is the {@code age}
 * property. Once the cracking starts, it does not stop.
 */
public class BrittleIceBlock extends Block {
    public static final MapCodec<BrittleIceBlock> CODEC = simpleCodec(BrittleIceBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = 3;

    private static final int CRACK_DELAY_TICKS = 10;
    private static final TagKey<EntityType<?>> DOES_NOT_BREAK = TagKey.create(
        Registries.ENTITY_TYPE,
        Identifier.withDefaultNamespace("does_not_break_brittle_ice")
    );

    public BrittleIceBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && !entity.is(DOES_NOT_BREAK) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, CRACK_DELAY_TICKS);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age >= MAX_AGE) {
            level.destroyBlock(pos, false);
            return;
        }

        level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
        level.scheduleTick(pos, this, CRACK_DELAY_TICKS);
    }
}
