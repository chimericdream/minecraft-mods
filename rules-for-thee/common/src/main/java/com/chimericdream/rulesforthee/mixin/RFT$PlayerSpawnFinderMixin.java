package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.chimericdream.rulesforthee.rule.SubjectScope;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The respawn radius. {@code PlayerSpawnFinder.findSpawn} is static and isn't given the player, so
 * {@link RFT$ServerPlayerMixin} opens a {@link SubjectScope} around the call and this reads the radius for
 * that player. With no scope open (some other caller) the world's value is used.
 */
@Mixin(PlayerSpawnFinder.class)
public abstract class RFT$PlayerSpawnFinderMixin {
    @WrapOperation(
        method = "findSpawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private static Object rft$perPlayerRadius(GameRules rules, GameRule<?> rule, Operation<Object> original) {
        Object world = original.call(rules, rule);

        return SubjectScope.current()
            .map(player -> PlayerRules.resolveUnchecked(player, rule, world))
            .orElse(world);
    }
}
