package com.chimericdream.sneakytweaks.mixin;

import com.chimericdream.sneakytweaks.campfire.CampfireGraceHolder;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class SneakyTweaks$PlayerMixin implements CampfireGraceHolder {
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void st$defineCampfireGraceData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(CampfireGraceHolder.ST$CAMPFIRE_GRACE_TICKS, SneakyTweaksConfig.HANDLER.instance().campfireGraceTicks);
    }

    @Override
    public int st$getCampfireGraceTicks() {
        return ((Player) (Object) this).getEntityData().get(CampfireGraceHolder.ST$CAMPFIRE_GRACE_TICKS);
    }

    @Override
    public void st$setCampfireGraceTicks(int ticks) {
        ((Player) (Object) this).getEntityData().set(CampfireGraceHolder.ST$CAMPFIRE_GRACE_TICKS, ticks);
    }
}
