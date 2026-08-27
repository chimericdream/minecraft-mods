package com.chimericdream.hopperxtreme.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * A hopper that round-robins its output across whichever of its four horizontal sides plus one
 * vertical side ({@code DOWN} for a multi-hopper, {@code UP} for a multi-hupper) are toggled
 * connected. {@link #getNextDirection()} advances a round-robin cursor that is persisted to NBT
 * (so it survives unload/reload); the concrete variant supplies the connection
 * {@link BooleanProperty properties} and the vertical direction.
 */
public abstract class AbstractMultiXtremeHopperBlockEntity extends AbstractXtremeHopperBlockEntity {
    private static final String LAST_DIRECTION_KEY = "LastDirection";

    private Direction lastDirection;
    private boolean northConnected;
    private boolean southConnected;
    private boolean eastConnected;
    private boolean westConnected;
    private boolean verticalConnected;

    protected AbstractMultiXtremeHopperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(type, pos, state, cooldownInTicks, withFilter);

        this.lastDirection = verticalDirection();

        this.northConnected = state.getValue(northConnectedProperty());
        this.southConnected = state.getValue(southConnectedProperty());
        this.eastConnected = state.getValue(eastConnectedProperty());
        this.westConnected = state.getValue(westConnectedProperty());
        this.verticalConnected = state.getValue(verticalConnectedProperty());
    }

    // --- connection wiring (supplied per variant) ----------------------------------------------

    protected abstract BooleanProperty northConnectedProperty();

    protected abstract BooleanProperty southConnectedProperty();

    protected abstract BooleanProperty eastConnectedProperty();

    protected abstract BooleanProperty westConnectedProperty();

    protected abstract BooleanProperty verticalConnectedProperty();

    /** The single vertical side this variant can connect: DOWN (multi-hopper) or UP (multi-hupper). */
    protected abstract Direction verticalDirection();

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);

        this.northConnected = state.getValue(northConnectedProperty());
        this.southConnected = state.getValue(southConnectedProperty());
        this.eastConnected = state.getValue(eastConnectedProperty());
        this.westConnected = state.getValue(westConnectedProperty());
        this.verticalConnected = state.getValue(verticalConnectedProperty());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.lastDirection = view.read(LAST_DIRECTION_KEY, Direction.CODEC).orElse(verticalDirection());
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.store(LAST_DIRECTION_KEY, Direction.CODEC, this.lastDirection);
    }

    /**
     * Cycles to the next connected output side (N → S → E → W → vertical → wrap), remembering where
     * it left off so a full inventory spreads evenly across the connected containers. Returns
     * {@code null} when nothing is connected.
     */
    @Nullable
    public Direction getNextDirection() {
        Direction vertical = verticalDirection();

        switch (lastDirection) {
            case NORTH:
                if (this.southConnected) {
                    lastDirection = Direction.SOUTH;
                    return lastDirection;
                }

                // deliberately fall through
            case SOUTH:
                if (this.eastConnected) {
                    lastDirection = Direction.EAST;
                    return lastDirection;
                }

                // deliberately fall through
            case EAST:
                if (this.westConnected) {
                    lastDirection = Direction.WEST;
                    return lastDirection;
                }

                // deliberately fall through
            case WEST:
                if (this.verticalConnected) {
                    lastDirection = vertical;
                    return lastDirection;
                }

                // deliberately fall through
            default:
                if (this.northConnected) {
                    lastDirection = Direction.NORTH;
                    return lastDirection;
                }
                if (this.southConnected) {
                    lastDirection = Direction.SOUTH;
                    return lastDirection;
                }
                if (this.eastConnected) {
                    lastDirection = Direction.EAST;
                    return lastDirection;
                }
                if (this.westConnected) {
                    lastDirection = Direction.WEST;
                    return lastDirection;
                }
                if (this.verticalConnected) {
                    lastDirection = vertical;
                    return lastDirection;
                }
        }

        return null;
    }

    /** Insert into the connected containers, round-robining a fresh side per non-empty slot. */
    protected boolean insertOutput(Level world, BlockPos pos) {
        int itemsPerTick = this.getItemsPerTick();

        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);

            if (!itemStack.isEmpty()) {
                Direction nextFacing = this.getNextDirection();
                if (nextFacing == null) {
                    return false;
                }

                Container inventory = getOutputInventoryAt(world, pos.relative(nextFacing));
                if (inventory == null) {
                    continue;
                }

                Direction direction = nextFacing.getOpposite();
                if (isInventoryFull(inventory, direction)) {
                    continue;
                }

                int transferCount = Math.min(itemsPerTick, itemStack.getCount());
                ItemStack leftover = transfer(this, inventory, this.removeItem(i, transferCount), direction);

                returnToSlot(this, i, leftover);

                if (leftover.getCount() != transferCount) {
                    inventory.setChanged();
                    return true;
                }
            }
        }

        return false;
    }

    /** Drop up to this tier's items-per-tick toward the next connected side whose face isn't sturdy. */
    protected boolean dropOutput(Level world, BlockPos pos) {
        ItemStack stack = this.getItem(0);

        if (stack.isEmpty()) {
            return false;
        }

        Direction nextFacing = this.getNextDirection();
        if (nextFacing == null) {
            return false;
        }

        BlockState blockState = world.getBlockState(pos.relative(nextFacing));
        if (blockState.isFaceSturdy(world, pos, nextFacing)) {
            return false;
        }

        int dropCount = Math.min(this.getItemsPerTick(), stack.getCount());
        ItemStack stack2 = stack.copy();
        stack2.setCount(dropCount);
        stack.shrink(dropCount);

        this.setItem(0, stack);

        drop(world, stack2, pos, nextFacing, this.cooldownInTicks);

        return true;
    }
}
