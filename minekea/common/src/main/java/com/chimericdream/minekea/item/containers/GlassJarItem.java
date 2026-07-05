package com.chimericdream.minekea.item.containers;

import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class GlassJarItem extends BlockItem {
    public GlassJarItem(RegistrySupplier<Block> block, Properties settings) {
        super(block.get(), settings.setId(REGISTRY_HELPER.makeItemRegistryKey(GlassJarBlock.BLOCK_ID)));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Mob mob && canCaptureMob(mob) && !hasStoredMob(stack)) {
            mob.stopRiding();
            mob.ejectPassengers();

            ItemStack newStack = new ItemStack(this);
            newStack.setCount(1);

            TypedEntityData<EntityType<?>> entityData = GlassJarBlockEntity.OccupantData.of(mob).entityData();
            newStack.set(DataComponents.ENTITY_DATA, entityData);

            CompoundTag hasCustomNameNbt = new CompoundTag();
            if (mob.hasCustomName()) {
                hasCustomNameNbt.putBoolean("HasCustomName", true);
                newStack.set(DataComponents.CUSTOM_NAME, mob.getCustomName());
            } else {
                hasCustomNameNbt.putBoolean("HasCustomName", false);
                newStack.set(DataComponents.CUSTOM_NAME, getDefaultName(mob));
            }
            newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(hasCustomNameNbt));

            player.addItem(newStack);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            mob.discard();

            Level world = player.level();
            BlockPos blockPos = player.blockPosition();

            world.playSound(
                null,
                (double) blockPos.getX() + 0.5,
                (double) blockPos.getY() + 0.5,
                (double) blockPos.getZ() + 0.5,
                SoundEvents.ITEM_PICKUP,
                SoundSource.BLOCKS,
                0.5F,
                0.5F
            );

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        Level world = context.getLevel();
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown() && hasStoredMob(stack)) {
            BlockPos releasePos = this.getReleasePosition(context);

            this.releaseContents(context.getPlayer(), (ServerLevel) world, stack, releasePos);

            return InteractionResult.CONSUME;
        }

        return this.place(new BlockPlaceContext(context));
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        if (!this.getBlock().isEnabled(context.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        }

        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        }

        BlockPlaceContext itemPlacementContext = this.updatePlacementContext(context);
        if (itemPlacementContext == null) {
            return InteractionResult.FAIL;
        }

        BlockState blockState = this.getPlacementState(itemPlacementContext);
        if (blockState == null) {
            return InteractionResult.FAIL;
        }

        if (!this.placeBlock(itemPlacementContext, blockState)) {
            return InteractionResult.FAIL;
        }

        BlockPos blockPos = itemPlacementContext.getClickedPos();
        Level world = itemPlacementContext.getLevel();
        Player playerEntity = itemPlacementContext.getPlayer();
        ItemStack itemStack = itemPlacementContext.getItemInHand();
        BlockState blockState2 = world.getBlockState(blockPos);

        if (blockState2.is(blockState.getBlock())) {
            blockState2 = this.updateBlockStateFromTag(blockPos, world, itemStack, blockState2);
            this.updateCustomBlockEntityTag(blockPos, world, playerEntity, itemStack, blockState2);
            updateBlockEntityComponents(world, blockPos, itemStack);
            blockState2.getBlock().setPlacedBy(world, blockPos, blockState2, playerEntity, itemStack);
            if (playerEntity instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerEntity, blockPos, itemStack);
            }
        }

        SoundType blockSoundGroup = blockState2.getSoundType();
        world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundSource.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
        world.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(playerEntity, blockState2));
        itemStack.consume(1, playerEntity);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    private BlockState updateBlockStateFromTag(BlockPos pos, Level world, ItemStack stack, BlockState state) {
        BlockItemStateProperties blockStateComponent = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        if (blockStateComponent.isEmpty()) {
            return state;
        }

        BlockState blockState = blockStateComponent.apply(state);
        if (blockState != state) {
            world.setBlock(pos, blockState, 2);
        }

        return blockState;
    }

    private static void updateBlockEntityComponents(Level world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity != null) {
            blockEntity.applyComponentsFromItemStack(stack);
            blockEntity.setChanged();
        }
    }

    private BlockPos getReleasePosition(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockPos);
        if (blockState.getCollisionShape(context.getLevel(), blockPos).isEmpty()) {
            return blockPos;
        } else {
            return blockPos.relative(context.getClickedFace());
        }
    }

    public void releaseContents(@Nullable Player player, ServerLevel world, ItemStack stack, BlockPos pos) {
        TypedEntityData<EntityType<?>> entityData = stack.get(DataComponents.ENTITY_DATA);

        if (entityData == null) {
            return;
        }

        EntityType<?> entityType = entityData.type();

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt = customData == null ? new CompoundTag() : customData.copyTag();

        ItemStack itemCopy = stack.copy();
        boolean hasCustomName = nbt.getBooleanOr("HasCustomName", false);
        if (!hasCustomName) {
            itemCopy.remove(DataComponents.CUSTOM_NAME);
        }

        Entity entity = entityType.create(
            world,
            EntityType.createDefaultStackConfig(world, itemCopy, null),
            pos,
            EntitySpawnReason.BUCKET,
            true,
            false
        );

        if (entity != null) {
            world.addFreshEntity(entity);
            world.playSound(
                null,
                (double) pos.getX() + 0.5,
                (double) pos.getY() + 0.5,
                (double) pos.getZ() + 0.5,
                SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                SoundSource.BLOCKS,
                0.5F,
                0.5F
            );
            world.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
        }

        stack.shrink(1);

        if (player != null) {
            ItemStack newStack = new ItemStack(this);
            newStack.setCount(1);
            player.addItem(newStack);
        }
    }

    private Component getDefaultName(Mob mob) {
        return Component.nullToEmpty(String.format("%s in a jar", mob.getName().getString()));
    }

    public boolean canCaptureMob(Mob entity) {
        if (!entity.isAlive()) {
            return false;
        }

        if (entity instanceof Slime) {
            return ((Slime) entity).isTiny();
        }

        return entity instanceof Bee
            || entity instanceof Vex
            || entity instanceof Allay
            || entity instanceof Silverfish
            || entity instanceof Endermite
            || entity instanceof Bat;
    }

    public boolean hasStoredMob(ItemStack stack) {
        return stack.get(DataComponents.ENTITY_DATA) != null;
    }
}
