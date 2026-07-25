package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.block.XtremeMultiHupperBlock;
import com.chimericdream.hopperxtreme.client.screen.FilteredHopperScreenHandler;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

import static com.chimericdream.hopperxtreme.block.ModBlocks.FILTERED_HOPPER_SCREEN_HANDLER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.XTREME_MULTI_HUPPER_BLOCK_ENTITY;

public class XtremeMultiHupperBlockEntity extends AbstractMultiXtremeHopperBlockEntity {
    private static final AABB SUCK_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).toAabbs().getFirst();

    public XtremeMultiHupperBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getCooldownForBlock(state.getBlock()));
    }

    public XtremeMultiHupperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks) {
        this(pos, state, cooldownInTicks, false);
    }

    public XtremeMultiHupperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(XTREME_MULTI_HUPPER_BLOCK_ENTITY.get(), pos, state, cooldownInTicks, withFilter);
    }

    private static int getCooldownForBlock(Block block) {
        return block instanceof XtremeMultiHupperBlock hupper ? hupper.getCooldownInTicks() : 8;
    }

    @Override
    public @NotNull AABB getSuckAabb() {
        return SUCK_AABB;
    }

    @Override
    protected int storageSlotCount() {
        return 5;
    }

    @Override
    protected Direction extractSide() {
        return Direction.UP;
    }

    @Override
    protected double inputBlockYOffset() {
        return 0.0;
    }

    @Override
    protected double levelYOffset() {
        return -0.5;
    }

    @Override
    protected BooleanProperty northConnectedProperty() {
        return XtremeMultiHupperBlock.NORTH_CONNECTED;
    }

    @Override
    protected BooleanProperty southConnectedProperty() {
        return XtremeMultiHupperBlock.SOUTH_CONNECTED;
    }

    @Override
    protected BooleanProperty eastConnectedProperty() {
        return XtremeMultiHupperBlock.EAST_CONNECTED;
    }

    @Override
    protected BooleanProperty westConnectedProperty() {
        return XtremeMultiHupperBlock.WEST_CONNECTED;
    }

    @Override
    protected BooleanProperty verticalConnectedProperty() {
        return XtremeMultiHupperBlock.UP_CONNECTED;
    }

    @Override
    protected Direction verticalDirection() {
        return Direction.UP;
    }

    @Override
    protected boolean pushOutput(Level world, BlockPos pos) {
        return this.insertOutput(world, pos);
    }

    // Multi-hupper carries an extra source-slot gate that the other five variants don't: a
    // non-filtered multi-hupper only pulls the filter item out of source slot 5 (and everything
    // else out of the other slots). Preserved verbatim from the pre-extraction copy.
    @Override
    protected boolean passesExtractFilter(ItemStack stack, int slot) {
        if (this.withFilter) {
            return HopperItemFilterItem.matchesFilter(this.getItem(this.getContainerSize()), stack);
        }

        boolean isFilter = ItemStack.isSameItem(stack, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));

        return isFilter == (slot == 5);
    }

    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        if (this.withFilter) {
            return new FilteredHopperScreenHandler(FILTERED_HOPPER_SCREEN_HANDLER.get(), syncId, playerInventory, this);
        }

        return new HopperMenu(syncId, playerInventory, this);
    }
}
