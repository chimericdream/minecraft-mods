package com.chimericdream.minekea.entity.block.containers;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.block.containers.ContainerBlocks;
import com.chimericdream.minekea.fluid.ModFluids;
import com.chimericdream.minekea.item.containers.ContainerItems;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class GlassJarBlockEntity extends BlockEntity implements ImplementedInventory {
    static final Logger LOGGER = LogUtils.getLogger();

    public static final int MAX_BUCKETS = 8;
    public static final double BOTTLE_SIZE = 0.33;

    // Since the `items` stores a single stack, the actual total is one higher than this
    public static final int MAX_ITEM_STACKS = 7;

    // The jar presents itself to automation as a two-slot Container. Slot 0 is the real, single-stack
    // "active" slot (what renders and what a hopper drains). Slot 1 is a virtual overflow *input*: it lets a
    // hopper keep pushing once the active slot is full (a one-slot container reads as "full" the moment its
    // only slot hits a full stack, so the hopper gives up before touching the reserve), and setItem routes
    // whatever lands there down into the compressed reserve. It reads empty while the jar has capacity, and a
    // phantom full stack once the jar is completely full (so a pushing hopper trips isInventoryFull's cheap
    // early-out); it is never extractable and never rendered.
    public static final int STORAGE_SLOT = 0;
    public static final int OVERFLOW_SLOT = 1;
    private static final int CONTAINER_SIZE = 2;

    // The jar exposes itself to automation as a genuine single-slot Container (see the Container overrides
    // near the bottom of the class). Slot 0 holds the "active" stack; fullItemStacks is a count of extra
    // *compressed* full stacks of that same item, giving the jar a total capacity of MAX_ITEM_STACKS + 1
    // stacks. The stored item's identity only lives on the active stack, so the active stack is kept
    // non-empty whenever the jar holds any items (fullItemStacks > 0 always implies a non-empty slot 0).
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private int fullItemStacks = 0;

    // Vanilla hopper extraction (HopperBlockEntity#tryTakeInItemFromSlot, and the Hopper X-Treme fork)
    // removes one item and, when the destination rejects it *and* the source slot held exactly one item,
    // puts the original stack back via setItem. When removeItem cascades a compressed reserve stack down
    // into the freshly-emptied active slot, that naive putback would overwrite the refilled stack and
    // silently delete a whole reserve stack. We remember the cascading removal so setItem can recognise the
    // putback and hand the borrowed reserve stack back instead of losing items.
    private ItemStack pendingCascadePutback = ItemStack.EMPTY;

    private Fluid storedFluid = Fluids.EMPTY;
    private double fluidAmountInBuckets = 0.0;

    private TypedEntityData<EntityType<?>> storedMobData = null;
    private String storedMobId = null;

    // The item's own display name (either the mob's real, player-given name, or our generated
    // "<mob> in a jar" fallback) and whether it's the former. Lives on the ItemStack as vanilla's
    // CUSTOM_NAME component / a flag inside CUSTOM_DATA, so it has to be captured here too or it's
    // lost the moment the jar is placed and later broken back into an item via toItemStack().
    private Component customName = null;
    private boolean hasCustomName = false;

    public static final String ITEM_KEY = "StoredItem";
    public static final String ITEM_QTY_KEY = "StoredItemQty";
    public static final String ITEM_STACKS_KEY = "FullItemStacks";
    public static final String FLUID_KEY = "StoredFluid";
    public static final String FLUID_AMT_KEY = "StoredFluidAmount";
    public static final String MOB_DATA_KEY = "StoredMobData";
    public static final String CUSTOM_NAME_KEY = "CustomName";
    public static final String HAS_CUSTOM_NAME_KEY = "HasCustomName";

    public GlassJarBlockEntity(BlockPos pos, BlockState state) {
        this(ContainerBlocks.GLASS_JAR_BLOCK_ENTITY.get(), pos, state);
    }

    public GlassJarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void readDataFromItemStack(ItemStack stack) {
        readMobDataFromComponent(stack);
        readCustomNameFromStack(stack);

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        CompoundTag nbt = customData.copyTag();
        this.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, this.level.registryAccess(), nbt));
    }

    public static GlassJarBlockEntity fromItemStack(ItemStack stack, Level world) {
        GlassJarBlockEntity entity = new GlassJarBlockEntity(ContainerBlocks.GLASS_JAR_BLOCK_ENTITY.get(), BlockPos.ZERO, ContainerBlocks.GLASS_JAR.get().defaultBlockState());
        entity.setLevel(world);

        entity.readMobDataFromComponent(stack);
        entity.readCustomNameFromStack(stack);

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return entity;
        }

        CompoundTag nbt = customData.copyTag();
        entity.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), nbt));

        return entity;
    }

    /*
     * A freshly captured mob is stored on the ItemStack via the vanilla DataComponents.ENTITY_DATA
     * component (set in GlassJarItem#interactLivingEntity), not under CUSTOM_DATA. A placed block only
     * picks this up indirectly, once the server resyncs it to the client and BlockEntity's own
     * component -> "components" NBT plumbing round-trips it back through readMobData. The item-render
     * preview (fromItemStack) never goes through that resync, so it needs to read the component directly.
     */
    private void readMobDataFromComponent(ItemStack stack) {
        TypedEntityData<EntityType<?>> entityData = stack.get(DataComponents.ENTITY_DATA);
        if (entityData == null) {
            return;
        }

        storedMobData = entityData;
        storedMobId = EntityType.getKey(entityData.type()).toString();
    }

    /*
     * The display name ("Allay in a jar", or the mob's own name) lives on the ItemStack's vanilla
     * CUSTOM_NAME component, set once at capture time in GlassJarItem#interactLivingEntity. It's never
     * part of CUSTOM_DATA, so it has to be read from the stack directly, same as the mob's entity data.
     */
    private void readCustomNameFromStack(ItemStack stack) {
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        if (name != null) {
            customName = name;
        }
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(ContainerItems.GLASS_JAR_ITEM.get());
        TagValueOutput writeView = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);

        // saveAdditional() is also what the vanilla chunk save/load machinery uses to persist a placed,
        // never-picked-up block entity, so it always writes the mob's full entity NBT into CUSTOM_DATA.
        // Here, though, that same data is being set a few lines down as the real ENTITY_DATA/CUSTOM_NAME
        // components - exactly like a fresh capture looks - so CUSTOM_DATA only needs the one flag that
        // has no component equivalent. For fluid/item jars there's no such component, so their state
        // still has to go into CUSTOM_DATA in full.
        writeView.putBoolean(HAS_CUSTOM_NAME_KEY, hasCustomName);

        if (hasMob()) {
            stack.set(DataComponents.ENTITY_DATA, storedMobData);
        } else {
            writeView.putString(ITEM_KEY, activeStack().typeHolder().getRegisteredName());
            writeView.putInt(ITEM_QTY_KEY, activeStack().getCount());
            writeView.putInt(ITEM_STACKS_KEY, fullItemStacks);
            writeFluidData(writeView);
        }

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(writeView.buildResult()));

        if (customName != null) {
            stack.set(DataComponents.CUSTOM_NAME, customName);
        }

        return stack;
    }

    public TypedEntityData<EntityType<?>> getStoredMobData() {
        return storedMobData;
    }

    public String getStoredMobId() {
        return storedMobId;
    }

    public ItemStack getStoredItem() {
        return activeStack();
    }

    private ItemStack activeStack() {
        return items.getFirst();
    }

    private void setActiveStack(ItemStack stack) {
        items.set(0, stack);
    }

    public int getStoredStacks() {
        return fullItemStacks;
    }

    public Fluid getStoredFluid() {
        return storedFluid;
    }

    public double getStoredBuckets() {
        return fluidAmountInBuckets;
    }

    public boolean canAcceptFluid(Fluid fluid) {
        return canAcceptFluid(fluid, 1.0);
    }

    public boolean canAcceptFluid(Fluid fluid, double amount) {
        if (this.isEmpty()) {
            return true;
        }

        // We can't store multiple things at the same time
        if (this.hasItem() || this.hasMob()) {
            return false;
        }

        if (storedFluid.isSame(Fluids.EMPTY)) {
            return true;
        }

        // If this is the same fluid we're already storing, AND the jar isn't full yet
        return fluid.isSame(storedFluid) && (fluidAmountInBuckets + amount) <= MAX_BUCKETS;
    }

    public boolean tryInsert(Fluid fluid) {
        return tryInsert(fluid, 1.0);
    }

    public boolean tryInsert(Fluid fluid, double amount) {
        if (!canAcceptFluid(fluid, amount)) {
            return false;
        }

        storedFluid = fluid;
        fluidAmountInBuckets += amount;

        if (fluidAmountInBuckets > (MAX_BUCKETS - BOTTLE_SIZE)) {
            fluidAmountInBuckets = MAX_BUCKETS;
        }

        return true;
    }

    @Nullable
    public ItemStack getBottle() {
        if (!this.hasFluid()) {
            return null;
        }

        if (!this.hasFluid()) {
            return Items.GLASS_BOTTLE.getDefaultInstance();
        }

        ItemStack retStack = null;

        if (this.getStoredFluid() == Fluids.WATER) {
            fluidAmountInBuckets -= BOTTLE_SIZE;
            retStack = Items.POTION.getDefaultInstance();
        }

        if (this.getStoredFluid() == ModFluids.HONEY_FLUID) {
            fluidAmountInBuckets -= BOTTLE_SIZE;
            retStack = Items.HONEY_BOTTLE.getDefaultInstance();
        }

        if (fluidAmountInBuckets < BOTTLE_SIZE) {
            storedFluid = Fluids.EMPTY;
            fluidAmountInBuckets = 0;
        }

        return retStack;
    }

    public Fluid getBucket() {
        if (!this.hasFluid() || fluidAmountInBuckets < 1) {
            return Fluids.EMPTY;
        }

        Fluid fluid = storedFluid;
        fluidAmountInBuckets -= 1;

        if (fluidAmountInBuckets <= 0) {
            storedFluid = Fluids.EMPTY;
        }

        return fluid;
    }

    public boolean canAcceptItem(ItemStack item) {
        // We can't store multiple things at the same time
        if (this.hasFluid() || this.hasMob()) {
            return false;
        }

        if (!item.is(MinekeaItemTags.GLASS_JAR_STORABLE)) {
            return false;
        }

        if (this.isEmpty()) {
            return true;
        }

        // If this is the same item we're already storing, AND the jar isn't full yet
        ItemStack stored = activeStack();
        return item.is(stored.getItem()) && (fullItemStacks < MAX_ITEM_STACKS || stored.getCount() < stored.getMaxStackSize());
    }

    @Override
    public ItemStack tryInsert(ItemStack stack) {
        if (this.hasFluid() || this.hasMob()) {
            return stack;
        }

        // The jar was empty. Now it won't be
        if (activeStack().isEmpty()) {
            setActiveStack(stack.copy());

            return ItemStack.EMPTY;
        }

        ItemStack stored = activeStack();

        // We're full. No dice
        if (this.fullItemStacks == MAX_ITEM_STACKS && stored.getCount() == stored.getMaxStackSize()) {
            return stack;
        }

        // You can't insert different things
        if (!stack.is(stored.getItem())) {
            return stack;
        }

        int itemCount = stack.getCount();
        int storedItemCount = stored.getCount();

        // The stack coming in fits completely in the main inventory slot
        if (itemCount + storedItemCount <= stored.getMaxStackSize()) {
            stored.setCount(itemCount + storedItemCount);

            return ItemStack.EMPTY;
        }

        int remainder = (itemCount + storedItemCount) - stored.getMaxStackSize();

        // The stack coming in will fill up the main slot, plus a little overflow, but that's ok because we have room.
        if (this.fullItemStacks < MAX_ITEM_STACKS) {
            this.fullItemStacks += 1;

            stored.setCount(remainder);

            return ItemStack.EMPTY;
        }

        // At this point, we have MAX_ITEM_STACKS already stored, but a little space left in the "real" inventory slot
        stored.setCount(stored.getMaxStackSize());

        stack.setCount(remainder);

        return stack;
    }

    @Override
    public ItemStack removeStack() {
        if (!this.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = activeStack().copy();

        if (fullItemStacks > 1 || (fullItemStacks == 1 && stack.isStackable())) {
            stack.setCount(stack.getMaxStackSize());
            fullItemStacks -= 1;

            return stack;
        }

        setActiveStack(ItemStack.EMPTY);
        fullItemStacks = 0;

        return stack;
    }

    @Nullable
    public String getMobId() {
        if (this.hasMob()) {
            return storedMobId;
        }

        return null;
    }

    public boolean hasMob() {
        if (storedMobData == null) {
            return false;
        }

        return !storedMobData.copyTagWithoutId().isEmpty();
    }

    public boolean hasFluid() {
        return !storedFluid.isSame(Fluids.EMPTY);
    }

    public boolean hasItem() {
        if (!storedFluid.isSame(Fluids.EMPTY)) {
            return false;
        }

        return !activeStack().isEmpty();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput view) {
        super.saveAdditional(view);

        view.putString(ITEM_KEY, activeStack().typeHolder().getRegisteredName());
        view.putInt(ITEM_QTY_KEY, activeStack().getCount());
        view.putInt(ITEM_STACKS_KEY, fullItemStacks);

        writeFluidData(view);
        writeMobData(view);
        writeCustomNameData(view);
    }

    private void writeCustomNameData(ValueOutput view) {
        if (customName != null) {
            view.store(CUSTOM_NAME_KEY, ComponentSerialization.CODEC, customName);
        }

        view.putBoolean(HAS_CUSTOM_NAME_KEY, hasCustomName);
    }

    private void writeFluidData(ValueOutput view) {
        if (storedFluid.isSame(Fluids.EMPTY)) {
            view.putString(FLUID_KEY, "NONE");
            view.putDouble(FLUID_AMT_KEY, 0.0);

            return;
        }

        view.putString(FLUID_KEY, BuiltInRegistries.FLUID.getKey(storedFluid).toString());
        view.putDouble(FLUID_AMT_KEY, fluidAmountInBuckets);
    }

    private void writeMobData(ValueOutput view) {
        if (!hasMob()) {
            return;
        }

        CompoundTag nbt = storedMobData.copyTagWithoutId();
        nbt.putString("id", storedMobId);

        view.store("minecraft:entity_data", CompoundTag.CODEC, nbt);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput view) {
        super.loadAdditional(view);

        boolean hasFluid = readFluidData(view);
        boolean hasMob = readMobData(view);
        readCustomNameData(view);

        if (!hasFluid && !hasMob) {
            String storedItemKey = view.getStringOr(ITEM_KEY, "");
            if (!storedItemKey.isEmpty()) {
                ItemStack loaded = BuiltInRegistries.ITEM.getValue(Identifier.parse(storedItemKey)).getDefaultInstance();
                loaded.setCount(view.getIntOr(ITEM_QTY_KEY, 1));
                setActiveStack(loaded);
            }
            fullItemStacks = view.getIntOr(ITEM_STACKS_KEY, 0);
        }

        setChanged();
    }

    private void readCustomNameData(ValueInput view) {
        view.read(CUSTOM_NAME_KEY, ComponentSerialization.CODEC).ifPresent(name -> customName = name);
        hasCustomName = view.getBooleanOr(HAS_CUSTOM_NAME_KEY, hasCustomName);
    }

    private boolean readFluidData(ValueInput view) {
        String fluidKey = view.getStringOr(FLUID_KEY, "NONE");

        if (fluidKey.equals("NONE")) {
            storedFluid = Fluids.EMPTY;
            fluidAmountInBuckets = 0.0;

            return false;
        }

        storedFluid = BuiltInRegistries.FLUID.getValue(Identifier.parse(fluidKey));
        fluidAmountInBuckets = view.getDoubleOr(FLUID_AMT_KEY, 0.0);

        return true;
    }

    private boolean readMobData(ValueInput view) {
        CompoundTag entityNbt = view.read("minecraft:entity_data", CompoundTag.CODEC).orElse(null);

        if (entityNbt == null) {
            return false;
        }

        String id = entityNbt.getStringOr("id", "");

        if (id.isEmpty()) {
            return false;
        }

        Optional<EntityType<?>> entityType = Optional.ofNullable(Identifier.tryParse(id)).flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional);
        if (entityType.isEmpty()) {
            return false;
        }

        storedMobData = TypedEntityData.of(entityType.get(), entityNbt);
        storedMobId = id;

        return true;
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState oldState) {
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public @NonNull ItemStack getItem(int slot) {
        if (slot == STORAGE_SLOT) {
            return activeStack();
        }

        // The overflow slot is a write-only input that normally reads empty. When the jar is completely full,
        // report a full stack so a pushing hopper stops at vanilla's cheap isInventoryFull() early-out instead
        // of re-probing canPlaceItem every cooldown. It's blocked from extraction (see canTakeItem) and, being
        // a full stack, is never a valid merge target, so nothing can pull from it or duplicate through it.
        return isItemStorageFull() ? activeStack().copyWithCount(activeStack().getMaxStackSize()) : ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        if (hasFluid() || hasMob()) {
            return false;
        }

        return activeStack().isEmpty() && fullItemStacks == 0;
    }

    /**
     * Gate insertion through the jar's own rules so automation can't stuff in a disallowed item, a second item
     * type, or an item while the jar is holding a fluid or a captured mob.
     *
     * <p>The two slots accept differently. The active slot (0) is filled by a normal hopper merge, so a
     * partial fit is fine. The overflow slot (1) is reached only via {@link #setItem}, which can't report a
     * leftover, so it accepts an incoming stack only when the whole thing fits — otherwise the caller would
     * assume it was placed and delete the excess.
     */
    @Override
    public boolean canPlaceItem(int slot, @NonNull ItemStack stack) {
        if (slot == OVERFLOW_SLOT) {
            return canAcceptItem(stack) && stack.getCount() <= remainingItemCapacity(stack);
        }

        return canAcceptItem(stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, @NonNull ItemStack stack) {
        // Only the real active slot yields items. The overflow slot is input-only, and may momentarily read as
        // a phantom full stack (see getItem) purely to trip isInventoryFull — it must never be extracted.
        return slot == STORAGE_SLOT;
    }

    /**
     * Remove up to {@code count} items from the active slot. When that empties the active slot while
     * compressed reserve stacks remain, refill the active slot from the reserve so automation can drain the
     * whole jar, not just the top stack. See {@link #pendingCascadePutback} for why setItem has to cooperate.
     * The overflow slot is input-only and never yields anything.
     */
    @Override
    public @NonNull ItemStack removeItem(int slot, int count) {
        pendingCascadePutback = ItemStack.EMPTY;

        if (slot != STORAGE_SLOT) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = ContainerHelper.removeItem(items, STORAGE_SLOT, count);
        if (removed.isEmpty()) {
            return removed;
        }

        if (activeStack().isEmpty() && fullItemStacks > 0) {
            setActiveStack(removed.copyWithCount(removed.getMaxStackSize()));
            fullItemStacks -= 1;
            pendingCascadePutback = removed.copy();
        }

        setChanged();

        return removed;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int slot) {
        pendingCascadePutback = ItemStack.EMPTY;

        if (slot != STORAGE_SLOT) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = ContainerHelper.takeItem(items, STORAGE_SLOT);

        if (!removed.isEmpty() && activeStack().isEmpty() && fullItemStacks > 0) {
            setActiveStack(removed.copyWithCount(removed.getMaxStackSize()));
            fullItemStacks -= 1;
        }

        setChanged();

        return removed;
    }

    /**
     * Slot 0 replaces the active stack (honouring the post-extraction putback described on
     * {@link #pendingCascadePutback}). Slot 1 is the overflow input: route whatever a hopper drops there down
     * into the reserve via {@link #tryInsert}. {@link #canPlaceItem} guarantees the whole stack fits before we
     * get here, so nothing is dropped.
     */
    @Override
    public void setItem(int slot, @NonNull ItemStack stack) {
        if (slot == OVERFLOW_SLOT) {
            tryInsert(stack);
            setChanged();

            return;
        }

        if (!pendingCascadePutback.isEmpty()
            && ItemStack.isSameItemSameComponents(stack, pendingCascadePutback)
            && stack.getCount() == pendingCascadePutback.getCount()) {
            fullItemStacks += 1;
        }

        pendingCascadePutback = ItemStack.EMPTY;

        items.set(STORAGE_SLOT, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

        setChanged();
    }

    @Override
    public void clearContent() {
        setActiveStack(ItemStack.EMPTY);
        fullItemStacks = 0;
        pendingCascadePutback = ItemStack.EMPTY;

        setChanged();
    }

    /**
     * How many more of {@code stack} the jar can still hold across the active slot and the compressed reserve,
     * assuming {@code stack} matches what's stored (or the jar is empty). Used to reject overflow inserts that
     * wouldn't fit in full.
     */
    private int remainingItemCapacity(ItemStack stack) {
        int perStack = activeStack().isEmpty() ? stack.getMaxStackSize() : activeStack().getMaxStackSize();
        int used = activeStack().getCount() + (fullItemStacks * perStack);

        return ((MAX_ITEM_STACKS + 1) * perStack) - used;
    }

    /** Whether the jar's item storage is at capacity: the active stack is full and every reserve stack is used. */
    private boolean isItemStorageFull() {
        return !activeStack().isEmpty()
            && fullItemStacks == MAX_ITEM_STACKS
            && activeStack().getCount() >= activeStack().getMaxStackSize();
    }

    public void playEmptyBottleSound() {
        playSound(SoundEvents.BOTTLE_EMPTY);
    }

    public void playFillBottleSound() {
        playSound(SoundEvents.BOTTLE_FILL);
    }

    public void playEmptyBucketSound(Fluid fluid) {
        if (fluid == Fluids.LAVA || fluid == ModFluids.HONEY_FLUID) {
            playSound(SoundEvents.BUCKET_EMPTY_LAVA);
        } else {
            playSound(SoundEvents.BUCKET_EMPTY);
        }
    }

    public void playFillBucketSound(Fluid fluid) {
        if (fluid == Fluids.LAVA || fluid == ModFluids.HONEY_FLUID) {
            playSound(SoundEvents.BUCKET_FILL_LAVA);
        } else {
            playSound(SoundEvents.BUCKET_FILL);
        }
    }

    public void playAddItemSound() {
        playSound(SoundEvents.ITEM_FRAME_ADD_ITEM);
    }

    public void playRemoveItemSound() {
        playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
    }

    public void playSound(SoundEvent soundEvent) {
        if (this.level == null) {
            return;
        }

        this.level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public record OccupantData(TypedEntityData<EntityType<?>> entityData) {
        public static final Codec<OccupantData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(TypedEntityData.codec(EntityType.CODEC).fieldOf("entity_data").forGetter(OccupantData::entityData)).apply(instance, OccupantData::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, OccupantData> PACKET_CODEC;

        public static OccupantData of(Entity entity) {
            OccupantData data;
            try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
                TagValueOutput nbtWriteView = TagValueOutput.createWithContext(logging, entity.registryAccess());
                entity.save(nbtWriteView);
                Objects.requireNonNull(nbtWriteView);
                CompoundTag nbtCompound = nbtWriteView.buildResult();

                data = new OccupantData(TypedEntityData.of(entity.getType(), nbtCompound));
            }

            return data;
        }

        static {
            PACKET_CODEC = StreamCodec.composite(TypedEntityData.streamCodec(EntityType.STREAM_CODEC), OccupantData::entityData, OccupantData::new);
        }
    }
}
