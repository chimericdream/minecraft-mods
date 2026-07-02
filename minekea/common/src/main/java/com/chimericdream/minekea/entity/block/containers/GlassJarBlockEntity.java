package com.chimericdream.minekea.entity.block.containers;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.block.containers.ContainerBlocks;
import com.chimericdream.minekea.fluid.ModFluids;
import com.chimericdream.minekea.item.containers.ContainerItems;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
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

    private ItemStack storedItem = ItemStack.EMPTY;
    private int fullItemStacks = 0;

    private Fluid storedFluid = Fluids.EMPTY;
    private double fluidAmountInBuckets = 0.0;

    private TypedEntityData<EntityType<?>> storedMobData = null;
    private String storedMobId = null;

    public static final String ITEM_KEY = "StoredItem";
    public static final String ITEM_QTY_KEY = "StoredItemQty";
    public static final String ITEM_STACKS_KEY = "FullItemStacks";
    public static final String FLUID_KEY = "StoredFluid";
    public static final String FLUID_AMT_KEY = "StoredFluidAmount";
    public static final String MOB_DATA_KEY = "StoredMobData";

    public GlassJarBlockEntity(BlockPos pos, BlockState state) {
        this(ContainerBlocks.GLASS_JAR_BLOCK_ENTITY.get(), pos, state);
    }

    public GlassJarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void readDataFromItemStack(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        CompoundTag nbt = customData.copyTag();
        this.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, VanillaRegistries.createLookup(), nbt));
    }

    public static GlassJarBlockEntity fromItemStack(ItemStack stack, ClientLevel world) {
        GlassJarBlockEntity entity = new GlassJarBlockEntity(ContainerBlocks.GLASS_JAR_BLOCK_ENTITY.get(), BlockPos.ZERO, ContainerBlocks.GLASS_JAR.get().defaultBlockState());

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return entity;
        }

        CompoundTag nbt = customData.copyTag();
        entity.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, VanillaRegistries.createLookup(), nbt));

        return entity;
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(ContainerItems.GLASS_JAR_ITEM.get());
        TagValueOutput writeView = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        this.saveAdditional(writeView);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(writeView.buildResult()));

        return stack;
    }

    public TypedEntityData<EntityType<?>> getStoredMobData() {
        return storedMobData;
    }

    public String getStoredMobId() {
        return storedMobId;
    }

    public ItemStack getStoredItem() {
        return storedItem;
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
        return item.is(storedItem.getItem()) && (fullItemStacks < MAX_ITEM_STACKS || storedItem.getCount() < storedItem.getMaxStackSize());
    }

    @Override
    public ItemStack tryInsert(ItemStack stack) {
        if (this.hasFluid() || this.hasMob()) {
            return stack;
        }

        // The jar was empty. Now it won't be
        if (storedItem.isEmpty()) {
            storedItem = stack.copy();

            return ItemStack.EMPTY;
        }

        // We're full. No dice
        if (this.fullItemStacks == MAX_ITEM_STACKS && storedItem.getCount() == storedItem.getMaxStackSize()) {
            return stack;
        }

        // You can't insert different things
        if (!stack.is(storedItem.getItem())) {
            return stack;
        }

        int itemCount = stack.getCount();
        int storedItemCount = storedItem.getCount();

        // The stack coming in fits completely in the main inventory slot
        if (itemCount + storedItemCount <= storedItem.getMaxStackSize()) {
            storedItem.setCount(itemCount + storedItemCount);

            return ItemStack.EMPTY;
        }

        int remainder = (itemCount + storedItemCount) - storedItem.getMaxStackSize();

        // The stack coming in will fill up the main slot, plus a little overflow, but that's ok because we have room.
        if (this.fullItemStacks < MAX_ITEM_STACKS) {
            this.fullItemStacks += 1;

            storedItem.setCount(remainder);

            return ItemStack.EMPTY;
        }

        // At this point, we have MAX_ITEM_STACKS already stored, but a little space left in the "real" inventory slot
        storedItem.setCount(storedItem.getMaxStackSize());

        stack.setCount(remainder);

        return stack;
    }

    @Override
    public ItemStack removeStack() {
        if (!this.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = storedItem.copy();

        if (fullItemStacks > 1 || (fullItemStacks == 1 && stack.isStackable())) {
            stack.setCount(stack.getMaxStackSize());
            fullItemStacks -= 1;

            return stack;
        }

        storedItem = ItemStack.EMPTY;
        fullItemStacks = 0;

        return stack;
    }

//    public DefaultedList<ItemStack> getItemsOnBreak() {
//        DefaultedList<ItemStack> stacks = DefaultedList.of();
//
//        int i = 0;
//
//        while (i < fullItemStacks) {
//            ItemStack stack = this.items.getFirst().copy();
//            stack.setCount(stack.getMaxCount());
//
//            stacks.add(stack);
//            i++;
//        }
//
//        stacks.add(this.items.getFirst());
//
//        return stacks;
//    }

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

        return !storedItem.isEmpty();
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putString(ITEM_KEY, storedItem.getItemHolder().getRegisteredName());
        view.putInt(ITEM_QTY_KEY, storedItem.getCount());
        view.putInt(ITEM_STACKS_KEY, fullItemStacks);

        writeFluidData(view);
        writeMobData(view);
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
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        boolean hasFluid = readFluidData(view);
        boolean hasMob = readMobData(view);

        if (!hasFluid && !hasMob) {
            String storedItemKey = view.getStringOr(ITEM_KEY, null);
            if (storedItemKey != null) {
                storedItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(storedItemKey)).getDefaultInstance();
                storedItem.setCount(view.getIntOr(ITEM_QTY_KEY, 1));
            }
            fullItemStacks = view.getIntOr(ITEM_STACKS_KEY, 0);
        }

        setChanged();
    }

    private boolean readFluidData(ValueInput view) {
        String fluidKey = view.getStringOr(FLUID_KEY, "NONE");

        if (fluidKey.equals("NONE")) {
            storedFluid = Fluids.EMPTY;
            fluidAmountInBuckets = 0.0;

            return false;
        }

        storedFluid = BuiltInRegistries.FLUID.getValue(ResourceLocation.parse(fluidKey));
        fluidAmountInBuckets = view.getDoubleOr(FLUID_AMT_KEY, 0.0);

        return true;
    }

    private boolean readMobData(ValueInput view) {
        CompoundTag components = view.read("components", CompoundTag.CODEC).orElse(null);
        if (components == null) {
            return false;
        }

        CompoundTag entityNbt = components.read("minecraft:entity_data", CompoundTag.CODEC).orElse(null);

        if (entityNbt == null) {
            return false;
        }

        String id = entityNbt.getStringOr("id", null);

        if (id == null) {
            return false;
        }

        Optional<EntityType<?>> entityType = EntityType.byString(id);
        if (entityType.isEmpty()) {
            return false;
        }

        storedMobData = TypedEntityData.of(entityType.get(), entityNbt);
        storedMobId = id;

        return true;
    }

