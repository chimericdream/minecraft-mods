package com.chimericdream.effectivegear.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

/**
 * A button with all of {@link ButtonBlock}'s redstone/neighbor-update/press-ticking behavior but no
 * model and no outline/selection shape, so the redstone trim's momentary button placement (see
 * {@code RedstoneTrimPulses}) never flashes a visible block on screen.
 */
public class EGInvisibleButtonBlock extends ButtonBlock {
    public EGInvisibleButtonBlock(BlockSetType type, int ticksToStayPressed, Properties properties) {
        super(type, ticksToStayPressed, properties);
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NonNull VoxelShape getShape(
        @NonNull BlockState state,
        @NonNull BlockGetter level,
        @NonNull BlockPos pos,
        @NonNull CollisionContext context
    ) {
        return Shapes.empty();
    }
}
