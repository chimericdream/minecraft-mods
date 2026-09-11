package com.chimericdream.allhallowssteve.block.entity;

import com.chimericdream.lib.inventories.ContainerOpenersCounters;
import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.*;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.allhallowssteve.block.ModBlocks.CARVING_STATION_BLOCK_ENTITY;

public class CarvingStationBlockEntity extends BaseContainerBlockEntity implements MenuProvider, ImplementedInventory {
    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/entity/carving_station");

    public static final int INVENTORY_SIZE = 8;

    private final NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;

    public CarvingStationBlockEntity(BlockPos pos, BlockState state) {
        super(CARVING_STATION_BLOCK_ENTITY.get(), pos, state);

        this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        this.stateManager = ContainerOpenersCounters.create(
            this,
            CarvingStationScreenHandler.class,
            menu -> ((CarvingStationScreenHandler) menu).getInventory(),
            null,
            null
        );
    }

    public static int getPlayersLookingInStationCount(BlockGetter world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CarvingStationBlockEntity) {
                return ((CarvingStationBlockEntity) blockEntity).stateManager.getOpenerCount();
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
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new CarvingStationScreenHandler(ModBlocks.CARVING_STATION_SCREEN_HANDLER.get(), syncId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDefaultName() {
        return Component.translatable(CarvingStationScreenHandler.SCREEN_ID.toLanguageKey());
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

    public static void tick(Level world, BlockPos pos, BlockState state, CarvingStationBlockEntity entity) {
        if (!entity.remove) {
            entity.stateManager.recheckOpeners(world, pos, state);
        }
    }
}
