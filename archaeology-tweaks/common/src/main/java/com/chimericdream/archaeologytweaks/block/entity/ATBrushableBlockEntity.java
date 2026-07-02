package com.chimericdream.archaeologytweaks.block.entity;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
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
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;

    public ATBrushableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BRUSHABLE_MOD_BLOCK_ENTITY.get(), pos, state);
        this.item = ItemStack.EMPTY;
    }

    public boolean brush(long worldTime, ServerLevel world, LivingEntity brusher, Direction hitDirection, ItemStack brush) {
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
            world.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
            int j = this.getDustedLevel();
            if (i != j) {
                BlockState blockState = this.getBlockState();
                BlockState blockState2 = blockState.setValue(BlockStateProperties.DUSTED, j);
                world.setBlock(this.getBlockPos(), blockState2, 3);
            }

            return false;
        }
    }

    public void setItem(ItemStack item) {
        this.item = item;
        this.lootTable = null;
        this.setChanged();
    }

    private void generateItem(ServerLevel world, LivingEntity brusher, ItemStack brush) {
        if (this.lootTable != null) {
            LootTable lootTable = world.getServer().reloadableRegistries().getLootTable(this.lootTable);
            if (brusher instanceof ServerPlayer serverPlayerEntity) {
                CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayerEntity, this.lootTable);
            }

            LootParams lootWorldContext = (new LootParams.Builder(world)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withLuck(brusher.getLuck()).withParameter(LootContextParams.THIS_ENTITY, brusher).withParameter(LootContextParams.TOOL, brush).create(LootContextParamSets.ARCHAEOLOGY);
            ObjectArrayList<ItemStack> objectArrayList = lootTable.getRandomItems(lootWorldContext, this.lootTableSeed);

            this.item = switch (objectArrayList.size()) {
                case 0 -> ItemStack.EMPTY;
                case 1 -> objectArrayList.getFirst();
                default -> {
                    LOGGER.warn("Expected max 1 loot from loot table {}, but got {}", this.lootTable.location(), objectArrayList.size());
                    yield objectArrayList.getFirst();
                }
            };

            this.lootTable = null;
            this.setChanged();
        }
    }

    private void finishBrushing(ServerLevel world, LivingEntity brusher, ItemStack brush) {
        this.spawnItem(world, brusher, brush);
        BlockState blockState = this.getBlockState();
        world.levelEvent(3008, this.getBlockPos(), Block.getId(blockState));
        Block block = this.getBlockState().getBlock();
        Block block2;
        if (block instanceof BrushableBlock brushableBlock) {
            block2 = brushableBlock.getTurnsInto();
        } else {
            block2 = Blocks.AIR;
        }

        world.setBlock(this.worldPosition, block2.defaultBlockState(), 3);
    }

    private void spawnItem(ServerLevel world, LivingEntity brusher, ItemStack brush) {
        this.generateItem(world, brusher, brush);
        if (!this.item.isEmpty()) {
            double d = EntityType.ITEM.getWidth();
            double e = (double) 1.0F - d;
            double f = d / (double) 2.0F;
            Direction direction = Objects.requireNonNullElse(this.hitDirection, Direction.UP);
            BlockPos blockPos = this.worldPosition.relative(direction, 1);
            double g = (double) blockPos.getX() + (double) 0.5F * e + f;
            double h = (double) blockPos.getY() + (double) 0.5F + (double) (EntityType.ITEM.getHeight() / 2.0F);
            double i = (double) blockPos.getZ() + (double) 0.5F * e + f;
            ItemEntity itemEntity = new ItemEntity(world, g, h, i, this.item.split(world.random.nextInt(21) + 10));
            itemEntity.setDeltaMovement(Vec3.ZERO);
            world.addFreshEntity(itemEntity);
            this.item = ItemStack.EMPTY;
        }

    }

    public void scheduledTick(ServerLevel world) {
        if (this.brushesCount != 0 && world.getGameTime() >= this.nextDustTime) {
            int i = this.getDustedLevel();
            this.brushesCount = Math.max(0, this.brushesCount - 2);
            int j = this.getDustedLevel();
            if (i != j) {
                world.setBlock(this.getBlockPos(), this.getBlockState().setValue(BlockStateProperties.DUSTED, j), 3);
            }

            int k = 4;
            this.nextDustTime = world.getGameTime() + 4L;
        }

        if (this.brushesCount == 0) {
            this.hitDirection = null;
            this.nextDustTime = 0L;
            this.nextBrushTime = 0L;
        } else {
            world.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
        }

    }

    private boolean readLootTableFromData(ValueInput data) {
        this.lootTable = data.read("LootTable", LootTable.KEY_CODEC).orElse(null);
        this.lootTableSeed = data.getLongOr("LootTableSeed", 0L);
        return this.lootTable != null;
    }

    private boolean writeLootTableToData(ValueOutput data) {
        if (this.lootTable == null) {
            return false;
        }

        data.store("LootTable", LootTable.KEY_CODEC, this.lootTable);
        if (this.lootTableSeed != 0L) {
            data.putLong("LootTableSeed", this.lootTableSeed);
        }

        return true;
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbtCompound = super.getUpdateTag(registries);
        nbtCompound.storeNullable("hit_direction", Direction.LEGACY_ID_CODEC, this.hitDirection);
        if (!this.item.isEmpty()) {
            RegistryOps<Tag> registryOps = registries.createSerializationContext(NbtOps.INSTANCE);
            nbtCompound.store("item", ItemStack.CODEC, registryOps, this.item);
        }

        return nbtCompound;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void loadAdditional(ValueInput data) {
        super.loadAdditional(data);
        if (!this.readLootTableFromData(data)) {
            this.item = (ItemStack) data.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        } else {
            this.item = ItemStack.EMPTY;
        }

        this.hitDirection = (Direction) data.read("hit_direction", Direction.LEGACY_ID_CODEC).orElse(null);
    }

    protected void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        if (!this.writeLootTableToData(data) && !this.item.isEmpty()) {
            data.store("item", ItemStack.CODEC, this.item);
        }
    }

    public void setLootTable(ResourceKey<LootTable> lootTable, long seed) {
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
