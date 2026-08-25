package com.chimericdream.effectivegear.client;

import com.chimericdream.effectivegear.enchantment.PreservingHelper;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Overrides vanilla's block color registration for the leaves the Preserving enchantment can affect:
 * a {@code preserved} block (see {@link com.chimericdream.effectivegear.mixin.EG$LeavesBlockMixin})
 * always renders its fixed default color instead of sampling the surrounding biome.
 */
public final class PreservingBlockColors {
    private static final BlockTintSource TINT_SOURCE = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return FoliageColor.FOLIAGE_DEFAULT;
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            if (state.getValue(PreservingHelper.PRESERVED)) {
                return FoliageColor.FOLIAGE_DEFAULT;
            }

            return BiomeColors.getAverageFoliageColor(level, pos);
        }
    };

    private PreservingBlockColors() {
    }

    public static void init() {
        Block[] blocks = PreservingHelper.getPreservableLeaves().toArray(new Block[0]);
        ColorHandlerRegistry.registerBlockColors(TINT_SOURCE, blocks);
    }
}
