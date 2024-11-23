package com.chimericdream.shulkerstuff.block.entity;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static com.chimericdream.shulkerstuff.block.ModBlocks.DYE_STATION_BLOCK_ENTITY;

public class DyeStationBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    public static final Identifier ENTITY_ID = Identifier.of(ModInfo.MOD_ID, "block/entity/dye_station");

    public static final int INVENTORY_SIZE = 7;

    private final DefaultedList<ItemStack> inventory;
    private final ViewerCountManager stateManager;

    public DyeStationBlockEntity(BlockPos pos, BlockState state) {
        super(DYE_STATION_BLOCK_ENTITY.get(), pos, state);

        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

        this.stateManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                DyeStationBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                    Inventory inventory = ((GenericContainerScreenHandler) player.currentScreenHandler).getInventory();
                    return inventory == DyeStationBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    public static int getPlayersLookingInStationCount(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DyeStationBlockEntity) {
                return ((DyeStationBlockEntity) blockEntity).stateManager.getViewerCount();
            }
        }

        return 0;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new DyeStationScreenHandler(ModBlocks.DYE_STATION_SCREEN_HANDLER.get(), syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(DyeStationScreenHandler.SCREEN_ID.toTranslationKey());
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        Inventories.readNbt(nbt, inventory, registryLookup);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, inventory, registryLookup);

        super.writeNbt(nbt, registryLookup);
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, DyeStationBlockEntity entity) {
        if (!entity.removed) {
            entity.stateManager.updateViewerCount(world, pos, state);
        }
    }

    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
    }
}
