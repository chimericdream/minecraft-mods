package com.chimericdream.jdcrafte.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A single-block feeding trough with a "level" property (0-3) representing how full it is, and a
 * "food" property recording which of the accepted item types it's currently holding (only relevant
 * once level > 0). The trough is visually symmetric front-to-back, so it only needs to track which
 * horizontal axis it runs along rather than a full facing direction.
 */
public class FeedingTroughBlock extends BaseEntityBlock {
    public static final MapCodec<FeedingTroughBlock> CODEC = simpleCodec(FeedingTroughBlock::new);

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
    public static final EnumProperty<FoodType> FOOD = EnumProperty.create("food", FoodType.class);

    // Shape is authored against the trough model as it sits in models/block/trough.json (long axis
    // running along X) and rotated 90 degrees for the Z-axis orientation. The four angled legs are
    // collapsed into two bounding boxes (one per end) rather than modeled precisely.
    private static final Map<Direction.Axis, VoxelShape> SHAPES;

    static {
        VoxelShape xAxis = Shapes.or(
            Block.box(0, 3, 3, 16, 4, 13),
            Block.box(0, 4, 3, 16, 10, 4),
            Block.box(0, 4, 12, 16, 10, 13),
            Block.box(0, 4, 4, 1, 10, 12),
            Block.box(15, 4, 4, 16, 10, 12),
            Block.box(0.25, 0, 4, 1.25, 4, 12),
            Block.box(14.75, 0, 4, 15.75, 4, 12)
        );

        SHAPES = Map.of(
            Direction.Axis.X, xAxis,
            Direction.Axis.Z, Shapes.rotate(xAxis, OctahedralGroup.BLOCK_ROT_Y_90)
        );
    }

    public FeedingTroughBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(AXIS, Direction.Axis.X)
                .setValue(LEVEL, 0)
                .setValue(FOOD, FoodType.WHEAT)
        );
    }

    @Override
    protected MapCodec<FeedingTroughBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, LEVEL, FOOD);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(AXIS));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FeedingTroughBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity trough)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!FeedingTroughBlockEntity.isValidFood(held)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        int countBefore = held.getCount();
        ItemStack remainder = trough.tryInsert(held);
        player.setItemInHand(InteractionHand.MAIN_HAND, remainder);

        return remainder.getCount() == countBefore ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FeedingTroughBlockEntity trough) {
            Containers.dropContents(level, pos, trough);
        }

        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    /**
     * The accepted feed items. Model selection uses the serialized name to look up
     * {@code trough_level<N>_<name>} in {@code FeedingTroughBlockDataGenerator}.
     */
    public enum FoodType implements StringRepresentable {
        WHEAT("wheat", Items.WHEAT),
        BEETROOT("beetroot", Items.BEETROOT),
        CARROTS("carrots", Items.CARROT),
        POTATOES("potatoes", Items.POTATO),
        WHEAT_SEEDS("wheat_seeds", Items.WHEAT_SEEDS);

        private final String serializedName;
        private final Item item;

        FoodType(String serializedName, Item item) {
            this.serializedName = serializedName;
            this.item = item;
        }

        public Item getItem() {
            return item;
        }

        public static FoodType fromItem(Item item) {
            for (FoodType type : values()) {
                if (type.item == item) {
                    return type;
                }
            }

            return null;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
