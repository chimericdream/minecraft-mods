package com.chimericdream.minekea.crop;

import com.chimericdream.minekea.ModInfo;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WarpedWartPlantBlock extends VegetationBlock {
    public static final MapCodec<WarpedWartPlantBlock> CODEC = simpleCodec(_unused -> new WarpedWartPlantBlock());

    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "crops/warped_wart/block");
    public static final IntegerProperty AGE;
    private static final VoxelShape[] AGE_TO_SHAPE;

    public WarpedWartPlantBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_WART).mapColor(MapColor.WARPED_WART_BLOCK).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));

        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    public @NotNull MapCodec<WarpedWartPlantBlock> codec() {
        return CODEC;
    }

    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AGE_TO_SHAPE[state.getValue(AGE)];
    }

    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(Blocks.SOUL_SAND);
    }

    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int i = state.getValue(AGE);
        if (i < 3 && random.nextInt(10) == 0) {
            state = state.setValue(AGE, i + 1);
            world.setBlock(pos, state, 2);
        }
    }

    public ItemStack getPickStack(LevelReader world, BlockPos pos, BlockState state) {
        return new ItemStack(ModCrops.WARPED_WART_ITEM);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    static {
        AGE = BlockStateProperties.AGE_3;
        AGE_TO_SHAPE = new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
        };
    }
}
