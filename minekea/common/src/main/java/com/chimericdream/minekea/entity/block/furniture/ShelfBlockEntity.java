package com.chimericdream.minekea.entity.block.furniture;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.block.furniture.shelves.Shelves;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ShelfBlockEntity extends BlockEntity implements ImplementedInventory, WorldlyContainer {
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    public ShelfBlockEntity(BlockPos pos, BlockState state) {
        this(Shelves.SHELF_BLOCK_ENTITY.get(), pos, state);
    }

    public ShelfBlockEntity(BlockEntityType<? extends ShelfBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        ContainerHelper.loadAllItems(view, this.items);
        if (this.items.getFirst().is(Blocks.BARRIER.asItem())) {
            this.items.set(0, ItemStack.EMPTY);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public int[] getSlotsForFace(Direction var1) {
        int[] result = new int[getItems().size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }

        return result;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public ItemStack tryInsert(int slot, ItemStack stack) {
        ItemStack ret = ImplementedInventory.super.tryInsert(slot, stack);

        if (!ItemStack.matches(ret, stack)) {
            this.playAddItemSound();
        }

        return ret;
    }

    @Override
    public void setChanged() {
        if (this.level != null) {
            markDirtyInWorld(this.level, this.worldPosition, this.getBlockState());
        }
    }

    protected void markDirtyInWorld(Level world, BlockPos pos, BlockState state) {
        world.blockEntityChanged(pos);

        if (!world.isClientSide()) {
            ((ServerLevel) world).getChunkSource().blockChanged(pos); // Mark changes to be synced to the client.
        }
    }

    public void playAddItemSound() {
        playSound(SoundEvents.ITEM_FRAME_ADD_ITEM);
    }

    public void playSound(SoundEvent soundEvent) {
        if (this.level == null) {
            return;
        }

        this.level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
