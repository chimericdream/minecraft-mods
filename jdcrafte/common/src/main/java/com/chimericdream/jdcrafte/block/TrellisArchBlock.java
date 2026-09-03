package com.chimericdream.jdcrafte.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.lib.blocks.BlockConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.chimericdream.jdcrafte.JDCrafteMod.REGISTRY_HELPER;

/**
 * A decorative, climbable (tagged {@code minecraft:climbable} in {@code TrellisArchBlockDataGenerator},
 * same as {@code TrellisBlock}) arch built from 3 blocks in a row along {@link #FACING}, the same way a
 * bed occupies 2 (see vanilla {@code BedBlock}, which this closely mirrors) - one per vanilla wood
 * type, same as {@code TrellisBlock} (see {@code ModBlocks.TRELLIS_ARCH_BLOCKS}). Unlike a bed, the
 * clicked block isn't part of the structure's middle - it becomes {@link Part#BACK}, the end nearest
 * the player, and
 * - in {@link #setPlacedBy} - {@link Part#CENTER}/{@link Part#FRONT} extend outward from there, 1 and 2
 * blocks further in {@code FACING} respectively. So the whole arch sits beyond the targeted block, in
 * the direction faced, rather than straddling it. Breaking, or otherwise removing, any one of the 3
 * collapses the rest via {@link #updateShape}, same as a bed.
 *
 * <p>{@code models/block/trellis_arch_top.json} (used for {@link Part#CENTER}) is a single self
 * contained arch: authored with its low ends at local Z 0 and Z 16 and its peak in between, spanning
 * the block's full depth - not a thin wall-mounted slice like {@code trellis.json}. {@code
 * models/block/trellis_arch.json} (used for {@link Part#FRONT}/{@link Part#BACK}) is a short stub
 * that continues that low end outward. Both are texture templates like {@code trellis.json} (see
 * {@code TrellisArchBlockDataGenerator}), authored at the "identity" FACING = NORTH orientation
 * exactly like {@code TrellisBlock}, with the same per facing Y rotation applied per facing - except
 * {@link Part#BACK} additionally uses the rotation for {@code FACING.getOpposite()}, since its stub is
 * the same model as {@link Part#FRONT} mirrored.
 */
public class TrellisArchBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private static final Map<Direction, VoxelShape> CENTER_SHAPES = Shapes.rotateHorizontal(Block.box(0.0, 2.0, 0.0, 16.0, 7.0, 16.0));
    private static final Map<Direction, VoxelShape> SIDE_SHAPES = Shapes.rotateHorizontal(Block.box(0.0, 0.0, 14.0, 16.0, 3.0, 16.0));

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, material + "_trellis_arch");
    }

    public TrellisArchBlock(BlockConfig config) {
        super(config.getBaseSettings().noOcclusion().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, Part.CENTER));

        this.BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);

        return switch (state.getValue(PART)) {
            case CENTER -> CENTER_SHAPES.get(facing);
            case FRONT -> SIDE_SHAPES.get(facing);
            case BACK -> SIDE_SHAPES.get(facing.getOpposite());
        };
    }

    // The clicked block becomes BACK (the end nearest the player), and the other 2 extend outward from
    // there in FACING - CENTER 1 block further, FRONT 2 blocks further - rather than the clicked block
    // becoming CENTER. So the whole arch sits beyond the targeted block, in the direction faced.
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos centerPos = pos.relative(facing);
        BlockPos frontPos = pos.relative(facing, 2);

        if (!level.getBlockState(centerPos).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(centerPos)) {
            return null;
        }

        if (!level.getBlockState(frontPos).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(frontPos)) {
            return null;
        }

        return this.defaultBlockState().setValue(FACING, facing).setValue(PART, Part.BACK);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        Direction facing = state.getValue(FACING);
        level.setBlockAndUpdate(pos.relative(facing), state.setValue(PART, Part.CENTER));
        level.setBlockAndUpdate(pos.relative(facing, 2), state.setValue(PART, Part.FRONT));
    }

    // Only CENTER's loot table actually drops an item (see TrellisArchBlockDataGenerator) - any one
    // break always destroys CENTER too, either directly or via the updateShape cascade, so exactly one
    // item drops regardless of which of the 3 blocks the player targets. That cascade-triggered
    // destruction of CENTER goes through the normal drop-yielding path though (Block#updateOrDestroy
    // routes a shape update that resolves to air through Level#destroyBlock, same as mining), which
    // survives a creative pick just fine - creative's own "prevents block drops" check only suppresses
    // the *directly targeted* block's harvest, not a sibling's cascade-triggered one. So when a
    // creative player targets FRONT/BACK (not CENTER itself), silently pre-remove CENTER first with
    // its drop suppressed, before the normal removal even starts - mirrors vanilla BedBlock's own
    // playerWillDestroy exactly (there, only for BedPart.FOOT, since BedPart.HEAD is its "drops" part).
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.preventsBlockDrops() && state.getValue(PART) != Part.CENTER) {
            Direction facing = state.getValue(FACING);
            Direction towardCenter = state.getValue(PART) == Part.FRONT ? facing.getOpposite() : facing;
            BlockPos centerPos = pos.relative(towardCenter);
            BlockState centerState = level.getBlockState(centerPos);

            if (centerState.is(this) && centerState.getValue(PART) == Part.CENTER && centerState.getValue(FACING) == facing) {
                level.setBlock(centerPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, centerPos, Block.getId(centerState));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbor, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        Direction facing = state.getValue(FACING);
        Part expectedNeighborPart = expectedNeighborPart(state.getValue(PART), facing, directionToNeighbor);

        if (expectedNeighborPart != null) {
            boolean intact = neighborState.is(this) && neighborState.getValue(PART) == expectedNeighborPart && neighborState.getValue(FACING) == facing;
            if (!intact) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbor, neighborPos, neighborState, random);
    }

    // The Part the neighbor in directionToNeighbor must be for `part` to stay intact, or null if that
    // direction isn't structurally relevant to `part` (e.g. FRONT only cares about the CENTER-ward
    // neighbor, not the one beyond it).
    private static @Nullable Part expectedNeighborPart(Part part, Direction facing, Direction directionToNeighbor) {
        return switch (part) {
            case CENTER -> {
                if (directionToNeighbor == facing) {
                    yield Part.FRONT;
                }
                if (directionToNeighbor == facing.getOpposite()) {
                    yield Part.BACK;
                }
                yield null;
            }
            case FRONT -> directionToNeighbor == facing.getOpposite() ? Part.CENTER : null;
            case BACK -> directionToNeighbor == facing ? Part.CENTER : null;
        };
    }

    public enum Part implements StringRepresentable {
        CENTER("center"),
        FRONT("front"),
        BACK("back");

        private final String serializedName;

        Part(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
