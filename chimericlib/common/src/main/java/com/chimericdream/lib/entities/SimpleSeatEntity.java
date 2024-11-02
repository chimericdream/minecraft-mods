package com.chimericdream.lib.entities;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * A simple entity that does nothing but exist to be sat upon (aka "ridden").
 */
public class SimpleSeatEntity extends Entity {
    public SimpleSeatEntity(EntityType<? extends SimpleSeatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return new EntitySpawnS2CPacket(this, entityTrackerEntry);
    }

    @Override
    public void tick() {
        if (this.age > 20 && this.getPassengerList().isEmpty()) {
            this.kill();
        }
    }

    public static class EmptyRenderer extends EntityRenderer<SimpleSeatEntity> {
        public EmptyRenderer(EntityRendererFactory.Context ctx) {
            super(ctx);
        }

        @Override
        public boolean shouldRender(SimpleSeatEntity entity, Frustum frustum, double d, double e, double f) {
            return false;
        }

        @Override
        public Identifier getTexture(SimpleSeatEntity entity) {
            return null;
        }
    }
}
