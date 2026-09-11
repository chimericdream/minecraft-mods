package com.chimericdream.allhallowssteve.client.color;

import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Tints the placed dyed pumpkin block from the color stored on its {@link DecoratedPumpkinBlockEntity}. */
public final class DecoratedPumpkinBlockColors {
    public static final BlockTintSource TINT_SOURCE = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return ARGB.opaque(DyedColorComponent.DEFAULT_COLOR);
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            if (level != null && pos != null) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof DecoratedPumpkinBlockEntity decoratedPumpkin) {
                    return ARGB.opaque(decoratedPumpkin.getColor());
                }
            }

            return color(state);
        }
    };

    private DecoratedPumpkinBlockColors() {
    }
}
