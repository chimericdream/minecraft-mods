package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.block.XtremeMultiHopperBlock;
import com.chimericdream.hopperxtreme.client.screen.FilteredHopperScreenHandler;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static com.chimericdream.hopperxtreme.block.ModBlocks.FILTERED_HOPPER_SCREEN_HANDLER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.XTREME_MULTI_HOPPER_BLOCK_ENTITY;

public class XtremeMultiHopperBlockEntity extends AbstractDownMultiXtremeHopperBlockEntity {
    public XtremeMultiHopperBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getCooldownForBlock(state.getBlock()));
    }

    public XtremeMultiHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks) {
        this(pos, state, cooldownInTicks, false);
    }

    public XtremeMultiHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(XTREME_MULTI_HOPPER_BLOCK_ENTITY.get(), pos, state, cooldownInTicks, withFilter);
    }

    private static int getCooldownForBlock(Block block) {
        return block instanceof XtremeMultiHopperBlock hopper ? hopper.getCooldownInTicks() : 8;
    }

    @Override
    protected int storageSlotCount() {
        return 5;
    }

    @Override
    protected boolean pushOutput(Level world, BlockPos pos) {
        return this.insertOutput(world, pos);
    }

    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        if (this.withFilter) {
            return new FilteredHopperScreenHandler(FILTERED_HOPPER_SCREEN_HANDLER.get(), syncId, playerInventory, this);
        }

        return new HopperMenu(syncId, playerInventory, this);
    }
}
