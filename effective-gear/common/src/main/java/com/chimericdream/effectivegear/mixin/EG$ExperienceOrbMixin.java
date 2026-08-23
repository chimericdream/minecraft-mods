package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ExperienceOrb.class)
public class EG$ExperienceOrbMixin {
    @ModifyVariable(method = "award", at = @At("HEAD"), argsOnly = true, name = "amount")
    private static int eg$boostXpWithFullAmethystTrim(int amount, ServerLevel level, Vec3 pos) {
        if (amount <= 0) {
            return amount;
        }

        Player nearestPlayer = level.getNearestPlayer(pos.x, pos.y, pos.z, 6.0, false);
        if (nearestPlayer != null && TrimSetUtils.isWearingFullTrim(nearestPlayer, TrimMaterials.AMETHYST)) {
            return Math.max(amount + 1, Mth.ceil(amount * 1.05F));
        }

        return amount;
    }
}
