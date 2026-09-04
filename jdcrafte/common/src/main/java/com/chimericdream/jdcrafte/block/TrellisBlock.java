package com.chimericdream.jdcrafte.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.lib.blocks.BlockConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

import static com.chimericdream.jdcrafte.JDCrafteMod.REGISTRY_HELPER;

/**
 * A thin wooden lattice, one per vanilla wood type (see {@code ModBlocks.TRELLIS_BLOCKS}). Climbable
 * (tagged {@code minecraft:climbable} in {@code TrellisBlockDataGenerator}) and freestanding: unlike
 * a vanilla ladder, it needs no supporting wall behind it and won't break if a wall it was placed
 * against is later removed. {@link #FACING} just tracks which way the open lattice faces - set
 * opposite the placing player's look direction, like a sign - purely to orient the model.
 *
 * <p>{@code models/block/trellis.json} is authored with the lattice flush against the south side of
 * the block (open face pointing north), so {@link #SHAPES}' NORTH entry - and the unrotated model
 * variant in {@code TrellisBlockDataGenerator} - line up with that as the "identity" orientation that
 * the other 3 facings are rotated from.
 */
public class TrellisBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0));

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, material + "_trellis");
    }

    public TrellisBlock(BlockConfig config) {
        super(config.getBaseSettings().noOcclusion().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));

        this.BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbor, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbor, neighborPos, neighborState, random);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
