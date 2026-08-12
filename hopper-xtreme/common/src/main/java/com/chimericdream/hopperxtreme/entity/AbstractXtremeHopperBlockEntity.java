package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.block.HopperDeprecation;
import com.chimericdream.hopperxtreme.block.HopperVariantBlock;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

/**
 * Shared logic for every Hopper X-Treme hopper/hupper/glazed block entity. This is a straight
 * extraction of the vanilla-hopper fork that all six variants used to carry an ~80%-identical copy
 * of; the variation surface is exposed through a small set of hooks:
 *
 * <ul>
 *   <li>{@link #storageSlotCount()} — 5 (Xtreme) or 1 (Glazed); the filter slot, when present, sits
 *       at index {@link #getContainerSize()} directly after the storage slots.</li>
 *   <li>{@link #extractSide()} / {@link #inputBlockYOffset()} / {@link #levelYOffset()} — the
 *       pull direction (hopper=DOWN/above, hupper=UP/below).</li>
 *   <li>{@link #pushOutput(Level, BlockPos)} — insert into a facing container vs. drop an item in
 *       front, supplied by {@link AbstractSingleFacingXtremeHopperBlockEntity} /
 *       {@link AbstractMultiXtremeHopperBlockEntity}.</li>
 * </ul>
 *
 * <p>The parallel collapse of the six <em>block</em> classes and the screen handlers/screens is a
 * deferred follow-up (they don't share geometry, so it's a block-entity-plumbing dedup, not one base
 * class) — see the "Step 4 (deferred)" section of {@code hopper-xtreme/REFACTOR-3.1-PLAN.md} for the
 * concrete approach.
 */
