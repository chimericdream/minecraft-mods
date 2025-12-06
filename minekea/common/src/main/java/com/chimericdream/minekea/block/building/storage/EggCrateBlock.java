package com.chimericdream.minekea.block.building.storage;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.sound.MinekeaSoundGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

// @TODO: turn this into a slab-like block with top/bottom/full variations
public class EggCrateBlock extends Block implements Waterloggable {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "storage/egg_crate");

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public EggCrateBlock() {
        this(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID));
    }

    public EggCrateBlock(RegistryKey<Block> registryKey) {
        super(AbstractBlock.Settings.copy(Blocks.NETHER_WART_BLOCK).sounds(MinekeaSoundGroup.EGG_CRATE_SOUND_GROUP).registryKey(registryKey));

        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
            .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
            Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 2.0, 16.0),
            Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0),
            Block.createCuboidShape(0.0, 9.0, 0.0, 16.0, 10.0, 16.0)
        );
    }
}
