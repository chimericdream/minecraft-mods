package com.chimericdream.bettertargetdummies.block.entity;

import com.chimericdream.bettertargetdummies.ModInfo;
import com.chimericdream.bettertargetdummies.block.ModBlocks;
import com.chimericdream.bettertargetdummies.block.TargetDummyBlock;
import com.chimericdream.bettertargetdummies.entity.TargetDummyMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TargetDummyBlockEntity extends BlockEntity {
    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "target_dummy");

    @Nullable
    private UUID boundEntityId;
    @Nullable
    private EntityType<?> boundEntityType;
    private Direction boundFacing = Direction.NORTH;

    public TargetDummyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TARGET_DUMMY_BLOCK_ENTITY.get(), pos, state);
    }

    /**
     * Remembers {@code type} as the bound mob and, if the dummy is currently powered, spawns it
     * immediately. If it isn't powered, the binding is only remembered — it spawns the next time a
     * redstone signal turns the dummy on.
     *
     * <p>The mob's starting facing matches the player's position relative to the block (e.g. a
     * player standing on the north side binds a mob that starts out facing north), snapped to the
     * nearest cardinal direction.
     */
    public boolean bindMob(ServerLevel level, EntityType<?> type, @Nullable ItemStack spawnEggStack, Player player) {
        discardLiveEntity(level);
        this.boundEntityType = type;
        this.boundFacing = facingFromPlayer(player);
        this.setChanged();

        if (!isPowered()) {
            return true;
        }

        return spawnBoundEntity(level, type, spawnEggStack, player);
    }

    /** Rotates the bound mob (live or just remembered) 90 degrees clockwise. */
    public void rotateBoundMob(ServerLevel level) {
        if (this.boundEntityType == null) {
            return;
        }

        this.boundFacing = this.boundFacing.getClockWise();
        this.setChanged();

        if (this.boundEntityId == null) {
            return;
        }

        if (level.getEntity(this.boundEntityId) instanceof LivingEntity livingEntity) {
            applyFacing(livingEntity, this.boundFacing);
        }
    }

    /** Called when the dummy gains a redstone signal: spawns the remembered mob, if any. */
    public void onPowered(ServerLevel level) {
        if (this.boundEntityType != null && this.boundEntityId == null) {
            spawnBoundEntity(level, this.boundEntityType, null, null);
        }
    }

    /** Called when the dummy loses its redstone signal: removes the live mob but keeps the binding. */
    public void onUnpowered(ServerLevel level) {
        discardLiveEntity(level);
    }

    /** Removes both the live mob (if any) and the remembered binding entirely. */
    public void clearBoundEntity(ServerLevel level) {
        discardLiveEntity(level);
        this.boundEntityType = null;
        this.setChanged();
    }

    private void discardLiveEntity(ServerLevel level) {
        if (this.boundEntityId == null) {
            return;
        }

        Entity existing = level.getEntity(this.boundEntityId);
        if (existing != null) {
            existing.discard();
        }

        this.boundEntityId = null;
        this.setChanged();
    }

    /**
     * Spawns {@code type} as the real, live vanilla entity above this block (immobilized) so it
     * receives damage through the normal combat pipeline instead of a fake stand-in — that's what
     * makes per-mob damage modifiers (Smite, Bane of Arthropods, mob-category resistances, ...)
     * come out correct for free.
     */
    private boolean spawnBoundEntity(ServerLevel level, EntityType<?> type, @Nullable ItemStack spawnEggStack, @Nullable Player player) {
        BlockPos spawnPos = this.getBlockPos().above();
        Entity spawned = type.spawn(level, spawnEggStack, player, spawnPos, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
        if (!(spawned instanceof LivingEntity livingEntity)) {
            return false;
        }

        TargetDummyMarker.mark(livingEntity);
        applyFacing(livingEntity, this.boundFacing);
        if (livingEntity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setPersistenceRequired();
        }

        this.boundEntityId = livingEntity.getUUID();
        this.setChanged();
        return true;
    }

    private static void applyFacing(LivingEntity entity, Direction facing) {
        float yaw = facing.toYRot();
        entity.setYRot(yaw);
        entity.setYHeadRot(yaw);
        entity.setYBodyRot(yaw);
    }

    private Direction facingFromPlayer(Player player) {
        BlockPos pos = this.getBlockPos();
        int dx = (int) Math.round(player.getX() - (pos.getX() + 0.5));
        int dz = (int) Math.round(player.getZ() - (pos.getZ() + 0.5));
        return Direction.getNearest(dx, 0, dz, this.boundFacing);
    }

    private boolean isPowered() {
        BlockState state = this.getBlockState();
        return state.hasProperty(TargetDummyBlock.POWERED) && state.getValue(TargetDummyBlock.POWERED);
    }

    public Component describeBoundMob() {
        if (this.boundEntityType == null) {
            return Component.translatable(ModInfo.MOD_ID + ".target_dummy.unbound");
        }

        Component mobName = this.boundEntityType.getDescription();
        Component facingName = Component.translatable(ModInfo.MOD_ID + ".direction." + this.boundFacing.getSerializedName());

        if (this.boundEntityId == null) {
            return Component.translatable(ModInfo.MOD_ID + ".target_dummy.bound_unpowered", mobName, facingName);
        }

        return Component.translatable(ModInfo.MOD_ID + ".target_dummy.bound_facing", mobName, facingName);
    }

    // Fires while the block (and this instance) is still fully intact, before the chunk actually
    // removes it — unlike Block#affectNeighborsAfterRemoval, which by then can no longer look this
    // block entity back up via the level, so cleanup placed there silently never runs.
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.getLevel() instanceof ServerLevel serverLevel) {
            clearBoundEntity(serverLevel);
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public void loadAdditional(ValueInput data) {
        super.loadAdditional(data);
        this.boundEntityId = data.read("BoundEntity", UUIDUtil.CODEC).orElse(null);
        this.boundEntityType = data.read("BoundEntityType", EntityType.CODEC).orElse(null);
        this.boundFacing = data.read("BoundFacing", Direction.CODEC).orElse(Direction.NORTH);
    }

    @Override
    protected void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        if (this.boundEntityId != null) {
            data.store("BoundEntity", UUIDUtil.CODEC, this.boundEntityId);
        }
        if (this.boundEntityType != null) {
            data.store("BoundEntityType", EntityType.CODEC, this.boundEntityType);
        }
        data.store("BoundFacing", Direction.CODEC, this.boundFacing);
    }
}
