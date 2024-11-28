package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.block.GlazedHopperBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_HOPPER_BLOCK_ENTITY;

public class GlazedHopperBlockEntity extends LootableContainerBlockEntity implements Hopper {
    private final int cooldownInTicks;

    private static final int[][] AVAILABLE_SLOTS_CACHE = new int[54][];
    private DefaultedList<ItemStack> inventory;
    private int transferCooldown;
    private long lastTickTime;
    private Direction facing;

    public GlazedHopperBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getCooldownForBlock(state.getBlock()));
    }

    public GlazedHopperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks) {
        super(GLAZED_HOPPER_BLOCK_ENTITY.get(), pos, state);

        this.cooldownInTicks = cooldownInTicks;
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.transferCooldown = -1;
        this.facing = (Direction) state.get(GlazedHopperBlock.FACING);
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }

        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }

        nbt.putInt("TransferCooldown", this.transferCooldown);
    }

    public int size() {
        return this.inventory.size();
    }

    public ItemStack removeStack(int slot, int amount) {
        this.generateLoot(null);
        return Inventories.splitStack(this.getHeldStacks(), slot, amount);
    }

    public void setStack(int slot, ItemStack stack) {
        this.generateLoot(null);
        this.getHeldStacks().set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
    }

    public void setCachedState(BlockState state) {
        super.setCachedState(state);
        this.facing = state.get(GlazedHopperBlock.FACING);
    }

    protected Text getContainerName() {
        Block block = this.getCachedState().getBlock();

        if (block instanceof GlazedHopperBlock) {
            String hopperKey = ((GlazedHopperBlock) block).getBaseKey();
            return Text.translatable(String.format("container.%s", hopperKey));
        }

        return Text.translatable("container.hopper");
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, GlazedHopperBlockEntity blockEntity) {
        --blockEntity.transferCooldown;
        blockEntity.lastTickTime = world.getTime();

        if (!blockEntity.needsCooldown()) {
            blockEntity.setTransferCooldown(0);
            dropAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity));
        }
    }

    private static int getCooldownForBlock(GlazedHopperBlockEntity blockEntity) {
        return getCooldownForBlock(blockEntity.getCachedState().getBlock());
    }

    private static int getCooldownForBlock(Block block) {
        if (block instanceof GlazedHopperBlock) {
            return ((GlazedHopperBlock) block).getCooldownInTicks();
        }

        return 8;
    }

    private static boolean dropAndExtract(World world, BlockPos pos, BlockState state, GlazedHopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return false;
        }

        if (blockEntity.needsCooldown() || !state.get(GlazedHopperBlock.ENABLED)) {
            return false;
        }

        boolean hasChanged = false;

        if (!blockEntity.isEmpty()) {
            hasChanged = drop(world, pos, blockEntity);
        }

        if (!blockEntity.isFull()) {
            hasChanged |= booleanSupplier.getAsBoolean();
        }

        if (hasChanged) {
            blockEntity.setTransferCooldown(getCooldownForBlock(blockEntity));
            markDirty(world, pos, state);

            return true;
        }

        return false;
    }

    private boolean isFull() {
        Iterator<?> var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack) var1.next();
        } while (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount());

        return false;
    }

    private static boolean drop(World world, BlockPos pos, GlazedHopperBlockEntity blockEntity) {
        BlockState blockState = getFacingBlock(world, pos, blockEntity);
        if (blockState.isSideSolidFullSquare(world, pos, blockEntity.facing)) {
            return false;
        }

        ItemStack stack = blockEntity.getStack(0);
        if (stack.isEmpty()) {
            return false;
        }

        ItemStack stack2 = stack.copy();
        stack2.setCount(1);
        stack.decrement(1);

        blockEntity.setStack(0, stack);

        drop(world, stack2, pos, blockEntity.facing, blockEntity.cooldownInTicks);

        return true;
    }

    private static int[] getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory sidedInventory) {
            return sidedInventory.getAvailableSlots(side);
        }

        int i = inventory.size();
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

    public static boolean extract(World world, Hopper hopper) {
        BlockPos blockPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
        BlockState blockState = world.getBlockState(blockPos);
        Inventory inventory = getInputInventory(world, hopper, blockPos, blockState);

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

        boolean bl = hopper.canBlockFromAbove() && blockState.isFullCube(world, blockPos) && !blockState.isIn(BlockTags.DOES_NOT_BLOCK_HOPPERS);
        if (!bl) {
            Iterator var6 = getInputItemEntities(world, hopper).iterator();

            while (var6.hasNext()) {
                ItemEntity itemEntity = (ItemEntity) var6.next();
                if (extract(hopper, itemEntity)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        ItemStack itemStack = inventory.getStack(slot);
        if (!itemStack.isEmpty() && canExtract(hopper, inventory, itemStack, slot, side)) {
            int i = itemStack.getCount();
            ItemStack itemStack2 = transfer(inventory, hopper, inventory.removeStack(slot, 1), null);

            if (itemStack2.isEmpty()) {
                inventory.markDirty();
                return true;
            }

            itemStack.setCount(i);
            if (i == 1) {
                inventory.setStack(slot, itemStack);
            }
        }

        return false;
    }

    public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
        boolean bl = false;

        ItemStack itemStack = itemEntity.getStack().copy();
        ItemStack itemStack2 = transfer(null, inventory, itemStack, null);

        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.setStack(ItemStack.EMPTY);
            itemEntity.discard();
        } else {
            itemEntity.setStack(itemStack2);
        }

        return bl;
    }

    public static void drop(World world, ItemStack stack, BlockPos pos, Direction facing, int hopperSpeed) {
        double x = (double) pos.getX() + 0.5 + 0.1 * (double) facing.getOffsetX();
        double y = (double) pos.getY() + 0.1 * (double) facing.getOffsetY();
        double z = (double) pos.getZ() + 0.5 + 0.1 * (double) facing.getOffsetZ();

        double vx = 0.0;
        double vy = 0.0;
        double vz = 0.0;

        switch (facing) {
            case DOWN:
                y = (double) pos.getY() - 0.6 - 0.1 * (double) facing.getOffsetX();
                vy = -0.1 * ((double) 8 / hopperSpeed);
                break;
            case NORTH:
                z = (double) pos.getZ() - 0.6 - 0.1 * (double) facing.getOffsetZ();
                vz = -0.125 * ((double) 8 / hopperSpeed);
                break;
            case SOUTH:
                z = (double) pos.getZ() + 1.1 + 0.1 * (double) facing.getOffsetZ();
                vz = 0.125 * ((double) 8 / hopperSpeed);
                break;
            case WEST:
                x = (double) pos.getX() - 0.6 - 0.1 * (double) facing.getOffsetX();
                vx = -0.125 * ((double) 8 / hopperSpeed);
                break;
            case EAST:
                x = (double) pos.getX() + 1.1 + 0.1 * (double) facing.getOffsetX();
                vx = 0.125 * ((double) 8 / hopperSpeed);
                break;
        }

        ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
        itemEntity.setVelocity(vx, vy, vz);
        world.spawnEntity(itemEntity);
    }

    public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
        int i;
        if (to instanceof SidedInventory sidedInventory) {
            if (side != null) {
                int[] is = sidedInventory.getAvailableSlots(side);

                for (i = 0; i < is.length && !stack.isEmpty(); ++i) {
                    stack = transfer(from, to, stack, is[i], side);
                }

                return stack;
            }
        }

        int j = to.size();

        for (i = 0; i < j && !stack.isEmpty(); ++i) {
            stack = transfer(from, to, stack, i, side);
        }

        return stack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        }

        if (inventory instanceof SidedInventory sidedInventory) {
            if (!sidedInventory.canInsert(slot, stack, side)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canExtract(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing) {
        if (!fromInventory.canTransferTo(hopperInventory, slot, stack)) {
            return false;
        }

        if (fromInventory instanceof SidedInventory sidedInventory) {
            if (!sidedInventory.canExtract(slot, stack, facing)) {
                return false;
            }
        }

        return true;
    }

    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = to.getStack(slot);

        if (canInsert(to, stack, slot, side)) {
            boolean bl = false;
            boolean bl2 = to.isEmpty();

            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
                bl = j > 0;
            }

            if (bl) {
                if (bl2 && to instanceof GlazedHopperBlockEntity hopperBlockEntity) {
                    if (!hopperBlockEntity.isDisabled()) {
                        int j = 0;

                        if (from instanceof GlazedHopperBlockEntity hopperBlockEntity2) {
                            if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
                                j = 1;
                            }
                        }

                        hopperBlockEntity.setTransferCooldown(getCooldownForBlock(hopperBlockEntity) - j);
                    }
                }

                to.markDirty();
            }
        }

        return stack;
    }

    private static BlockState getFacingBlock(World world, BlockPos pos, GlazedHopperBlockEntity blockEntity) {
        return world.getBlockState(pos.offset(blockEntity.facing));
    }

    @Nullable
    private static Inventory getInputInventory(World world, Hopper hopper, BlockPos pos, BlockState state) {
        return getInventoryAt(world, pos, state, hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
    }

    public static List<ItemEntity> getInputItemEntities(World world, Hopper hopper) {
        Box box = hopper.getInputAreaShape().offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5);
        return world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY);
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos pos) {
        return getInventoryAt(world, pos, world.getBlockState(pos), (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5);
    }

    @Nullable
    private static Inventory getInventoryAt(World world, BlockPos pos, BlockState state, double x, double y, double z) {
        Inventory inventory = getBlockInventoryAt(world, pos, state);
        if (inventory == null) {
            inventory = getEntityInventoryAt(world, x, y, z);
        }

        return inventory;
    }

    @Nullable
    private static Inventory getBlockInventoryAt(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof InventoryProvider) {
            return ((InventoryProvider) block).getInventory(state, world, pos);
        }

        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory inventory) {
                if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    inventory = ChestBlock.getInventory((ChestBlock) block, state, world, pos, true);
                }

                return inventory;
            }
        }

        return null;
    }

    @Nullable
    private static Inventory getEntityInventoryAt(World world, double x, double y, double z) {
        List<Entity> list = world.getOtherEntities(null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES);
        return !list.isEmpty() ? (Inventory) list.get(world.random.nextInt(list.size())) : null;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
    }

    public double getHopperX() {
        return (double) this.pos.getX() + 0.5;
    }

    public double getHopperY() {
        return (double) this.pos.getY() + 0.5;
    }

    public double getHopperZ() {
        return (double) this.pos.getZ() + 0.5;
    }

    public boolean canBlockFromAbove() {
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

    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, GlazedHopperBlockEntity blockEntity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (!itemEntity.getStack().isEmpty() && entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(blockEntity.getInputAreaShape())) {
                dropAndExtract(world, pos, state, blockEntity, () -> extract(blockEntity, itemEntity));
            }
        }
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }
}
