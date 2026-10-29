package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.chimericdream.rulesforthee.rule.SubjectScope;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

/**
 * Per-player rules read inside {@link ServerPlayer}:
 *
 * <ul>
 *   <li>{@code die}: whether the player's death is announced ({@code showDeathMessages}).</li>
 *   <li>{@code restoreFrom}: whether the respawned player is given the old inventory, XP and score back
 *       ({@code keepInventory}). In this method {@code this} is the new player; the UUID is the same.</li>
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
}
