package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.block.AbstractHopperBlock;
import com.chimericdream.hopperxtreme.block.GlazedHopperBlock;
import com.chimericdream.hopperxtreme.client.screen.FilteredGlazedHopperScreenHandler;
import com.chimericdream.hopperxtreme.client.screen.GlazedHopperScreenHandler;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import static com.chimericdream.hopperxtreme.block.ModBlocks.FILTERED_GLAZED_HOPPER_SCREEN_HANDLER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_HOPPER_BLOCK_ENTITY;
import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_HOPPER_SCREEN_HANDLER;

public class GlazedHopperBlockEntity extends AbstractSingleFacingXtremeHopperBlockEntity {
    public GlazedHopperBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getCooldownForBlock(state.getBlock()));
    }

    public GlazedHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks) {
        this(pos, state, cooldownInTicks, false);
    }

    public GlazedHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(GLAZED_HOPPER_BLOCK_ENTITY.get(), pos, state, cooldownInTicks, withFilter);
    }

    private static int getCooldownForBlock(Block block) {
        return block instanceof GlazedHopperBlock hopper ? hopper.getCooldownInTicks() : 8;
    }

    @Override
    protected int storageSlotCount() {
        return 1;
    }

    @Override
    protected Direction extractSide() {
        return Direction.DOWN;
    }

    @Override
    protected double inputBlockYOffset() {
        return 1.0;
    }

    @Override
    protected double levelYOffset() {
        return 0.5;
    }

    @Override
    protected EnumProperty<Direction> facingProperty() {
        return AbstractHopperBlock.FACING;
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
