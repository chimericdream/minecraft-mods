package com.chimericdream.camelnostrils.entity.fish;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A cod that flopped its last flop while leashed out of water — see {@link ZombieFishConverter}.
 * Shares its look and sounds with a regular cod; only its behavior (hostile, 1 heart of health, immune
 * to drowning/sunlight) is different. See {@link ZombieFishBehavior} for the AI shared with
 * {@link ZombieSalmon} and {@link ZombieTropicalFish}.
 */
public class ZombieCod extends Cod {
    private int attackCooldown = 0;

    public ZombieCod(EntityType<? extends ZombieCod> type, Level level) {
        super(type, level);
    }

    public static Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2.0);
    }

    @Override
    protected void registerGoals() {
        // Deliberately not calling super — the vanilla schooling/panic/flee-the-player goals don't make
        // sense for a hostile fish that's stuck flopping on land.
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            ZombieFishBehavior.steerFlopTowardTarget(this);
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        this.attackCooldown = ZombieFishBehavior.tickTailSlapAttack(this, this.attackCooldown, level);
    }

    @Override
    protected void handleAirSupply(ServerLevel level, int preTickAirSupply) {
        // Undead — doesn't need to breathe, so it never drowns from flopping around out of water.
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Hostile now — no more scooping it into a bucket like a docile fish.
        return InteractionResult.PASS;
    }
}
