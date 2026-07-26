package com.chimericdream.minekea.entity.block.furniture;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.block.furniture.armoires.Armoires;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class ArmoireBlockEntity extends BlockEntity implements ImplementedInventory, WorldlyContainer {
    /*
     * Older versions of the armoire displayed chestplates and leggings by equipping invisible,
     * small, marker armor stands with disabledSlots set to this value. Armor is rendered directly
     * by the block entity renderer now, but worlds saved with those versions may still contain the
     * stands, so each armoire checks for (and discards) them once after it loads.
     */
    private static final int LEGACY_STAND_DISABLED_SLOTS = 16191;
    private boolean checkedForLegacyArmorStands = false;

    private final NonNullList<ItemStack> items = NonNullList.withSize(16, ItemStack.EMPTY);

    public ArmoireBlockEntity(BlockPos pos, BlockState state) {
        this(Armoires.ARMOIRE_BLOCK_ENTITY.get(), pos, state);
    }

    public ArmoireBlockEntity(BlockEntityType<? extends ArmoireBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void cleanUpLegacyArmorStands(Level world, BlockPos pos, BlockState state, ArmoireBlockEntity entity) {
        if (entity.checkedForLegacyArmorStands || !(world instanceof ServerLevel serverWorld)) {
            return;
        }

        // Entities load separately from block entities; wait until this position ticks entities
        if (!serverWorld.isPositionEntityTicking(pos)) {
            return;
        }

        entity.checkedForLegacyArmorStands = true;

        AABB armoireBox = AABB.encapsulatingFullBlocks(pos, pos.above());
        for (ArmorStand stand : serverWorld.getEntitiesOfClass(ArmorStand.class, armoireBox)) {
            boolean isLegacyStand = stand.isMarker()
                && stand.isSmall()
                && stand.isInvisible()
                && stand.isNoGravity()
                && stand.disabledSlots == LEGACY_STAND_DISABLED_SLOTS;

            if (isLegacyStand) {
                // The stand's equipment mirrored this armoire's inventory; discarding it (rather
                // than destroying it) ensures the items aren't duplicated as drops
                MinekeaMod.LOGGER.info("Removing legacy armor stand from the armoire at {}", pos);
                stand.discard();
            }
        }
    }

    public boolean hasItem(int slot) {
        return !items.get(slot).isEmpty();
    }

    public boolean canAccept(int slot, ItemStack item) {
        Equippable component = item.get(DataComponents.EQUIPPABLE);

        if (hasItem(slot) || component == null) {
            return false;
        }

        int slotType = slot % 4; // 0, 1, 2, 3 -> chestplate, leggings, helmet, boots

        return switch (slotType) {
            case 0 -> component.slot() == EquipmentSlot.CHEST;
            case 1 -> component.slot() == EquipmentSlot.LEGS;
            case 2 -> component.slot() == EquipmentSlot.HEAD;
            case 3 -> component.slot() == EquipmentSlot.FEET;
            default -> false;
        };
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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction var1) {
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
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack items = ContainerHelper.takeItem(getItems(), slot);

        if (!items.isEmpty()) {
            setChanged();
        }

        return items;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, count);

        if (!result.isEmpty()) {
            setChanged();
        }

        return result;
    }

    @Override
    public ItemStack tryInsert(int slot, ItemStack stack) {
        if (!canAccept(slot, stack)) {
            return stack;
        }

        ItemStack ret = stack.copy();
        ItemStack toInsert = stack.copy();
        toInsert.setCount(1);

        setItem(slot, toInsert);

        ret.shrink(1);

        if (!ItemStack.matches(ret, stack)) {
            this.playAddItemSound(slot % 4);
        }

        return ret;
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

    public void playAddItemSound(int slot) {
        switch (slot) {
            case 0, 1 -> playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value());
            default -> playSound(SoundEvents.ITEM_FRAME_ADD_ITEM);
        }
    }

    public void playSound(SoundEvent soundEvent) {
        if (this.level == null) {
            return;
        }

        this.level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
