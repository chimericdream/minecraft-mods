package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBruteAi.class)
public class EG$PiglinBruteAiMixin {
    // findNearestValidAttackTarget also supplies the ANGRY_AT retaliation target (set when the brute
    // is hit), not just the passive idle-scan pick - unconditionally nulling out a snout-trim player's
    // result here would silence a legitimate "I was attacked" target just as readily as an unprovoked
    // one, leaving the brute permanently passive even after the player attacks it directly.
    @Inject(method = "findNearestValidAttackTarget", at = @At("RETURN"), cancellable = true)
    private static void eg$dontAutomaticallyAttackPlayersWithSnout(
        ServerLevel level,
        AbstractPiglin body,
        CallbackInfoReturnable<Optional<? extends LivingEntity>> cir
    ) {
        Optional<? extends LivingEntity> target = cir.getReturnValue();
        if (target.isEmpty() || !(target.get() instanceof Player player) || !TrimSetUtils.isWearingFullPattern(player, TrimPatterns.SNOUT)) {
            return;
        }

        Optional<LivingEntity> angryAt = BehaviorUtils.getLivingEntityFromUUIDMemory(body, MemoryModuleType.ANGRY_AT);
        if (angryAt.isPresent() && angryAt.get() == player) {
            return;
        }

        cir.setReturnValue(Optional.empty());
    }
}
