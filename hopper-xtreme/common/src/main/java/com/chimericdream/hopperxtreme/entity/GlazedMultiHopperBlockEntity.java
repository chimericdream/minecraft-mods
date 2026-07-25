package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.block.GlazedMultiHopperBlock;
import com.chimericdream.hopperxtreme.client.screen.FilteredGlazedHopperScreenHandler;
import com.chimericdream.hopperxtreme.client.screen.GlazedHopperScreenHandler;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static com.chimericdream.hopperxtreme.block.ModBlocks.FILTERED_GLAZED_HOPPER_SCREEN_HANDLER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_MULTI_HOPPER_BLOCK_ENTITY;
import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_HOPPER_SCREEN_HANDLER;

public class GlazedMultiHopperBlockEntity extends AbstractDownMultiXtremeHopperBlockEntity {
    public GlazedMultiHopperBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getCooldownForBlock(state.getBlock()));
    }

    public GlazedMultiHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks) {
        this(pos, state, cooldownInTicks, false);
    }

    public GlazedMultiHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(GLAZED_MULTI_HOPPER_BLOCK_ENTITY.get(), pos, state, cooldownInTicks, withFilter);
    }

    private static int getCooldownForBlock(Block block) {
        return block instanceof GlazedMultiHopperBlock hopper ? hopper.getCooldownInTicks() : 8;
    }

    @Override
    protected int storageSlotCount() {
        return 1;
    }

    @Override
    protected boolean pushOutput(Level world, BlockPos pos) {
        return this.dropOutput(world, pos);
    }

    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        if (this.withFilter) {
            return new FilteredGlazedHopperScreenHandler(FILTERED_GLAZED_HOPPER_SCREEN_HANDLER.get(), syncId, playerInventory, this);
        }

        return new GlazedHopperScreenHandler(GLAZED_HOPPER_SCREEN_HANDLER.get(), syncId, playerInventory, this);
    }
}
