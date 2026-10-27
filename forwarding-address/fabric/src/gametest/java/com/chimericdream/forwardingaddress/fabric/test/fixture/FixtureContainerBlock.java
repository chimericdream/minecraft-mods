package com.chimericdream.forwardingaddress.fabric.test.fixture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A {@link FixtureBlock} that owns a {@link FixtureContainerEntity}. Which block-entity type (and how
 * many hidden slots) it uses is what lets the tests cover the same-type and different-type paths.
 */
public class FixtureContainerBlock extends FixtureBlock implements EntityBlock {
    private final Supplier<BlockEntityType<?>> type;
    private final int hidden;

    public FixtureContainerBlock(Properties properties, Supplier<BlockEntityType<?>> type, int hidden) {
        super(properties);
        this.type = type;
        this.hidden = hidden;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FixtureContainerEntity(type, pos, state, hidden);
    }
}
