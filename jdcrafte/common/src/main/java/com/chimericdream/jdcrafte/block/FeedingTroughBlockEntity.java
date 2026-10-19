package com.chimericdream.jdcrafte.block;

import com.chimericdream.jdcrafte.config.JDCrafteConfig;
import com.chimericdream.lib.inventories.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds up to three stacks of a single accepted feed item. The feeding trough's blockstate
 * ({@code level} + {@code food}) is kept in sync with the inventory contents so the correct
 * per-level, per-food model is displayed.
 */
public class FeedingTroughBlockEntity extends BlockEntity implements ImplementedInventory, WorldlyContainer {
    public static final int SLOT_COUNT = 3;

    private static final Set<Item> VALID_FOOD_ITEMS = Set.of(
        Items.WHEAT,
        Items.BEETROOT,
        Items.CARROT,
        Items.POTATO,
        Items.WHEAT_SEEDS,
        Items.GOLDEN_CARROT,
        Items.GOLDEN_APPLE
    );

    private static final Set<EntityType<?>> BREEDABLE_SPECIES = Set.of(
        EntityTypes.COW,
        EntityTypes.SHEEP,
        EntityTypes.PIG,
        EntityTypes.CHICKEN,
        EntityTypes.RABBIT,
        EntityTypes.HORSE
    );

    private static final int LOVE_MODE_TICKS = 600;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public FeedingTroughBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlocks.FEEDING_TROUGH_BLOCK_ENTITY.get(), pos, state);
    }

    public FeedingTroughBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static boolean isValidFood(ItemStack stack) {
        return VALID_FOOD_ITEMS.contains(stack.getItem());
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public ItemStack getStoredFood() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public int getStoredCount() {
        int total = 0;
        for (ItemStack stack : items) {
            total += stack.getCount();
        }

        return total;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (!isValidFood(stack)) {
            return false;
        }

        ItemStack stored = getStoredFood();

        return stored.isEmpty() || ItemStack.isSameItemSameComponents(stored, stack);
    }

    /**
     * Removes and returns the first non-empty slot's entire stack, or {@link ItemStack#EMPTY} if the
     * trough has nothing stored.
     */
    public ItemStack extractStack() {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (!getItem(slot).isEmpty()) {
                return removeItemNoUpdate(slot);
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Inserts as much of the given stack as the trough's remaining capacity and single-food-type
     * restriction allow, spread across whichever slots have room, and returns whatever didn't fit.
     */
    public ItemStack tryInsert(ItemStack stack) {
        if (stack.isEmpty() || !canPlaceItem(0, stack)) {
            return stack;
        }

        ItemStack remainder = stack;
        for (int slot = 0; slot < SLOT_COUNT && !remainder.isEmpty(); slot++) {
            remainder = ImplementedInventory.super.tryInsert(slot, remainder);
        }

        return remainder;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FeedingTroughBlockEntity trough) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        JDCrafteConfig config = JDCrafteConfig.CONFIG.instance();
        config.clamp();

        if (!config.troughFeedingEnabled || trough.getStoredCount() <= 0) {
            return;
        }

        // Offset by position so a field of troughs doesn't all scan on the same tick.
        long interval = config.troughIntervalSeconds * 20L;
        if (Math.floorMod(level.getGameTime() + pos.asLong(), interval) != 0) {
            return;
        }

        trough.feedOnePair(serverLevel, pos, config);
    }

    /**
     * Puts one pair of same-species animals in love mode, if the stored food suits two eligible
     * animals and the area isn't overcrowded. Costs one food item per animal.
     */
    public void feedOnePair(ServerLevel level, BlockPos pos, JDCrafteConfig config) {
        if (getStoredCount() < 2) {
            return;
        }

        ItemStack food = getStoredFood();

        int h = config.troughHorizontalRadius;
        int v = config.troughVerticalRadius;
        AABB area = new AABB(pos).inflate(h, v, h);

        List<Animal> nearby = level.getEntitiesOfClass(Animal.class, area, animal -> BREEDABLE_SPECIES.contains(animal.getType()));
        if (nearby.size() >= config.troughAnimalCap) {
            return;
        }

        Map<EntityType<?>, List<Animal>> eligibleBySpecies = new HashMap<>();
        for (Animal animal : nearby) {
            if (animal.getAge() == 0 && !animal.isInLove() && animal.canFallInLove() && animal.isFood(food)) {
                eligibleBySpecies.computeIfAbsent(animal.getType(), t -> new ArrayList<>()).add(animal);
            }
        }

        for (List<Animal> group : eligibleBySpecies.values()) {
            if (group.size() < 2) {
                continue;
            }

            if (!consumeFood(2)) {
                return;
            }

            group.get(0).setInLoveTime(LOVE_MODE_TICKS);
            group.get(1).setInLoveTime(LOVE_MODE_TICKS);

            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 7, 0.3, 0.2, 0.3, 0.0);

            return;
        }
    }

    private boolean consumeFood(int amount) {
        if (getStoredCount() < amount) {
            return false;
        }

        int remaining = amount;
        for (int slot = 0; slot < SLOT_COUNT && remaining > 0; slot++) {
            ItemStack stack = items.get(slot);
            int taken = Math.min(stack.getCount(), remaining);
            if (taken > 0) {
                stack.shrink(taken);
                remaining -= taken;
            }
        }

        setChanged();

        return true;
    }

    private int computeLevel() {
        int total = getStoredCount();
        if (total <= 0) {
            return 0;
        }

        int maxStackSize = getStoredFood().getMaxStackSize();
        int fullStacksNeeded = (total + maxStackSize - 1) / maxStackSize;

        return Math.min(fullStacksNeeded, SLOT_COUNT);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (this.level != null && !this.level.isClientSide()) {
            syncBlockState();
        }
    }

    private void syncBlockState() {
        BlockState state = getBlockState();
        int newLevel = computeLevel();

        FeedingTroughBlock.FoodType newFood = state.getValue(FeedingTroughBlock.FOOD);
        ItemStack stored = getStoredFood();
        if (!stored.isEmpty()) {
            newFood = FeedingTroughBlock.FoodType.fromItem(stored.getItem());
        }

        if (state.getValue(FeedingTroughBlock.LEVEL) == newLevel && state.getValue(FeedingTroughBlock.FOOD) == newFood) {
            return;
        }

        this.level.setBlock(
            worldPosition,
            state.setValue(FeedingTroughBlock.LEVEL, newLevel).setValue(FeedingTroughBlock.FOOD, newFood),
            Block.UPDATE_ALL
        );
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
    public int @NotNull [] getSlotsForFace(Direction direction) {
        int[] slots = new int[SLOT_COUNT];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }

        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return true;
    }
}
