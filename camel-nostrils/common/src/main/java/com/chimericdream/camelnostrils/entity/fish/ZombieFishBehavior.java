package com.chimericdream.camelnostrils.entity.fish;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Shared out-of-water AI for {@link ZombieSalmon}, {@link ZombieCod}, and {@link ZombieTropicalFish}.
 * Pulled out as static helpers (rather than a common superclass) because each of those three extends a
 * different vanilla fish class, so Java's single inheritance rules out sharing this via a base class.
 */
public final class ZombieFishBehavior {
    public static final float TAIL_SLAP_DAMAGE = 1.0F;
    public static final int POISON_DURATION_TICKS = 80;
    private static final int ATTACK_COOLDOWN_TICKS = 20;
    private static final double ATTACK_RANGE_SQR = 1.5 * 1.5;
    private static final double FLOP_STEER_STRENGTH = 0.025;
    private static final double SWIM_SPEED = 1.0;

    private ZombieFishBehavior() {
    }

    /**
     * Nudges the fish's flopping horizontally toward its target every tick it's out of water, on top of
     * whatever random flop-jump the vanilla {@code AbstractFish.aiStep()} logic already did this tick.
     */
    public static void steerFlopTowardTarget(Mob fish) {
        LivingEntity target = fish.getTarget();
        if (target == null || fish.isInWater()) {
            return;
        }

        Vec3 toTarget = target.position().subtract(fish.position());
        double horizontalDistSqr = toTarget.x * toTarget.x + toTarget.z * toTarget.z;
        if (horizontalDistSqr < 1.0E-4) {
            return;
        }

        double horizontalDist = Math.sqrt(horizontalDistSqr);
        Vec3 steer = new Vec3(toTarget.x / horizontalDist, 0.0, toTarget.z / horizontalDist).scale(FLOP_STEER_STRENGTH);
        fish.setDeltaMovement(fish.getDeltaMovement().add(steer));
    }

    /**
     * These fish skip {@code AbstractFish.registerGoals()} entirely (see the class docs on
     * {@link ZombieCod}/{@link ZombieSalmon}/{@link ZombieTropicalFish}), so they never get the
     * vanilla {@code FishSwimGoal} that normally drives {@code FishMoveControl} while submerged.
     * Without it, a zombie fish that ends up back in water just floats in place instead of chasing —
     * this re-issues a swim-to-target path whenever the current one has finished or gone stale, which
     * is enough for the mob's own {@code navigation.tick()}/{@code moveControl.tick()} (still ticking
     * normally every frame) to carry it the rest of the way.
     */
    public static void swimTowardTarget(Mob fish) {
        LivingEntity target = fish.getTarget();
        if (target == null || !fish.isInWater()) {
            return;
        }

        if (fish.getNavigation().isDone()) {
            fish.getNavigation().moveTo(target, SWIM_SPEED);
        }
    }

    /**
     * Ticks down the attack cooldown and, once it's up and the target is in reach, lands a tail slap:
     * a small hit of damage plus a few seconds of poison. Returns the new cooldown value to store.
     */
    public static int tickTailSlapAttack(Mob fish, int cooldown, ServerLevel level) {
        if (cooldown > 0) {
            return cooldown - 1;
        }

        LivingEntity target = fish.getTarget();
        if (target == null || !target.isAlive() || fish.distanceToSqr(target) > ATTACK_RANGE_SQR) {
            return 0;
        }

        fish.swing(InteractionHand.MAIN_HAND);
        if (target.hurtServer(level, fish.damageSources().mobAttack(fish), TAIL_SLAP_DAMAGE)) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS));
        }

        return ATTACK_COOLDOWN_TICKS;
    }
}
