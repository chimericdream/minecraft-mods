package com.chimericdream.logallthethings.fabric.test;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * Test-only fixture blocks. They must be registered from a mod initializer: the block registry is
 * frozen by the time a GameTest runs, and constructing a {@link Block} needs an intrusive holder,
 * which a frozen registry refuses to create.
 */
public class LattTestBlocks implements ModInitializer {
    /**
     * A {@code CrossCollisionBlock} subclass that never gets {@code lavalogged}, because it extends
     * the abstract class directly (like Refurbished Furniture's lattice fence) so neither
     * {@code FenceBlock} nor {@code IronBarsBlock}'s state-definition mixin applies to it.
     * Registering it is the regression: vanilla calls {@code getFluidState} while it builds each
     * state's cache, which crashed at load (issue #141) when the mixin read the missing property.
     */
    public static final class BareCrossCollisionBlock extends CrossCollisionBlock {
        private static final MapCodec<BareCrossCollisionBlock> CODEC = simpleCodec(BareCrossCollisionBlock::new);

        BareCrossCollisionBlock(BlockBehaviour.Properties properties) {
            super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, properties);
            registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
        }

        /**
         * {@code CrossCollisionBlock} only declares these properties. Each concrete subclass adds them
         * itself, and vanilla's {@code getFluidState} reads {@code WATERLOGGED} unconditionally, so a
         * realistic fixture must add them too. It deliberately does not add {@code lavalogged}.
         */
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(NORTH, EAST, SOUTH, WEST, WATERLOGGED);
        }

        @Override
        protected MapCodec<? extends CrossCollisionBlock> codec() {
            return CODEC;
        }
    }

    public static final Identifier BARE_CROSS_COLLISION_ID = Identifier.fromNamespaceAndPath("logallthethings_test", "bare_cross_collision");

    public static BareCrossCollisionBlock bareCrossCollision;

    @Override
    public void onInitialize() {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, BARE_CROSS_COLLISION_ID);
        bareCrossCollision = Registry.register(
            BuiltInRegistries.BLOCK,
            key,
            new BareCrossCollisionBlock(BlockBehaviour.Properties.of().setId(key))
        );
    }
}
