package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ArtificialCreakingHeartBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "artificial_creaking_heart");
    public static final RegistryKey<Block> BLOCK_REGISTRY_KEY = RegistryKey.of(RegistryKeys.BLOCK, BLOCK_ID);
    public static final RegistryKey<Item> ITEM_REGISTRY_KEY = RegistryKey.of(RegistryKeys.ITEM, BLOCK_ID);

    public static final BooleanProperty ACTIVE_UP;
    public static final BooleanProperty ACTIVE_NORTH;
    public static final BooleanProperty ACTIVE_EAST;
    public static final BooleanProperty ACTIVE_SOUTH;
    public static final BooleanProperty ACTIVE_WEST;
    public static final BooleanProperty ACTIVE_DOWN;
    public static final EnumProperty<Direction.Axis> AXIS;

    public ArtificialCreakingHeartBlock() {
        super(Settings.copy(Blocks.PALE_OAK_LOG).registryKey(BLOCK_REGISTRY_KEY));

        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(ACTIVE_UP, false)
                .with(ACTIVE_NORTH, false)
                .with(ACTIVE_EAST, false)
                .with(ACTIVE_SOUTH, false)
                .with(ACTIVE_WEST, false)
                .with(ACTIVE_DOWN, false)
                .with(AXIS, Direction.Axis.Y)
        );
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
            ACTIVE_UP,
            ACTIVE_NORTH,
            ACTIVE_EAST,
            ACTIVE_SOUTH,
            ACTIVE_WEST,
            ACTIVE_DOWN, AXIS
        );
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
        ACTIVE_UP = BooleanProperty.of("active_up");
        ACTIVE_NORTH = BooleanProperty.of("active_north");
        ACTIVE_EAST = BooleanProperty.of("active_east");
        ACTIVE_SOUTH = BooleanProperty.of("active_south");
        ACTIVE_WEST = BooleanProperty.of("active_west");
        ACTIVE_DOWN = BooleanProperty.of("active_down");
        AXIS = Properties.AXIS;
    }
}
