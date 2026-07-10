package com.chimericdream.minekea.block.building.storage;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class ItemStorageBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS;
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty IS_BAGGED;

    public final Identifier BLOCK_ID;
    public final BlockConfig config;
    public final boolean isBaggedItem;
    public final StorageModel model;

    static {
        AXIS = BlockStateProperties.AXIS;
        FACING = BlockStateProperties.FACING;
        IS_BAGGED = BooleanProperty.create("is_bagged");
    }

    public ItemStorageBlock(BlockConfig config) {
        this(config, false);
    }

    public ItemStorageBlock(BlockConfig config, boolean isBaggedItem) {
        this(config, isBaggedItem, isBaggedItem ? StorageModel.BAGGED : StorageModel.DEFAULT);
    }

    public ItemStorageBlock(BlockConfig config, boolean isBaggedItem, StorageModel model) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        registerDefaultState(
            getStateDefinition()
                .any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(IS_BAGGED, false)
        );

        this.BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
        this.isBaggedItem = isBaggedItem;
        this.model = model;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("storage/compressed/%s", material));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(IS_BAGGED)) {
            return Shapes.or(
                Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0),
                Block.box(1.0, 9.0, 1.0, 15.0, 10.0, 15.0),
                Block.box(0.0, 10.0, 0.0, 16.0, 13.0, 16.0),
                Block.box(1.0, 13.0, 1.0, 15.0, 16.0, 15.0)
            );
        }

        return Shapes.block();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, FACING, IS_BAGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState()
            .setValue(AXIS, ctx.getClickedFace().getAxis())
            .setValue(FACING, ctx.getNearestLookingDirection().getOpposite());

        if (this.isBaggedItem) {
            return state.setValue(IS_BAGGED, true);
        }

        return state.setValue(IS_BAGGED, false);
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(IS_BAGGED) && stack.is(Items.SHEARS)) {
            if (world.isClientSide()) {
                world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else {
                world.setBlockAndUpdate(pos, state.setValue(IS_BAGGED, false));
                world.blockEntityChanged(pos);
            }

            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(IS_BAGGED) && this.isBaggedItem && stack.is(Items.LEATHER)) {
            if (world.isClientSide()) {
                world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else {
                world.setBlockAndUpdate(pos, state.setValue(IS_BAGGED, true));
                world.blockEntityChanged(pos);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public enum StorageModel {
        DEFAULT,
        BAGGED,
        FACING,
        AXIS,
        CUSTOM;
    }
}
