package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillager.class)
public abstract class VTZombieVillagerEntityMixin extends Entity {
    private Gson gson = new Gson();

    @Shadow
    private int conversionTimer;

    @Shadow
    public abstract boolean isConverting();

    public VTZombieVillagerEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Unique
    private Component prevName = null;

    @Unique
    private boolean wasPrevNameVisible = false;

    @Unique
    private boolean isTimerShowing = false;

    @ModifyArg(
        method = "interactMob",
        at = @At(value = "INVOKE", target = "net/minecraft/entity/mob/ZombieVillagerEntity.setConverting(Ljava/util/UUID;I)V"),
        index = 1
    )
    private int vt$modifyConversionTime(int time) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (config.enableConversionTimeOverride) {
            return config.conversionTime;
        }

        return time;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void vt$onTick(CallbackInfo ci) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (!this.isConverting() || !config.displayConversionTime || this.conversionTimer <= 0) {
            return;
        }

        int secondsLeft = this.conversionTimer / 20;
        if (isTimerShowing) {
            this.setCustomName(vt$getFormattedTime(secondsLeft));
        } else {
            if (this.hasCustomName()) {
                this.prevName = this.getCustomName();
                this.wasPrevNameVisible = this.isCustomNameVisible();
            }

            this.setCustomName(vt$getFormattedTime(secondsLeft));

            this.setCustomNameVisible(true);
            this.isTimerShowing = true;
        }
    }

    @Inject(method = "finishConversion", at = @At("HEAD"))
    private void vt$onFinishConversion(CallbackInfo ci) {
        if (this.prevName != null) {
            this.setCustomName(this.prevName);
            this.setCustomNameVisible(this.wasPrevNameVisible);
        } else {
            this.setCustomName(null);
        }

        this.isTimerShowing = false;
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void vt$writePreviousNameData(ValueOutput view, CallbackInfo ci) {

        if (this.prevName != null) {
            String json = this.gson.toJson(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, this.prevName).getOrThrow());
            view.putString("VTPrevName", json);
            view.putBoolean("VTWasPrevNameVisible", this.wasPrevNameVisible);
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void vt$readPreviousNameData(ValueInput view, CallbackInfo ci) {
        if (view.contains("VTPrevName")) {
            String jsonString = view.getStringOr("VTPrevName", "Uh oh!");

            this.prevName = ComponentSerialization.CODEC
                .decode(JsonOps.INSTANCE, this.gson.fromJson(jsonString, JsonElement.class))
                .getOrThrow()
                .getFirst();

            this.wasPrevNameVisible = view.getBooleanOr("VTWasPrevNameVisible", false);
            this.isTimerShowing = true;
        }
    }

    @Unique
    private Component vt$getFormattedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds - (minutes * 60);

        return Component.nullToEmpty(minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
    }
}
