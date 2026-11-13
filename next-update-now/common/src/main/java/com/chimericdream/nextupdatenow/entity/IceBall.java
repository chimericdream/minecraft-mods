package com.chimericdream.nextupdatenow.entity;

import com.chimericdream.nextupdatenow.item.ModItems;
import com.chimericdream.nextupdatenow.sound.ModSounds;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class IceBall extends ThrowableItemProjectile {
    public static final float MAX_DAMAGE = 4.0F;
    // The speed at which a thrown ice ball deals the full damage.
    public static final double FULL_DAMAGE_SPEED = 1.5D;

    private static final byte HIT_EVENT = 3;

    public IceBall(EntityType<? extends IceBall> type, Level level) {
        super(type, level);
    }

    public IceBall(Level level, LivingEntity owner, ItemStack stack) {
        super(ModEntities.ICE_BALL.get(), owner, level, stack);
    }

    public IceBall(Level level, double x, double y, double z, ItemStack stack) {
        super(ModEntities.ICE_BALL.get(), x, y, z, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ICE_BALL.get();
    }

    private ParticleOptions getParticle() {
        ItemStack stack = getItem();
        return stack.isEmpty()
            ? ParticleTypes.ITEM_SNOWBALL
            : new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(stack));
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == HIT_EVENT) {
            ParticleOptions particle = getParticle();
            for (int i = 0; i < 8; i++) {
                level().addParticle(particle, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        Entity target = hit.getEntity();
        float scale = (float) Math.min(1.0D, getDeltaMovement().length() / FULL_DAMAGE_SPEED);
        target.hurt(damageSources().thrown(this, getOwner()), MAX_DAMAGE * scale);
    }

    @Override
    protected void onHit(HitResult hit) {
        super.onHit(hit);
        if (!level().isClientSide()) {
            level().broadcastEntityEvent(this, HIT_EVENT);
            level().playSound(null, getX(), getY(), getZ(), ModSounds.ICE_BALL_BREAK.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
            discard();
        }
    }
}
