package com.chimericdream.sneakytweaks.mixin;

import com.chimericdream.sneakytweaks.campfire.CampfireGraceHolder;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlock.class)
public class SneakyTweaks$CampfireBlockMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    protected void st$entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci) {
        if (
            state.getValue(CampfireBlock.LIT)
            && entity instanceof Player player
            && player.isCrouching()
            && SneakyTweaksConfig.HANDLER.instance().enableCampfireSneaking
            && ((CampfireGraceHolder) player).st$getCampfireGraceTicks() > 0
        ) {
            ci.cancel();
        }
    }
}
