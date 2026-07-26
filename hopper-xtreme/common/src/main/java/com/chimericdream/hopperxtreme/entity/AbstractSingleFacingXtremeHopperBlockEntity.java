package com.chimericdream.hopperxtreme.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * A hopper that outputs toward a single fixed {@code FACING} direction (the plain Xtreme hopper /
 * hupper and the glazed dropper). Supplies both output strategies — {@link #insertOutput} into the
 * facing container and {@link #dropOutput} in front — with the concrete variant selecting one via
 * {@link #pushOutput(Level, BlockPos)}.
 */
public abstract class AbstractSingleFacingXtremeHopperBlockEntity extends AbstractXtremeHopperBlockEntity {
    protected Direction facing;

    protected AbstractSingleFacingXtremeHopperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(type, pos, state, cooldownInTicks, withFilter);
        this.facing = state.getValue(facingProperty());
    }

    /** The block's FACING property (the value set differs between hopper and hupper). */
    protected abstract EnumProperty<Direction> facingProperty();

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        this.facing = state.getValue(facingProperty());
    }

    /** Insert into the container at {@link #facing}. */
    protected boolean insertOutput(Level world, BlockPos pos) {
        Container inventory = getOutputInventoryAt(world, pos.relative(this.facing));
        if (inventory == null) {
            return false;
        }

        Direction direction = this.facing.getOpposite();
        if (isInventoryFull(inventory, direction)) {
            return false;
        }

        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);

            if (!itemStack.isEmpty()) {
                int j = itemStack.getCount();
                ItemStack itemStack2 = transfer(this, inventory, this.removeItem(i, 1), direction);

                if (itemStack2.isEmpty()) {
                    inventory.setChanged();
                    return true;
                }

                itemStack.setCount(j);

                if (j == 1) {
                    this.setItem(i, itemStack);
                }
            }
        }

        return false;
    }

    /** Drop a single item in front of {@link #facing} when that face isn't sturdy. */
    protected boolean dropOutput(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.relative(this.facing));
        if (blockState.isFaceSturdy(world, pos, this.facing)) {
            return false;
        }

        ItemStack stack = this.getItem(0);
        if (stack.isEmpty()) {
            return false;
        }

        ItemStack stack2 = stack.copy();
        stack2.setCount(1);
        stack.shrink(1);

        this.setItem(0, stack);

        drop(world, stack2, pos, this.facing, this.cooldownInTicks);

        return true;
    }
}
