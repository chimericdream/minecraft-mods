package com.chimericdream.houdiniblock.blocks;

import com.chimericdream.houdiniblock.HoudiniBlockMod;
import com.chimericdream.houdiniblock.items.HoudiniBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import static com.chimericdream.houdiniblock.items.ModItems.HOUDINI_BLOCK_ITEM;

public class HoudiniBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty PREVENT_ON_PLACE;
    public static final BooleanProperty PREVENT_ON_BREAK;
    public static final BooleanProperty PREVENT_ALL;
    public static final BooleanProperty REPLACE_BLOCK;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    static {
        PREVENT_ON_PLACE = BooleanProperty.create("prevent_on_place");
        PREVENT_ON_BREAK = BooleanProperty.create("prevent_on_break");
        PREVENT_ALL = BooleanProperty.create("prevent_all");
        REPLACE_BLOCK = BooleanProperty.create("replace_block");
    }

    public HoudiniBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(PREVENT_ON_PLACE, false)
                .setValue(PREVENT_ON_BREAK, true)
                .setValue(PREVENT_ALL, true)
                .setValue(REPLACE_BLOCK, true)
                .setValue(WATERLOGGED, false)
        );
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
            PREVENT_ON_PLACE,
            PREVENT_ON_BREAK,
            PREVENT_ALL,
            REPLACE_BLOCK,
            WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, HoudiniBlockItem.DEFAULT_NBT).copyTag();
        HoudiniBlockItem.PlacementMode mode = HoudiniBlockItem.PlacementMode.getFromNbt(nbt);

        return this.defaultBlockState()
            .setValue(PREVENT_ON_PLACE, mode == HoudiniBlockItem.PlacementMode.PREVENT_ON_PLACE)
            .setValue(PREVENT_ON_BREAK, mode == HoudiniBlockItem.PlacementMode.PREVENT_ON_BREAK)
            .setValue(PREVENT_ALL, mode == HoudiniBlockItem.PlacementMode.PREVENT_ALL)
            .setValue(REPLACE_BLOCK, mode == HoudiniBlockItem.PlacementMode.REPLACE_BLOCK)
            .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(REPLACE_BLOCK)) {
            return this.replaceWithBlockInHand(stack, state, world, pos, player, hand, hit);
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private InteractionResult replaceWithBlockInHand(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        try {
            if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                this.spawnDestroyParticles(world, player, pos, state);
                this.spawnHoudiniBlockItem(world, player, pos);

                BlockPlaceContext placementContext = new BlockPlaceContext(player, hand, stack, hit);
                placementContext.relativePos = pos;

                world.setBlockAndUpdate(pos, block.getStateForPlacement(placementContext));

                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                return InteractionResult.SUCCESS;
            }
        } catch (RuntimeException e) {
            HoudiniBlockMod.LOGGER.error("Error in HoudiniBlock$replaceWithBlockInHand", e);
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        this.spawnDestroyParticles(world, player, pos, state);
        this.spawnHoudiniBlockItem(world, player, pos);

        if (state.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        }

        return state;
    }

    private void spawnHoudiniBlockItem(Level world, Player player, BlockPos pos) {
        if (!player.isCreative()) {
            ItemEntity itemEntity = new ItemEntity(
                world,
                (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D,
                (double) pos.getZ() + 0.5D,
                HOUDINI_BLOCK_ITEM.get().getDefaultInstance()
            );

            itemEntity.setDefaultPickUpDelay();

            world.addFreshEntity(itemEntity);
        }
    }
}
