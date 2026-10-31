package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.chimericdream.rulesforthee.rule.SubjectScope;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * Per-player rules read inside {@link ServerPlayer}:
 *
 * <ul>
 *   <li>{@code die}: whether the player's death is announced ({@code showDeathMessages}).</li>
 *   <li>{@code restoreFrom}: whether the respawned player is given the old inventory, XP and score back
 *       ({@code keepInventory}). In this method {@code this} is the new player; the UUID is the same.</li>
 *   <li>{@code hurtServer} and {@code isPvpAllowed}: {@code pvp}. Damage from one player to another needs
 *       PVP on for both of them. See {@link #rft$attackerHasPvpOff}.</li>
 * </ul>
 */
@Mixin(ServerPlayer.class)
public abstract class RFT$ServerPlayerMixin {
    @WrapOperation(
        method = {"die", "restoreFrom"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$perPlayerRule(GameRules rules, GameRule<?> rule, Operation<Object> original) {
        return PlayerRules.resolveUnchecked((ServerPlayer) (Object) this, rule, original.call(rules, rule));
    }

    /**
     * The respawn radius is read inside the static spawn finder, which isn't given the player, so open a
     * scope naming this player around the call. See {@link RFT$PlayerSpawnFinderMixin}.
     */
    @WrapOperation(
        method = "adjustSpawnLocation",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/PlayerSpawnFinder;findSpawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private CompletableFuture<Vec3> rft$spawnForThisPlayer(ServerLevel level, BlockPos suggestion, Operation<CompletableFuture<Vec3>> original) {
        try (SubjectScope ignored = SubjectScope.enter((ServerPlayer) (Object) this)) {
            return original.call(level, suggestion);
        }
    }

    /** The victim's half of {@code pvp}: {@code this} is the player being hurt. */
    @ModifyReturnValue(method = "isPvpAllowed", at = @At("RETURN"))
    private boolean rft$victimPvp(boolean worldValue) {
        return PlayerRules.resolve((ServerPlayer) (Object) this, GameRules.PVP, worldValue);
    }

    /**
     * The attacker's half of {@code pvp}. Vanilla only asks about the victim's world, so a player with PVP
     * off would still be able to hurt others. The attacker is found the way vanilla finds it: the damage
     * source's entity, or the owner of an arrow (which covers tridents). A player is never blocked from
     * hurting themself here.
     */
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void rft$attackerHasPvpOff(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getEntity();
        if (attacker instanceof AbstractArrow arrow) {
            attacker = arrow.getOwner();
        }

        if (attacker instanceof ServerPlayer player
            && player != (Object) this
            && !PlayerRules.resolve(player, GameRules.PVP, level.isPvpAllowed())) {
            cir.setReturnValue(false);
        }
    }
}
