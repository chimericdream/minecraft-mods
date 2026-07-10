package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class ArtificialCreakingHeartBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "artificial_creaking_heart");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final BooleanProperty ACTIVE_UP;
    public static final BooleanProperty ACTIVE_NORTH;
    public static final BooleanProperty ACTIVE_EAST;
    public static final BooleanProperty ACTIVE_SOUTH;
    public static final BooleanProperty ACTIVE_WEST;
    public static final BooleanProperty ACTIVE_DOWN;
    public static final EnumProperty<Direction.Axis> AXIS;

    public ArtificialCreakingHeartBlock() {
        super(Properties.ofFullCopy(Blocks.PALE_OAK_LOG).setId(BLOCK_REGISTRY_KEY));

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(ACTIVE_UP, false)
                .setValue(ACTIVE_NORTH, false)
                .setValue(ACTIVE_EAST, false)
                .setValue(ACTIVE_SOUTH, false)
                .setValue(ACTIVE_WEST, false)
                .setValue(ACTIVE_DOWN, false)
                .setValue(AXIS, Direction.Axis.Y)
        );
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
            ACTIVE_UP,
            ACTIVE_NORTH,
            ACTIVE_EAST,
            ACTIVE_SOUTH,
            ACTIVE_WEST,
            ACTIVE_DOWN, AXIS
        );
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(AXIS, ctx.getClickedFace().getAxis());
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public static BooleanProperty getFaceProp(Direction hitFace) {
        return switch (hitFace) {
            case DOWN -> ACTIVE_DOWN;
            case UP -> ACTIVE_UP;
            case NORTH -> ACTIVE_NORTH;
            case SOUTH -> ACTIVE_SOUTH;
            case WEST -> ACTIVE_WEST;
            case EAST -> ACTIVE_EAST;
        };
    }

    static {
        ACTIVE_UP = BooleanProperty.create("active_up");
        ACTIVE_NORTH = BooleanProperty.create("active_north");
        ACTIVE_EAST = BooleanProperty.create("active_east");
        ACTIVE_SOUTH = BooleanProperty.create("active_south");
        ACTIVE_WEST = BooleanProperty.create("active_west");
        ACTIVE_DOWN = BooleanProperty.create("active_down");
        AXIS = BlockStateProperties.AXIS;
    }
}
