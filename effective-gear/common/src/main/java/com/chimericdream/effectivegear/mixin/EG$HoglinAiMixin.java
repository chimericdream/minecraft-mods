package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoglinAi.class)
public class EG$HoglinAiMixin {
    @Inject(method = "findNearestValidAttackTarget", at = @At("RETURN"), cancellable = true)
    private static void eg$ignorePlayersWearingFullSnoutTrim(
        ServerLevel level,
        Hoglin body,
        CallbackInfoReturnable<Optional<? extends LivingEntity>> cir
    ) {
        Optional<? extends LivingEntity> target = cir.getReturnValue();
        if (target.isPresent() && target.get() instanceof Player player && TrimSetUtils.isWearingFullPattern(player, TrimPatterns.SNOUT)) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
