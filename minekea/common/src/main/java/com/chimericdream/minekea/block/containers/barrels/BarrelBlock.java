package com.chimericdream.minekea.block.containers.barrels;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.entity.block.containers.MinekeaBarrelBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BarrelBlock extends BaseEntityBlock {
    public static final MapCodec<BarrelBlock> CODEC = simpleCodec(BarrelBlock::new);

    public final Identifier BLOCK_ID;
    public final BlockConfig config;
    public final String sideTextureKey;
    public final String faceTextureKey;

    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty OPEN;

    static {
        FACING = BlockStateProperties.FACING;
        OPEN = BlockStateProperties.OPEN;
    }

    public BarrelBlock(Properties settings) {
        this(new BlockConfig().material("acacia").materialName("Acacia").ingredient(Blocks.ACACIA_PLANKS).ingredient("slab", Blocks.ACACIA_SLAB), "acacia_planks", "stripped_acacia_log");
    }

    @Override
    protected @NotNull MapCodec<BarrelBlock> codec() {
        return CODEC;
    }

    public BarrelBlock(BlockConfig config, String sideTextureKey, String faceTextureKey) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
        this.sideTextureKey = sideTextureKey;
        this.faceTextureKey = faceTextureKey;

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("containers/barrels/%s", material));
    }

    public BlockConfig getConfig() {
        return config;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MinekeaBarrelBlockEntity) {
                player.openMenu((MinekeaBarrelBlockEntity) blockEntity);
                player.awardStat(Stats.OPEN_BARREL);
                PiglinAi.angerNearbyPiglins((ServerLevel) world, player, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MinekeaBarrelBlockEntity) {
            ((MinekeaBarrelBlockEntity) blockEntity).tick();
        }

    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MinekeaBarrelBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }
}