//    public void readMobNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//        storedMobData = nbt;
//    }
//
//    @Override
//    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//        Inventories.writeNbt(nbt, items, registryLookup);
//        nbt.putInt(ITEM_AMT_KEY, fullItemStacks);
//
//        writeFluidNbt(nbt, registryLookup);
//        writeMobNbt(nbt, registryLookup);
//
//        super.writeNbt(nbt, registryLookup);
//    }
//
//    private void writeFluidNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//        if (storedFluid.matchesType(Fluids.EMPTY)) {
//            nbt.putString(FLUID_KEY, "NONE");
//            nbt.putDouble(FLUID_AMT_KEY, 0.0);
//
//            return;
//        }
//
//        nbt.putString(FLUID_KEY, Registries.FLUID.getId(storedFluid).toString());
//        nbt.putDouble(FLUID_AMT_KEY, fluidAmountInBuckets);
//    }
//
//    public void writeMobNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//        nbt.put(MOB_DATA_KEY, storedMobData.copyNbtWithoutId());
//    }

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
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        if (hasFluid() || hasMob()) {
            return false;
        }

        return storedItem.isEmpty();
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

//        public static OccupantData create() {
//            return new OccupantData(TypedEntityData.create(EntityType.BEE, new NbtCompound()));
//        }

//        @Nullable
//        public Entity loadEntity(World world, BlockPos pos) {
//            NbtCompound nbtCompound = this.entityData.copyNbtWithoutId();
//            Objects.requireNonNull(nbtCompound);
//            Entity entity = EntityType.loadEntityWithPassengers(this.entityData.getType(), nbtCompound, world, SpawnReason.LOAD, (entityx) -> entityx);
//
//            if (entity != null) {
//                return entity;
//            } else {
//                return null;
//            }
//        }

        static {
            PACKET_CODEC = StreamCodec.composite(TypedEntityData.streamCodec(EntityType.STREAM_CODEC), OccupantData::entityData, OccupantData::new);
        }
    }
}
