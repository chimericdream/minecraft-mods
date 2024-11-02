package com.chimericdream.houdiniblock.blocks;

import com.chimericdream.houdiniblock.HoudiniBlockMod;
import com.chimericdream.houdiniblock.items.HoudiniBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.chimericdream.houdiniblock.items.ModItems.HOUDINI_BLOCK_ITEM;

public class HoudiniBlock extends Block implements Waterloggable {
    public static final BooleanProperty PREVENT_ON_PLACE;
    public static final BooleanProperty PREVENT_ON_BREAK;
    public static final BooleanProperty PREVENT_ALL;
    public static final BooleanProperty REPLACE_BLOCK;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    static {
        PREVENT_ON_PLACE = BooleanProperty.of("prevent_on_place");
        PREVENT_ON_BREAK = BooleanProperty.of("prevent_on_break");
        PREVENT_ALL = BooleanProperty.of("prevent_all");
        REPLACE_BLOCK = BooleanProperty.of("replace_block");
    }

    public HoudiniBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(PREVENT_ON_PLACE, false)
                .with(PREVENT_ON_BREAK, true)
                .with(PREVENT_ALL, true)
                .with(REPLACE_BLOCK, true)
                .with(WATERLOGGED, false)
        );
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
            PREVENT_ON_PLACE,
            PREVENT_ON_BREAK,
            PREVENT_ALL,
            REPLACE_BLOCK,
            WATERLOGGED
        );
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemStack stack = ctx.getStack();
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, HoudiniBlockItem.DEFAULT_NBT).copyNbt();
        HoudiniBlockItem.PlacementMode mode = HoudiniBlockItem.PlacementMode.valueOf(nbt.getString("houdini_placement_mode"));

        return this.getDefaultState()
            .with(PREVENT_ON_PLACE, mode == HoudiniBlockItem.PlacementMode.PREVENT_ON_PLACE)
            .with(PREVENT_ON_BREAK, mode == HoudiniBlockItem.PlacementMode.PREVENT_ON_BREAK)
            .with(PREVENT_ALL, mode == HoudiniBlockItem.PlacementMode.PREVENT_ALL)
            .with(REPLACE_BLOCK, mode == HoudiniBlockItem.PlacementMode.REPLACE_BLOCK)
            .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(REPLACE_BLOCK)) {
            return this.replaceWithBlockInHand(stack, state, world, pos, player, hand, hit);
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemActionResult replaceWithBlockInHand(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        try {
            if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                this.spawnBreakParticles(world, player, pos, state);
                this.spawnHoudiniBlockItem(world, player, pos);

                ItemPlacementContext placementContext = new ItemPlacementContext(player, hand, stack, hit);
                placementContext.placementPos = pos;

                world.setBlockState(pos, block.getPlacementState(placementContext));

                if (!player.isCreative()) {
                    stack.decrement(1);
                }

                return ItemActionResult.SUCCESS;
            }
        } catch (RuntimeException e) {
            HoudiniBlockMod.LOGGER.error("Error in HoudiniBlock$replaceWithBlockInHand", e);
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        this.spawnBreakParticles(world, player, pos, state);
        this.spawnHoudiniBlockItem(world, player, pos);

        if (state.get(WATERLOGGED)) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
        }

        return state;
    }

    private void spawnHoudiniBlockItem(World world, PlayerEntity player, BlockPos pos) {
        if (!player.isCreative()) {
            ItemEntity itemEntity = new ItemEntity(
                world,
                (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D,
                (double) pos.getZ() + 0.5D,
                HOUDINI_BLOCK_ITEM.get().getDefaultStack()
            );

            itemEntity.setToDefaultPickupDelay();

            world.spawnEntity(itemEntity);
        }
    }
}
