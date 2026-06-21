package com.chimericdream.minekea.item.containers;

import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class GlassJarItem extends BlockItem {
    public GlassJarItem(RegistrySupplier<Block> block, Settings settings) {
        super(block.get(), settings.registryKey(REGISTRY_HELPER.makeItemRegistryKey(GlassJarBlock.BLOCK_ID)));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (entity instanceof MobEntity mob && canCaptureMob(mob) && !hasStoredMob(stack)) {
            mob.stopRiding();
            mob.removeAllPassengers();

            ItemStack newStack = new ItemStack(this);
            newStack.setCount(1);

            TypedEntityData<EntityType<?>> entityData = GlassJarBlockEntity.OccupantData.of(mob).entityData();
            newStack.set(DataComponentTypes.ENTITY_DATA, entityData);

            NbtCompound hasCustomNameNbt = new NbtCompound();
            if (mob.hasCustomName()) {
                hasCustomNameNbt.putBoolean("HasCustomName", true);
                newStack.set(DataComponentTypes.CUSTOM_NAME, mob.getCustomName());
            } else {
                hasCustomNameNbt.putBoolean("HasCustomName", false);
                newStack.set(DataComponentTypes.CUSTOM_NAME, getDefaultName(mob));
            }
            newStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(hasCustomNameNbt));

            player.giveItemStack(newStack);
            if (!player.isCreative()) {
                stack.decrement(1);
            }

            mob.discard();

            World world = player.getEntityWorld();
            BlockPos blockPos = player.getBlockPos();

            world.playSound(
                null,
                (double) blockPos.getX() + 0.5,
                (double) blockPos.getY() + 0.5,
                (double) blockPos.getZ() + 0.5,
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.BLOCKS,
                0.5F,
                0.5F
            );

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();

        World world = context.getWorld();
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (context.getPlayer() != null && context.getPlayer().isSneaking() && hasStoredMob(stack)) {
            BlockPos releasePos = this.getReleasePosition(context);

            this.releaseContents(context.getPlayer(), (ServerWorld) world, stack, releasePos);

            return ActionResult.CONSUME;
        }

        return this.place(new ItemPlacementContext(context));
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        if (!this.getBlock().isEnabled(context.getWorld().getEnabledFeatures())) {
            return ActionResult.FAIL;
        }

        if (!context.canPlace()) {
            return ActionResult.FAIL;
        }

        ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
        if (itemPlacementContext == null) {
            return ActionResult.FAIL;
        }

        BlockState blockState = this.getPlacementState(itemPlacementContext);
        if (blockState == null) {
            return ActionResult.FAIL;
        }

        if (!this.place(itemPlacementContext, blockState)) {
            return ActionResult.FAIL;
        }

        BlockPos blockPos = itemPlacementContext.getBlockPos();
        World world = itemPlacementContext.getWorld();
        PlayerEntity playerEntity = itemPlacementContext.getPlayer();
        ItemStack itemStack = itemPlacementContext.getStack();
        BlockState blockState2 = world.getBlockState(blockPos);

        if (blockState2.isOf(blockState.getBlock())) {
            blockState2 = this.placeFromNbt(blockPos, world, itemStack, blockState2);
            this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
            copyComponentsToBlockEntity(world, blockPos, itemStack);
            blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) playerEntity, blockPos, itemStack);
            }
        }

        BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
        world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
        world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));
        itemStack.decrementUnlessCreative(1, playerEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
    }

    private BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockStateComponent blockStateComponent = stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
        if (blockStateComponent.isEmpty()) {
            return state;
        }

        BlockState blockState = blockStateComponent.applyToState(state);
        if (blockState != state) {
            world.setBlockState(pos, blockState, 2);
        }

        return blockState;
    }

    private static void copyComponentsToBlockEntity(World world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity != null) {
            blockEntity.readComponents(stack);
            blockEntity.markDirty();
        }
    }

    private BlockPos getReleasePosition(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = context.getWorld().getBlockState(blockPos);
        if (blockState.getCollisionShape(context.getWorld(), blockPos).isEmpty()) {
            return blockPos;
        } else {
            return blockPos.offset(context.getSide());
        }
    }

    public void releaseContents(@Nullable PlayerEntity player, ServerWorld world, ItemStack stack, BlockPos pos) {
        TypedEntityData<EntityType<?>> entityData = stack.get(DataComponentTypes.ENTITY_DATA);

        if (entityData == null) {
            return;
        }

        EntityType<?> entityType = entityData.getType();

        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = customData == null ? new NbtCompound() : customData.copyNbt();

        ItemStack itemCopy = stack.copy();
        boolean hasCustomName = nbt.getBoolean("HasCustomName", false);
        if (!hasCustomName) {
            itemCopy.remove(DataComponentTypes.CUSTOM_NAME);
        }

        Entity entity = entityType.create(
            world,
            EntityType.copier(world, itemCopy, null),
            pos,
            SpawnReason.BUCKET,
            true,
            false
        );

        if (entity != null) {
            world.spawnEntity(entity);
            world.playSound(
                null,
                (double) pos.getX() + 0.5,
                (double) pos.getY() + 0.5,
                (double) pos.getZ() + 0.5,
                SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM,
                SoundCategory.BLOCKS,
                0.5F,
                0.5F
            );
            world.emitGameEvent(player, GameEvent.ENTITY_PLACE, pos);
        }

        stack.decrement(1);

        if (player != null) {
            ItemStack newStack = new ItemStack(this);
            newStack.setCount(1);
            player.giveItemStack(newStack);
        }
    }

    private Text getDefaultName(MobEntity mob) {
        return Text.of(String.format("%s in a jar", mob.getName().getString()));
    }

    public boolean canCaptureMob(MobEntity entity) {
        if (!entity.isAlive()) {
            return false;
        }

        if (entity instanceof SlimeEntity) {
            return ((SlimeEntity) entity).isSmall();
        }

        return entity instanceof BeeEntity
            || entity instanceof VexEntity
            || entity instanceof AllayEntity
            || entity instanceof SilverfishEntity
            || entity instanceof EndermiteEntity
            || entity instanceof BatEntity;
    }

    public boolean hasStoredMob(ItemStack stack) {
        return stack.get(DataComponentTypes.ENTITY_DATA) != null;
    }
}
