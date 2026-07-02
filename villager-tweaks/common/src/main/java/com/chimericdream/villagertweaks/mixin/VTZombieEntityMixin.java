package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Zombie.class)
public class VTZombieEntityMixin {
    @Redirect(
        method = "onKilledOther",
        at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.getDifficulty()Lnet/minecraft/world/Difficulty;")
    )
    private Difficulty modifyConversionTime(ServerLevel world) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (config.forceVillagerConversion) {
            return Difficulty.HARD;
        }

        return world.getDifficulty();
    }
}
