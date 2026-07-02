package com.chimericdream.minekea.entity.block.furniture;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.block.furniture.armoires.ArmoireBlock;
import com.chimericdream.minekea.block.furniture.armoires.Armoires;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.world.entity.Entity;
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

public class ArmoireBlockEntity extends BlockEntity implements ImplementedInventory, WorldlyContainer {
    private final BlockState cachedBlockState;
    private final NonNullList<ItemStack> items = NonNullList.withSize(16, ItemStack.EMPTY);
    private final List<ArmorStand> armorStandEntities = new ArrayList<>();

    public ArmoireBlockEntity(BlockPos pos, BlockState state) {
        this(Armoires.ARMOIRE_BLOCK_ENTITY.get(), pos, state);
    }

    public ArmoireBlockEntity(BlockEntityType<? extends ArmoireBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        cachedBlockState = state;
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);

        if (world instanceof ServerLevel && armorStandEntities.isEmpty()) {
            initializeArmorStands(cachedBlockState);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        destroyArmorStands();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (armorStandEntities.isEmpty()) {
            initializeArmorStands(cachedBlockState);
        }
    }

    private void updateArmorStands() {
        destroyArmorStands();
        initializeArmorStands(cachedBlockState);
    }

    public void initializeArmorStands(BlockState armoireState) {
        if (this.level == null) {
            return;
        }

        Triplet<Double, Double, Double> stand1Pos = getArmorStandPosition(0, armoireState);
        Triplet<Double, Double, Double> stand2Pos = getArmorStandPosition(1, armoireState);
        Triplet<Double, Double, Double> stand3Pos = getArmorStandPosition(2, armoireState);
        Triplet<Double, Double, Double> stand4Pos = getArmorStandPosition(3, armoireState);

        armorStandEntities.add(new ArmorStand(this.level, stand1Pos.getA(), stand1Pos.getB(), stand1Pos.getC()));
        armorStandEntities.add(new ArmorStand(this.level, stand2Pos.getA(), stand2Pos.getB(), stand2Pos.getC()));
        armorStandEntities.add(new ArmorStand(this.level, stand3Pos.getA(), stand3Pos.getB(), stand3Pos.getC()));
        armorStandEntities.add(new ArmorStand(this.level, stand4Pos.getA(), stand4Pos.getB(), stand4Pos.getC()));

        float yaw = getArmorStandYaw(armoireState);

        armorStandEntities.forEach(entity -> {
            entity.setInvisible(true);
            entity.setNoGravity(true);
            entity.setSmall(true);
            entity.setMarker(true);
            entity.setYRot(yaw);

            // Disable all in-world interactions
            entity.disabledSlots = 16191;

            level.addFreshEntity(entity);
        });

        for (int i = 0; i < items.size(); i++) {
            if (i % 4 > 1) {
                continue;
            }

            EquipmentSlot itemSlot = i % 4 == 0 ? EquipmentSlot.CHEST : EquipmentSlot.LEGS;
            int armorStand = Math.floorDiv(i, 4);

            if (!items.get(i).isEmpty()) {
                armorStandEntities.get(armorStand).setItemSlot(itemSlot, items.get(i));
            }
        }
    }

    private float getArmorStandYaw(BlockState armoireState) {
        return switch (armoireState.getValue(ArmoireBlock.FACING)) {
            case NORTH -> -90.0f;
            case SOUTH -> 90.0f;
            case EAST -> 0.0f;
            case WEST -> -180.0f;
            default -> 0.0f;
        };
    }

    private Triplet<Double, Double, Double> getArmorStandPosition(int stand, BlockState armoireState) {
        double xOffset = switch (armoireState.getValue(ArmoireBlock.FACING)) {
            case NORTH -> 0.1875 + (0.21875 * stand);
            case SOUTH -> 0.8125 - (0.21875 * stand);
            case EAST -> 0.59375;
            case WEST -> 0.40625;
            default -> 0.0;
        };

        double zOffset = switch (armoireState.getValue(ArmoireBlock.FACING)) {
            case NORTH -> 0.40625;
            case SOUTH -> 0.59375;
            case EAST -> 0.1875 + (0.21875 * stand);
            case WEST -> 0.8125 - (0.21875 * stand);
            default -> 0.0;
        };

        return new Triplet<>(
            this.worldPosition.getX() + xOffset,
            this.worldPosition.getY() + 0.78125,
            this.worldPosition.getZ() + zOffset
        );
    }

    public void destroyArmorStands() {
        armorStandEntities.forEach(entity -> {
            entity.remove(Entity.RemovalReason.DISCARDED);
        });

        armorStandEntities.clear();
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

        if (this.level != null && this.level instanceof ServerLevel) {
            updateArmorStands();
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
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack items = ContainerHelper.takeItem(getItems(), slot);

        if (!items.isEmpty()) {
            handleSuccessfulRemoval(slot);
        }

        return items;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, count);

        if (!result.isEmpty()) {
            handleSuccessfulRemoval(slot);
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
            handleSuccessfulInsert(slot, toInsert.copy());
        }

        return ret;
    }

    private void handleSuccessfulInsert(int slot, ItemStack inserted) {
        this.playAddItemSound(slot % 4);

        // Helmets and boots are rendered normally, so they shouldn't get equipped onto the hidden armor stands
        if (slot % 4 >= 2) {
            return;
        }

        int armorStandIndex = Math.floorDiv(slot, 4);
        EquipmentSlot itemSlot = slot % 4 == 0 ? EquipmentSlot.CHEST : EquipmentSlot.LEGS;

        if (armorStandIndex >= armorStandEntities.size()) {
            return;
        }

        armorStandEntities.get(armorStandIndex).setItemSlot(itemSlot, inserted);

        MinekeaMod.LOGGER.info("inserting into slot: {}, armor stand: {}", slot, armorStandIndex);
    }

    private void handleSuccessfulRemoval(int slot) {
        setChanged();

        if (slot % 4 >= 2) {
            return;
        }

        int armorStandIndex = Math.floorDiv(slot, 4);
        EquipmentSlot itemSlot = slot % 4 == 0 ? EquipmentSlot.CHEST : EquipmentSlot.LEGS;

        if (armorStandIndex >= armorStandEntities.size()) {
            return;
        }

        armorStandEntities.get(armorStandIndex).setItemSlot(itemSlot, ItemStack.EMPTY);
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