public abstract class AbstractXtremeHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    private static final int[][] AVAILABLE_SLOTS_CACHE = new int[54][];

    protected final int cooldownInTicks;
    public boolean withFilter;

    protected NonNullList<ItemStack> inventory;
    private int transferCooldown;
    private long lastTickTime;

    protected AbstractXtremeHopperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(type, pos, state);

        this.cooldownInTicks = cooldownInTicks;
        this.withFilter = withFilter;
        this.inventory = NonNullList.withSize(withFilter ? storageSlotCount() + 1 : storageSlotCount(), ItemStack.EMPTY);
        this.transferCooldown = -1;
    }

    // --- variation hooks -----------------------------------------------------------------------

    /** Number of item-storage slots (excludes the filter slot). */
    protected abstract int storageSlotCount();

    /** Side an input container is approached from: DOWN for a hopper (pulls from above), UP for a hupper. */
    protected abstract Direction extractSide();

    /** Added to {@link #getLevelY()} to locate the input block: +1.0 for a hopper, 0.0 for a hupper. */
    protected abstract double inputBlockYOffset();

    /** Offset applied to the block Y for {@link #getLevelY()}: +0.5 for a hopper, -0.5 for a hupper. */
    protected abstract double levelYOffset();

    /** Move items out of this hopper (insert into a container or drop in front). Returns whether anything moved. */
    protected abstract boolean pushOutput(Level world, BlockPos pos);

    // --- persistence ---------------------------------------------------------------------------

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        // Every hopper is now filter-capable; derive the flag from the block so placed blocks saved
        // before this change (withFilter=false in NBT) are upgraded on load.
        Block block = this.getBlockState().getBlock();
        this.withFilter = block instanceof HopperVariantBlock variant ? variant.isWithFilter() : this.withFilter;

        this.inventory = NonNullList.withSize(this.withFilter ? storageSlotCount() + 1 : storageSlotCount(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(view)) {
            ContainerHelper.loadAllItems(view, this.inventory);
        }

        this.transferCooldown = view.getIntOr("TransferCooldown", -1);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        if (!this.trySaveLootTable(view)) {
            ContainerHelper.saveAllItems(view, this.inventory);
        }

        view.putBoolean(ModInfo.FILTER_NBT_KEY, this.withFilter);
        view.putInt("TransferCooldown", this.transferCooldown);
    }

    // --- Container -----------------------------------------------------------------------------

    public int getContainerSize() {
        if (this.withFilter) {
            return this.inventory.size() - 1;
        }

        return this.inventory.size();
    }

    public @NotNull ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), slot, amount);
    }

    public void setItem(int slot, ItemStack stack) {
        this.unpackLootTable(null);
        this.getItems().set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
    }

    protected @NotNull Component getDefaultName() {
        Block block = this.getBlockState().getBlock();

        if (block instanceof HopperVariantBlock variant) {
            return Component.translatable(String.format("container.%s", variant.getBaseKey()));
        }

        return Component.translatable("container.hopper");
    }

    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    /**
     * Vanilla's default {@code BlockEntity.preRemoveSideEffects} drops contents via
     * {@code Containers.dropContents(level, pos, (Container) this)}, which walks
     * {@code 0..getContainerSize()-1}. {@link #getContainerSize()} deliberately hides the filter slot
     * (index {@code storageSlotCount()}) from that count so insertion/extraction/fullness checks don't
     * see it — but that means the filter item was silently deleted on block break instead of dropping.
     * Drop the whole backing list, filter slot included.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.unpackLootTable(null);

        Level world = this.getLevel();
        if (world != null) {
            Containers.dropContents(world, pos, this.inventory);
        }
    }

    // --- ticking -------------------------------------------------------------------------------

    public static void serverTick(Level world, BlockPos pos, BlockState state, AbstractXtremeHopperBlockEntity blockEntity) {
        if (HopperDeprecation.convertIfDeprecated(world, pos, state)) {
            return;
        }

        --blockEntity.transferCooldown;
        blockEntity.lastTickTime = world.getGameTime();

        if (!blockEntity.needsCooldown()) {
            blockEntity.setTransferCooldown(0);
            insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity));
        }
    }

    public static void onEntityCollided(Level world, BlockPos pos, BlockState state, Entity entity, AbstractXtremeHopperBlockEntity blockEntity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (!itemEntity.getItem().isEmpty() && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(blockEntity.getSuckAabb())) {
                insertAndExtract(world, pos, state, blockEntity, () -> extract(blockEntity, itemEntity));
            }
        }
    }

    private static int getCooldownForBlock(AbstractXtremeHopperBlockEntity blockEntity) {
        return getCooldownForBlock(blockEntity.getBlockState().getBlock());
    }

    private static int getCooldownForBlock(Block block) {
        if (block instanceof HopperVariantBlock variant) {
            return variant.getCooldownInTicks();
        }

        return 8;
    }

    private static boolean insertAndExtract(Level world, BlockPos pos, BlockState state, AbstractXtremeHopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClientSide()) {
            return false;
        }

        if (!blockEntity.needsCooldown() && state.getValue(BlockStateProperties.ENABLED)) {
            boolean bl = false;

            if (!blockEntity.isEmpty()) {
                bl = blockEntity.pushOutput(world, pos);
            }

            if (!blockEntity.isFull()) {
                bl |= booleanSupplier.getAsBoolean();
            }

            if (bl) {
                blockEntity.setTransferCooldown(getCooldownForBlock(blockEntity));
                setChanged(world, pos, state);

                return true;
            }
        }

        return false;
    }

    private boolean isFull() {
        // Storage slots only. The backing list also holds the filter slot, which getContainerSize()
        // hides, so iterating the list made a filtered hopper with an empty filter never report full
        // and attempt an extraction on every tick.
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack itemStack = this.inventory.get(i);

            if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    // --- output helpers (shared by the single-facing / multi loops) ----------------------------

    /**
     * Resolves the container to output into at {@code pos}, using this variant's Y offset so a
     * hupper searches for entity containers in the same spot a hopper would mirror.
     */
    @Nullable
    protected Container getOutputInventoryAt(Level world, BlockPos pos) {
        return getInventoryAt(world, pos, world.getBlockState(pos), (double) pos.getX() + 0.5, (double) pos.getY() + this.levelYOffset(), (double) pos.getZ() + 0.5);
    }

    // --- extraction (input side) ---------------------------------------------------------------

    private static int[] getAvailableSlots(Container inventory, Direction side) {
        if (inventory instanceof WorldlyContainer sidedInventory) {
            return sidedInventory.getSlotsForFace(side);
        }

        int i = inventory.getContainerSize();
        if (i < AVAILABLE_SLOTS_CACHE.length) {
            int[] is = AVAILABLE_SLOTS_CACHE[i];

            if (is != null) {
                return is;
            }

            int[] js = indexArray(i);
            AVAILABLE_SLOTS_CACHE[i] = js;

            return js;
        }

        return indexArray(i);
    }

    private static int[] indexArray(int size) {
        int[] is = new int[size];

        for (int i = 0; i < is.length; is[i] = i++) {
        }

        return is;
    }

    protected static boolean isInventoryFull(Container inventory, Direction direction) {
        int[] is = getAvailableSlots(inventory, direction);
        int[] var3 = is;
        int var4 = is.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            int i = var3[var5];
            ItemStack itemStack = inventory.getItem(i);

            if (itemStack.getCount() < itemStack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    public static boolean extract(Level world, Hopper hopper) {
        // The public entry point accepts any vanilla Hopper (e.g. a hopper minecart), so default to
        // vanilla "pull from directly above" semantics and only consult the variant hooks when this
        // really is one of ours.
        Direction extractSide = Direction.DOWN;
        double inputYOffset = 1.0;
        if (hopper instanceof AbstractXtremeHopperBlockEntity blockEntity) {
            extractSide = blockEntity.extractSide();
            inputYOffset = blockEntity.inputBlockYOffset();
        }

        BlockPos blockPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + inputYOffset, hopper.getLevelZ());
        BlockState blockState = world.getBlockState(blockPos);
        Container inventory = getInputInventory(world, hopper, blockPos, blockState, inputYOffset);

        if (inventory != null) {
            int[] var11 = getAvailableSlots(inventory, extractSide);
            int var12 = var11.length;

            for (int var8 = 0; var8 < var12; ++var8) {
                int i = var11[var8];

                if (extract(hopper, inventory, i, extractSide)) {
                    return true;
                }
            }

            return false;
        }

        boolean bl = hopper.isGridAligned() && blockState.isCollisionShapeFullBlock(world, blockPos) && !blockState.is(BlockTags.DOES_NOT_BLOCK_HOPPERS);
        if (!bl) {
            for (ItemEntity itemEntity : getInputItemEntities(world, hopper)) {
                if (extract((Container) hopper, (ItemEntity) itemEntity)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean extract(Hopper hopper, Container inventory, int slot, Direction side) {
        ItemStack itemStack = inventory.getItem(slot);
        if (!itemStack.isEmpty() && canExtract(hopper, inventory, itemStack, slot, side)) {
            int i = itemStack.getCount();
            ItemStack itemStack2 = transfer(inventory, hopper, inventory.removeItem(slot, 1), null);

            if (itemStack2.isEmpty()) {
                inventory.setChanged();
                return true;
            }

            itemStack.setCount(i);
            if (i == 1) {
                inventory.setItem(slot, itemStack);
            }
        }

        return false;
    }

    public static boolean extract(Container inventory, ItemEntity itemEntity) {
        boolean bl = false;

        ItemStack itemStack = itemEntity.getItem().copy();
        ItemStack itemStack2 = transfer(null, inventory, itemStack, null);

        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.setItem(ItemStack.EMPTY);
            itemEntity.discard();
        } else {
            itemEntity.setItem(itemStack2);
        }

        return bl;
    }

    // --- transfer / can-insert / can-extract ---------------------------------------------------

    public static ItemStack transfer(@Nullable Container source, Container hopper, ItemStack stack, @Nullable Direction side) {
        int i;
        if (hopper instanceof WorldlyContainer hopperInventory) {
            if (side != null) {
                int[] is = hopperInventory.getSlotsForFace(side);

                for (i = 0; i < is.length && !stack.isEmpty(); ++i) {
                    stack = transfer(source, hopper, stack, is[i], side);
                }

                return stack;
            }
        }

        int j = hopper.getContainerSize();

        for (i = 0; i < j && !stack.isEmpty(); ++i) {
            stack = transfer(source, hopper, stack, i, side);
        }

        return stack;
    }

    private static boolean canInsert(Container target, ItemStack stack, int slot, @Nullable Direction side) {
        if (!target.canPlaceItem(slot, stack)) {
            return false;
        }

        if (target instanceof WorldlyContainer hopperInventory) {
            if (!hopperInventory.canPlaceItemThroughFace(slot, stack, side)) {
                return false;
            }
        }

        // Only the storage slots (0..getContainerSize()-1) are filter-checked; the filter item itself
        // lives at index getContainerSize() and is placed through the menu, never this path.
        if (target instanceof AbstractXtremeHopperBlockEntity hopper && hopper.withFilter && slot < hopper.getContainerSize()) {
            return HopperItemFilterItem.matchesFilter(hopper.getItem(hopper.getContainerSize()), stack);
        }

        return true;
    }

    private static boolean canExtract(Container hopper, Container source, ItemStack stack, int slot, Direction facing) {
        if (!source.canTakeItem(hopper, slot, stack)) {
            return false;
        }

        if (source instanceof WorldlyContainer sourceInventory) {
            if (!sourceInventory.canTakeItemThroughFace(slot, stack, facing)) {
                return false;
            }
        }

        // canExtract's public entry point accepts any Hopper, so this can't assume the
        // container is our own block entity.
        if (hopper instanceof AbstractXtremeHopperBlockEntity filtered) {
            return filtered.passesExtractFilter(stack, slot);
        }

        return true;
    }

    /**
     * Whether this hopper may pull {@code stack} out of a source slot. Filtered hoppers only accept
     * items matching their filter; unfiltered hoppers accept everything.
     */
    protected boolean passesExtractFilter(ItemStack stack, int slot) {
        if (this.withFilter) {
            return HopperItemFilterItem.matchesFilter(this.getItem(this.getContainerSize()), stack);
        }

        return true;
    }

    private static ItemStack transfer(@Nullable Container source, Container hopper, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = hopper.getItem(slot);

        if (canInsert(hopper, stack, slot, side)) {
            boolean bl = false;
            boolean bl2 = hopper.isEmpty();

            if (itemStack.isEmpty()) {
                hopper.setItem(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (canMergeItems(itemStack, stack)) {
                int i = stack.getMaxStackSize() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemStack.grow(j);
                bl = j > 0;
            }

            if (bl) {
                if (bl2 && hopper instanceof AbstractXtremeHopperBlockEntity hopperBlockEntity) {
                    if (!hopperBlockEntity.isDisabled()) {
                        int j = 0;

                        if (source instanceof AbstractXtremeHopperBlockEntity hopperBlockEntity2) {
                            if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
                                j = 1;
                            }
                        }

                        hopperBlockEntity.setTransferCooldown(getCooldownForBlock(hopperBlockEntity) - j);
                    }
                }

                hopper.setChanged();
            }
        }

        return stack;
    }

    // --- inventory lookup ----------------------------------------------------------------------

    @Nullable
    private static Container getInputInventory(Level world, Hopper hopper, BlockPos pos, BlockState state, double inputYOffset) {
        return getInventoryAt(world, pos, state, hopper.getLevelX(), hopper.getLevelY() + inputYOffset, hopper.getLevelZ());
    }

    public static List<ItemEntity> getInputItemEntities(Level world, Hopper hopper) {
        AABB box = hopper.getSuckAabb().move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5);
        return world.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE);
    }

    @Nullable
    protected static Container getInventoryAt(Level world, BlockPos pos, BlockState state, double x, double y, double z) {
        Container inventory = getBlockInventoryAt(world, pos, state);
        if (inventory == null) {
            inventory = getEntityInventoryAt(world, x, y, z);
        }

        return inventory;
    }

    @Nullable
    private static Container getBlockInventoryAt(Level world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            return ((WorldlyContainerHolder) block).getContainer(state, world, pos);
        }

        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Container inventory) {
                if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    inventory = ChestBlock.getContainer((ChestBlock) block, state, world, pos, true);
                }

                return inventory;
            }
        }

        return null;
    }

    @Nullable
    private static Container getEntityInventoryAt(Level world, double x, double y, double z) {
        List<Entity> list = world.getEntities((Entity) null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR);
        return !list.isEmpty() ? (Container) list.get(world.getRandom().nextInt(list.size())) : null;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxStackSize() && ItemStack.isSameItemSameComponents(first, second);
    }

    // --- drop helper (used by the glazed variants) ---------------------------------------------

    public static void drop(Level world, ItemStack stack, BlockPos pos, Direction facing, int hopperSpeed) {
        double x = (double) pos.getX() + 0.5 + 0.1 * (double) facing.getStepX();
        double y = (double) pos.getY() + 0.1 * (double) facing.getStepY();
        double z = (double) pos.getZ() + 0.5 + 0.1 * (double) facing.getStepZ();

        double vx = 0.0;
        double vy = 0.0;
        double vz = 0.0;

        switch (facing) {
            case DOWN:
                y = (double) pos.getY() - 0.6 - 0.1 * (double) facing.getStepX();
                vy = -0.125 * ((double) 8 / hopperSpeed);
                break;
            case NORTH:
                z = (double) pos.getZ() - 0.6 - 0.1 * (double) facing.getStepZ();
                vz = -0.125 * ((double) 8 / hopperSpeed);
                break;
            case SOUTH:
                z = (double) pos.getZ() + 1.1 + 0.1 * (double) facing.getStepZ();
                vz = 0.125 * ((double) 8 / hopperSpeed);
                break;
            case WEST:
                x = (double) pos.getX() - 0.6 - 0.1 * (double) facing.getStepX();
                vx = -0.125 * ((double) 8 / hopperSpeed);
                break;
            case EAST:
                x = (double) pos.getX() + 1.1 + 0.1 * (double) facing.getStepX();
                vx = 0.125 * ((double) 8 / hopperSpeed);
                break;
        }

        ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
        itemEntity.setDeltaMovement(vx, vy, vz);
        world.addFreshEntity(itemEntity);
    }

    // --- Hopper geometry -----------------------------------------------------------------------

    public double getLevelX() {
        return (double) this.worldPosition.getX() + 0.5;
    }

    public double getLevelY() {
        return (double) this.worldPosition.getY() + this.levelYOffset();
    }

    public double getLevelZ() {
        return (double) this.worldPosition.getZ() + 0.5;
    }

    public boolean isGridAligned() {
        return true;
    }

    // --- cooldown ------------------------------------------------------------------------------

    private void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean isDisabled() {
        return this.transferCooldown > this.cooldownInTicks;
    }
}
