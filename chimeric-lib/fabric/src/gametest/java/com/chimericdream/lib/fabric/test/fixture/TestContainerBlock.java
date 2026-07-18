package com.chimericdream.lib.fabric.test.fixture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * A throwaway {@link EntityBlock} whose block entity is a {@link TestContainerBlockEntity}. Registered
 * only from the test mod's entrypoint; never shipped. Exists so GameTests can place a real container
 * block in the world (and, e.g., let a vanilla hopper feed it).
 */
public class TestContainerBlock extends Block implements EntityBlock {
    public TestContainerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestContainerBlockEntity(pos, state);
    }
}
