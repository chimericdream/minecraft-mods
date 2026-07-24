package com.chimericdream.shulkerstuff.block.entity;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.*;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.shulkerstuff.block.ModBlocks.DYE_STATION_BLOCK_ENTITY;

public class DyeStationBlockEntity extends BaseContainerBlockEntity implements MenuProvider, ImplementedInventory {
    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/entity/dye_station");

    public static final int INVENTORY_SIZE = 7;

    private final NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;

    public DyeStationBlockEntity(BlockPos pos, BlockState state) {
        super(DYE_STATION_BLOCK_ENTITY.get(), pos, state);

        this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        this.stateManager = new ContainerOpenersCounter() {
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
            }

            protected void onClose(Level world, BlockPos pos, BlockState state) {
            }

            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                DyeStationBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
            }

            public boolean isOwnContainer(Player player) {
                // The dye station opens a DyeStationScreenHandler, never a ChestMenu — testing for
                // the latter (copy-paste from the barrel) meant recheckOpeners never saw a single
                // legitimate viewer.
                return player.containerMenu instanceof DyeStationScreenHandler handler
                    && handler.getInventory() == DyeStationBlockEntity.this;
            }
        };
    }

    public static int getPlayersLookingInStationCount(BlockGetter world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DyeStationBlockEntity) {
                return ((DyeStationBlockEntity) blockEntity).stateManager.getOpenerCount();
            }
        }

        return 0;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        // `this.inventory` is a fixed-size NonNullList.withSize; clear()+addAll would either throw or
        // let its size drift away from INVENTORY_SIZE. Copy in place, padding/truncating to our size.
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new DyeStationScreenHandler(ModBlocks.DYE_STATION_SCREEN_HANDLER.get(), syncId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDefaultName() {
        return Component.translatable(DyeStationScreenHandler.SCREEN_ID.toLanguageKey());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        ContainerHelper.loadAllItems(view, inventory);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        ContainerHelper.saveAllItems(view, inventory);

        super.saveAdditional(view);
    }

    @Override
    public void startOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.stateManager.incrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState(), user.getContainerInteractionRange());
        }
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.stateManager.decrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, DyeStationBlockEntity entity) {
        if (!entity.remove) {
            entity.stateManager.recheckOpeners(world, pos, state);
        }
    }

    protected void onViewerCountUpdate(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.blockEvent(pos, block, 1, newViewerCount);
    }
}
