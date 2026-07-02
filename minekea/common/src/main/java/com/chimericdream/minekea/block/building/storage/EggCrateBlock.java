package com.chimericdream.minekea.block.building.storage;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.sound.MinekeaSoundGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

// @TODO: turn this into a slab-like block with top/bottom/full variations
public class EggCrateBlock extends Block implements SimpleWaterloggedBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "storage/egg_crate");

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public EggCrateBlock() {
        this(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID));
    }

    public EggCrateBlock(ResourceKey<Block> registryKey) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK).sound(MinekeaSoundGroup.EGG_CRATE_SOUND_GROUP).setId(registryKey));

        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
            .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.or(
            Block.box(0.0, 1.0, 0.0, 16.0, 2.0, 16.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0),
            Block.box(0.0, 9.0, 0.0, 16.0, 10.0, 16.0)
        );
    }
}
