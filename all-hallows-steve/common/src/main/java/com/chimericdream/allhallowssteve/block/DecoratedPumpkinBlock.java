package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

/**
 * A pumpkin dyed by the {@link CarvingStationBlock}. A separate block from vanilla's pumpkin rather
 * than a retexture of it, so the stored {@link com.chimericdream.allhallowssteve.component.type.DyedColorComponent}
 * has somewhere of its own to live and render from (see {@link DecoratedPumpkinBlockEntity}) without
 * touching vanilla pumpkin's stem-growth behavior.
 * <p>
 * {@code FACING} points at the placer, matching {@link CarvingStationBlock}'s convention — the same
 * side a future stencil overlay is meant to face.
 */
public class DecoratedPumpkinBlock extends BaseEntityBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "decorated_pumpkin");
    public static final MapCodec<DecoratedPumpkinBlock> CODEC = simpleCodec(DecoratedPumpkinBlock::create);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    static DecoratedPumpkinBlock create(BlockBehaviour.Properties settings) {
        return new DecoratedPumpkinBlock() {
        };
    }

    public DecoratedPumpkinBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public @NotNull MapCodec<DecoratedPumpkinBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecoratedPumpkinBlockEntity(pos, state);
    }
}
