package com.chimericdream.archaeologytweaks.block.entity;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;

public class ATBrushableBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private int brushesCount;
    private long nextDustTime;
    private long nextBrushTime;
    private ItemStack item;
    @Nullable
    private Direction hitDirection;
    @Nullable
    private RegistryKey<LootTable> lootTable;
    private long lootTableSeed;

    public ATBrushableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BRUSHABLE_MOD_BLOCK_ENTITY.get(), pos, state);
        this.item = ItemStack.EMPTY;
    }

    public boolean brush(long worldTime, ServerWorld world, LivingEntity brusher, Direction hitDirection, ItemStack brush) {
        if (this.hitDirection == null) {
            this.hitDirection = hitDirection;
        }

        this.nextDustTime = worldTime + 40L;
        if (worldTime < this.nextBrushTime) {
            return false;
        } else {
            this.nextBrushTime = worldTime + 10L;
            this.generateItem(world, brusher, brush);
            int i = this.getDustedLevel();
            if (++this.brushesCount >= 10) {
                this.finishBrushing(world, brusher, brush);
                return true;
            }
            world.scheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), 2);
            int j = this.getDustedLevel();
            if (i != j) {
                BlockState blockState = this.getCachedState();
                BlockState blockState2 = blockState.with(Properties.DUSTED, j);
                world.setBlockState(this.getPos(), blockState2, 3);
            }

            return false;
        }
    }

    public void setItem(ItemStack item) {
        this.item = item;
        this.lootTable = null;
        this.markDirty();
    }

    private void generateItem(ServerWorld world, LivingEntity brusher, ItemStack brush) {
        if (this.lootTable != null) {
            LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(this.lootTable);
            if (brusher instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger(serverPlayerEntity, this.lootTable);
            }

            LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos)).luck(brusher.getLuck()).add(LootContextParameters.THIS_ENTITY, brusher).add(LootContextParameters.TOOL, brush).build(LootContextTypes.ARCHAEOLOGY);
            ObjectArrayList<ItemStack> objectArrayList = lootTable.generateLoot(lootWorldContext, this.lootTableSeed);

            this.item = switch (objectArrayList.size()) {
                case 0 -> ItemStack.EMPTY;
                case 1 -> objectArrayList.getFirst();
                default -> {
                    LOGGER.warn("Expected max 1 loot from loot table {}, but got {}", this.lootTable.getValue(), objectArrayList.size());
                    yield objectArrayList.getFirst();
                }
            };

            this.lootTable = null;
            this.markDirty();
        }
    }

    private void finishBrushing(ServerWorld world, LivingEntity brusher, ItemStack brush) {
        this.spawnItem(world, brusher, brush);
        BlockState blockState = this.getCachedState();
        world.syncWorldEvent(3008, this.getPos(), Block.getRawIdFromState(blockState));
        Block block = this.getCachedState().getBlock();
        Block block2;
        if (block instanceof BrushableBlock brushableBlock) {
            block2 = brushableBlock.getBaseBlock();
        } else {
            block2 = Blocks.AIR;
        }

        world.setBlockState(this.pos, block2.getDefaultState(), 3);
    }

    private void spawnItem(ServerWorld world, LivingEntity brusher, ItemStack brush) {
        this.generateItem(world, brusher, brush);
        if (!this.item.isEmpty()) {
            double d = EntityType.ITEM.getWidth();
            double e = (double) 1.0F - d;
            double f = d / (double) 2.0F;
            Direction direction = Objects.requireNonNullElse(this.hitDirection, Direction.UP);
            BlockPos blockPos = this.pos.offset(direction, 1);
            double g = (double) blockPos.getX() + (double) 0.5F * e + f;
            double h = (double) blockPos.getY() + (double) 0.5F + (double) (EntityType.ITEM.getHeight() / 2.0F);
            double i = (double) blockPos.getZ() + (double) 0.5F * e + f;
            ItemEntity itemEntity = new ItemEntity(world, g, h, i, this.item.split(world.random.nextInt(21) + 10));
            itemEntity.setVelocity(Vec3d.ZERO);
            world.spawnEntity(itemEntity);
            this.item = ItemStack.EMPTY;
        }

    }

    public void scheduledTick(ServerWorld world) {
        if (this.brushesCount != 0 && world.getTime() >= this.nextDustTime) {
            int i = this.getDustedLevel();
            this.brushesCount = Math.max(0, this.brushesCount - 2);
            int j = this.getDustedLevel();
            if (i != j) {
                world.setBlockState(this.getPos(), this.getCachedState().with(Properties.DUSTED, j), 3);
            }

            int k = 4;
            this.nextDustTime = world.getTime() + 4L;
        }

        if (this.brushesCount == 0) {
            this.hitDirection = null;
            this.nextDustTime = 0L;
            this.nextBrushTime = 0L;
        } else {
            world.scheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), 2);
        }

    }

    private boolean readLootTableFromData(ReadView data) {
        this.lootTable = data.read("LootTable", LootTable.TABLE_KEY).orElse(null);
        this.lootTableSeed = data.getLong("LootTableSeed", 0L);
        return this.lootTable != null;
    }

    private boolean writeLootTableToData(WriteView data) {
        if (this.lootTable == null) {
            return false;
        }

        data.put("LootTable", LootTable.TABLE_KEY, this.lootTable);
        if (this.lootTableSeed != 0L) {
            data.putLong("LootTableSeed", this.lootTableSeed);
        }

        return true;
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt(registries);
        nbtCompound.putNullable("hit_direction", Direction.INDEX_CODEC, this.hitDirection);
        if (!this.item.isEmpty()) {
            RegistryOps<NbtElement> registryOps = registries.getOps(NbtOps.INSTANCE);
            nbtCompound.put("item", ItemStack.CODEC, registryOps, this.item);
        }

        return nbtCompound;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void readData(ReadView data) {
        super.readData(data);
        if (!this.readLootTableFromData(data)) {
            this.item = (ItemStack) data.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        } else {
            this.item = ItemStack.EMPTY;
        }

        this.hitDirection = (Direction) data.read("hit_direction", Direction.INDEX_CODEC).orElse(null);
    }

    protected void writeData(WriteView data) {
        super.writeData(data);
        if (!this.writeLootTableToData(data) && !this.item.isEmpty()) {
            data.put("item", ItemStack.CODEC, this.item);
        }
    }

    public void setLootTable(RegistryKey<LootTable> lootTable, long seed) {
        this.lootTable = lootTable;
        this.lootTableSeed = seed;
    }

    private int getDustedLevel() {
        if (this.brushesCount == 0) {
            return 0;
        } else if (this.brushesCount < 3) {
            return 1;
        } else {
            return this.brushesCount < 6 ? 2 : 3;
        }
    }

    @Nullable
    public Direction getHitDirection() {
        return this.hitDirection;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
