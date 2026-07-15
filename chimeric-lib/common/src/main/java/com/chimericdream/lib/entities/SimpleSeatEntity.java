package com.chimericdream.lib.entities;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

/**
 * A simple entity that does nothing but exist to be sat upon (aka "ridden").
 */
public class SimpleSeatEntity extends Entity {
    public SimpleSeatEntity(EntityType<? extends SimpleSeatEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput data) {
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput data) {
    }

    @Override
    public @NonNull Packet<ClientGamePacketListener> getAddEntityPacket(@NonNull ServerEntity entityTrackerEntry) {
        return new ClientboundAddEntityPacket(this, entityTrackerEntry);
    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel world) {
            if (this.tickCount > 20 && this.getPassengers().isEmpty()) {
                this.kill(world);
            }
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel world, @NonNull DamageSource source, float amount) {
        return false;
    }

    public static class EmptyRenderer extends EntityRenderer<SimpleSeatEntity, EntityRenderState> {
        public EmptyRenderer(EntityRendererProvider.Context ctx) {
            super(ctx);
        }

        @Override
        public boolean shouldRender(@NonNull SimpleSeatEntity entity, @NonNull Frustum frustum, double d, double e, double f) {
            return false;
        }

        @Override
        public @NonNull EntityRenderState createRenderState() {
            return new EntityRenderState();
        }
    }
}
