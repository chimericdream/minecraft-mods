package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.block.XtremeHopperBlock;
import com.chimericdream.hopperxtreme.client.screen.FilteredHopperScreenHandler;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
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
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import static com.chimericdream.hopperxtreme.block.ModBlocks.FILTERED_HOPPER_SCREEN_HANDLER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.XTREME_HOPPER_BLOCK_ENTITY;

public class XtremeHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    private final int cooldownInTicks;
    public boolean withFilter;

    private static final int[][] AVAILABLE_SLOTS_CACHE = new int[54][];
    private NonNullList<ItemStack> inventory;
    private int transferCooldown;
    private long lastTickTime;
    private Direction facing;

    public XtremeHopperBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getCooldownForBlock(state.getBlock()));
    }

    public XtremeHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks) {
        this(pos, state, cooldownInTicks, false);
    }

    public XtremeHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(XTREME_HOPPER_BLOCK_ENTITY.get(), pos, state);

        this.cooldownInTicks = cooldownInTicks;
        this.inventory = NonNullList.withSize(withFilter ? 6 : 5, ItemStack.EMPTY);
        this.transferCooldown = -1;
        this.withFilter = withFilter;
        this.facing = state.getValue(XtremeHopperBlock.FACING);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.withFilter = view.getBooleanOr(ModInfo.FILTER_NBT_KEY, false);

        this.inventory = NonNullList.withSize(this.withFilter ? 6 : 5, ItemStack.EMPTY);
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

    public int getContainerSize() {
        if (this.withFilter) {
            return this.inventory.size() - 1;
        }

        return this.inventory.size();
    }

    public ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), slot, amount);
    }

    public void setItem(int slot, ItemStack stack) {
        this.unpackLootTable(null);
        this.getItems().set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
    }

    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        this.facing = state.getValue(XtremeHopperBlock.FACING);
    }

    protected Component getDefaultName() {
        Block block = this.getBlockState().getBlock();

        if (block instanceof XtremeHopperBlock) {
            String hopperKey = ((XtremeHopperBlock) block).getBaseKey();
            return Component.translatable(String.format("container.%s", hopperKey));
        }

        return Component.translatable("container.hopper");
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, XtremeHopperBlockEntity blockEntity) {
        --blockEntity.transferCooldown;
        blockEntity.lastTickTime = world.getGameTime();

        if (!blockEntity.needsCooldown()) {
            blockEntity.setTransferCooldown(0);
            insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity));
        }
    }

    private static int getCooldownForBlock(XtremeHopperBlockEntity blockEntity) {
        return getCooldownForBlock(blockEntity.getBlockState().getBlock());
    }

    private static int getCooldownForBlock(Block block) {
        if (block instanceof XtremeHopperBlock) {
            return ((XtremeHopperBlock) block).getCooldownInTicks();
        }

        return 8;
    }

    private static boolean insertAndExtract(Level world, BlockPos pos, BlockState state, XtremeHopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClientSide()) {
            return false;
        }

        if (!blockEntity.needsCooldown() && state.getValue(XtremeHopperBlock.ENABLED)) {
            boolean bl = false;

            if (!blockEntity.isEmpty()) {
                bl = insert(world, pos, blockEntity);
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
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private static boolean insert(Level world, BlockPos pos, XtremeHopperBlockEntity blockEntity) {
        Container inventory = getOutputInventory(world, pos, blockEntity);
        if (inventory == null) {
            return false;
        }

        Direction direction = blockEntity.facing.getOpposite();
        if (isInventoryFull(inventory, direction)) {
            return false;
        }

        for (int i = 0; i < blockEntity.getContainerSize(); ++i) {
            if (blockEntity.withFilter && i == blockEntity.getContainerSize() - 1) {
                continue;
            }

            ItemStack itemStack = blockEntity.getItem(i);

            if (!itemStack.isEmpty()) {
                int j = itemStack.getCount();
                ItemStack itemStack2 = transfer(blockEntity, inventory, blockEntity.removeItem(i, 1), direction);

                if (itemStack2.isEmpty()) {
                    inventory.setChanged();
                    return true;
                }

                itemStack.setCount(j);

                if (j == 1) {
                    blockEntity.setItem(i, itemStack);
                }
            }
        }

        return false;
    }

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

    private static boolean isInventoryFull(Container inventory, Direction direction) {
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
        BlockPos blockPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        BlockState blockState = world.getBlockState(blockPos);
        Container inventory = getInputInventory(world, hopper, blockPos, blockState);

        if (inventory != null) {
            Direction direction = Direction.DOWN;
            int[] var11 = getAvailableSlots(inventory, direction);
            int var12 = var11.length;

            for (int var8 = 0; var8 < var12; ++var8) {
                int i = var11[var8];

                if (extract(hopper, inventory, i, direction)) {
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

        if (target instanceof XtremeHopperBlockEntity hopper && hopper.withFilter) {
            return HopperItemFilterItem.matchesFilter(hopper.getItem(5), stack);
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

        if (((XtremeHopperBlockEntity) hopper).withFilter) {
            return HopperItemFilterItem.matchesFilter(hopper.getItem(5), stack);
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
                if (bl2 && hopper instanceof XtremeHopperBlockEntity hopperBlockEntity) {
                    if (!hopperBlockEntity.isDisabled()) {
                        int j = 0;

                        if (source instanceof XtremeHopperBlockEntity hopperBlockEntity2) {
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

    @Nullable
    private static Container getOutputInventory(Level world, BlockPos pos, XtremeHopperBlockEntity blockEntity) {
        return getInventoryAt(world, pos.relative(blockEntity.facing));
    }

    @Nullable
    private static Container getInputInventory(Level world, Hopper hopper, BlockPos pos, BlockState state) {
        return getInventoryAt(world, pos, state, hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
    }

    public static List<ItemEntity> getInputItemEntities(Level world, Hopper hopper) {
        AABB box = hopper.getSuckAabb().move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5);
        return world.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE);
    }

    @Nullable
    public static Container getInventoryAt(Level world, BlockPos pos) {
        return getInventoryAt(world, pos, world.getBlockState(pos), (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5);
    }

    @Nullable
    private static Container getInventoryAt(Level world, BlockPos pos, BlockState state, double x, double y, double z) {
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
        List<Entity> list = world.getEntities(null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR);
        return !list.isEmpty() ? (Container) list.get(world.random.nextInt(list.size())) : null;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxStackSize() && ItemStack.isSameItemSameComponents(first, second);
    }

    public double getLevelX() {
        return (double) this.worldPosition.getX() + 0.5;
    }

    public double getLevelY() {
        return (double) this.worldPosition.getY() + 0.5;
    }

    public double getLevelZ() {
        return (double) this.worldPosition.getZ() + 0.5;
    }

    public boolean isGridAligned() {
        return true;
    }

    private void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean isDisabled() {
        return this.transferCooldown > this.cooldownInTicks;
    }

    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public static void onEntityCollided(Level world, BlockPos pos, BlockState state, Entity entity, XtremeHopperBlockEntity blockEntity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (!itemEntity.getItem().isEmpty() && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(blockEntity.getSuckAabb())) {
                insertAndExtract(world, pos, state, blockEntity, () -> extract(blockEntity, itemEntity));
            }
        }
    }

    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        if (this.withFilter) {
            return new FilteredHopperScreenHandler(FILTERED_HOPPER_SCREEN_HANDLER.get(), syncId, playerInventory, this);
        }

        return new HopperMenu(syncId, playerInventory, this);
    }
}
