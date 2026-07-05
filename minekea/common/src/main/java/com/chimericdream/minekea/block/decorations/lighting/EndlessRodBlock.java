package com.chimericdream.minekea.block.decorations.lighting;

import com.chimericdream.minekea.ModInfo;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class EndlessRodBlock extends RodBlock {
    public final static ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "decorations/lighting/endless_rod");
    public static final MapCodec<EndlessRodBlock> CODEC = simpleCodec(EndlessRodBlock::new);

    public @NotNull MapCodec<EndlessRodBlock> codec() {
        return CODEC;
    }

    public EndlessRodBlock(Properties settings) {
        this();
    }

    public EndlessRodBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getClickedFace();
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(direction.getOpposite()));

        return blockState.is(this) && blockState.getValue(FACING) == direction
            ? this.defaultBlockState().setValue(FACING, direction.getOpposite())
            : this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        Direction direction = state.getValue(FACING);

        double d = (double) pos.getX() + 0.55 - (double) (random.nextFloat() * 0.1F);
        double e = (double) pos.getY() + 0.55 - (double) (random.nextFloat() * 0.1F);
        double f = (double) pos.getZ() + 0.55 - (double) (random.nextFloat() * 0.1F);
        double g = 0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F;

        if (random.nextInt(5) == 0) {
            world.addAlwaysVisibleParticle(
                ParticleTypes.END_ROD,
                d + (double) direction.getStepX() * g,
                e + (double) direction.getStepY() * g,
                f + (double) direction.getStepZ() * g,
                random.nextGaussian() * 0.005,
                random.nextGaussian() * 0.005,
                random.nextGaussian() * 0.005
            );
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

//    public void register() {
//        RegistryHelpers.registerBlockWithItem(this, BLOCK_ID);
//        FabricItemGroupEventHelpers.addBlockToItemGroup(this, ItemGroups.FUNCTIONAL);
//    }
}
