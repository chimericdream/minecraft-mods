package com.chimericdream.minekea.entity.block.furniture;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.block.furniture.displaycases.DisplayCases;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.minekea.block.furniture.displaycases.DisplayCaseBlock.ROTATION;

public class DisplayCaseBlockEntity extends BlockEntity implements ImplementedInventory, WorldlyContainer {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
        this(DisplayCases.DISPLAY_CASE_BLOCK_ENTITY.get(), pos, state);
    }

    public DisplayCaseBlockEntity(BlockEntityType<? extends DisplayCaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getRotation() {
        return this.getBlockState().getValue(ROTATION);
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

    @Override
    public void setChanged() {
        if (this.level == null) {
            return;
        }

        markDirtyInWorld(this.level, this.worldPosition, this.getBlockState());
    }

    protected void markDirtyInWorld(Level world, BlockPos pos, BlockState state) {
        world.blockEntityChanged(pos);

        if (!world.isClientSide()) {
            ((ServerLevel) world).getChunkSource().blockChanged(pos); // Mark changes to be synced to the client.
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction var1) {
        return new int[]{};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return false;
    }

    public void playRemoveItemSound() {
        playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
    }

    public void playRotateItemSound() {
        playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM);
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
