package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PhantomRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Per-player phantom spawning. Vanilla reads {@code spawnPhantoms} once for the whole level, then loops
 * over the players, skipping spectators. So:
 *
 * <ul>
 *   <li>The level-wide read stays open if the world allows phantoms or any online player's override does.</li>
 *   <li>The spectator check, which runs per player, also skips any player the rule doesn't allow.</li>
 * </ul>
 */
@Mixin(PhantomSpawner.class)
public abstract class RFT$PhantomSpawnerMixin {
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$openForOverrides(GameRules rules, GameRule<?> rule, Operation<Object> original, @Local(argsOnly = true) ServerLevel level) {
        return PhantomRules.gateOpen(level, (Boolean) original.call(rules, rule));
    }

    @WrapOperation(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z")
    )
    private boolean rft$skipDisallowedPlayers(ServerPlayer player, Operation<Boolean> original) {
        return original.call(player) || !PhantomRules.allowedFor(player);
    }
}
