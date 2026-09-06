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
import org.jspecify.annotations.NonNull;

import java.util.Map;

import static com.chimericdream.jdcrafte.JDCrafteMod.REGISTRY_HELPER;

/**
 * A two-wide decorative wall plaque ("Home Sweet Hrmmm"), the same 2-block shape as vanilla
 * {@code BedBlock} and closely mirroring {@link TrellisArchBlock}'s (3-block) take on that pattern in
 * this mod - one per vanilla wood type (see {@code ModBlocks.HOME_SWEET_HRMM_BLOCKS}), matching {@link
 * TrellisBlock}/{@link TrellisArchBlock}'s per-material model-template approach, except only the
 * plank/"sign" texture varies (there's no log/stem dimension here), so it's built from just the 12 base
 * wood types rather than all 24 {@code TrellisBlock} covers. {@link #FACING} tracks which way the
 * plaque faces (same "opposite the placing player's look direction" convention as {@link
 * TrellisBlock}), and {@link #SIDE} says which half of the plaque - {@code home_sweet_hrmm_left.json}
 * or {@code home_sweet_hrmm_right.json} - a given position renders.
 *
 * <p>Right-clicking with the item places the {@link Side#LEFT} half at the targeted position and
 * {@link #setPlacedBy} places the matching {@link Side#RIGHT} half alongside it, in the direction
 * given by {@link #getOtherHalfDirection}. That direction is the placer's right hand as they look at
 * the finished plaque - see that method for the derivation - so the two textures line up.
 *
 * <p>Only {@link Side#LEFT}'s blockstate actually drops an item (see
 * {@code HomeSweetHrmmBlockDataGenerator}, same trick as vanilla's bed loot tables). {@link
 * #updateShape} clears either half once its partner is gone, so breaking - or otherwise removing -
 * either half always destroys the other too, and that cascade-triggered destruction goes through the
 * normal drop-yielding path same as direct mining - so exactly one item drops regardless of which half
 * was broken. That cascade isn't gamemode-aware though, so {@link #playerWillDestroy} pre-empts it for
 * creative players exactly like {@link TrellisArchBlock#playerWillDestroy} does.
 */
public class HomeSweetHrmmBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Side> SIDE = EnumProperty.create("side", Side.class);

    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.box(0.0, 3.0, 15.0, 16.0, 15.0, 16.0));

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, material + "_home_sweet_hrmm");
    }

    public HomeSweetHrmmBlock(BlockConfig config) {
        super(config.getBaseSettings().noOcclusion().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SIDE, Side.LEFT));

        this.BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIDE);
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    /**
     * Direction from this half toward its partner: the placer's right hand while looking at the front
     * of the plaque. {@link #FACING} points toward the viewer, so the viewer's look direction is its
     * opposite, and {@link Direction#getClockWise()} of a look direction is that viewer's right hand.
     */
    private static Direction getOtherHalfDirection(BlockState state) {
        Direction rightHand = state.getValue(FACING).getOpposite().getClockWise();
        return state.getValue(SIDE) == Side.LEFT ? rightHand : rightHand.getOpposite();
    }

    private static boolean isMatchingOtherHalf(BlockState state, BlockState otherState) {
        return otherState.is(state.getBlock())
            && otherState.getValue(FACING) == state.getValue(FACING)
            && otherState.getValue(SIDE) != state.getValue(SIDE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockState placementState = this.defaultBlockState().setValue(FACING, facing).setValue(SIDE, Side.LEFT);

        Level level = context.getLevel();
        BlockPos otherPos = context.getClickedPos().relative(getOtherHalfDirection(placementState));

        if (!level.getBlockState(otherPos).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(otherPos)) {
            return null;
        }

        return placementState;
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockPos otherPos = pos.relative(getOtherHalfDirection(state));
        level.setBlockAndUpdate(otherPos, state.setValue(SIDE, Side.RIGHT));
    }

    @Override
    protected @NonNull BlockState updateShape(@NonNull BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, @NonNull Direction directionToNeighbor, @NonNull BlockPos neighborPos, @NonNull BlockState neighborState, @NonNull RandomSource random) {
        if (directionToNeighbor == getOtherHalfDirection(state) && !isMatchingOtherHalf(state, neighborState)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbor, neighborPos, neighborState, random);
    }

    @Override
    public @NonNull BlockState playerWillDestroy(Level level, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull Player player) {
        if (!level.isClientSide() && player.preventsBlockDrops() && state.getValue(SIDE) != Side.LEFT) {
            BlockPos leftPos = pos.relative(getOtherHalfDirection(state));
            BlockState leftState = level.getBlockState(leftPos);

            if (isMatchingOtherHalf(state, leftState)) {
                level.setBlock(leftPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, leftPos, Block.getId(leftState));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    public enum Side implements StringRepresentable {
        LEFT("left"),
        RIGHT("right");

        private final String serializedName;

        Side(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public @NonNull String getSerializedName() {
            return serializedName;
        }
    }
}
