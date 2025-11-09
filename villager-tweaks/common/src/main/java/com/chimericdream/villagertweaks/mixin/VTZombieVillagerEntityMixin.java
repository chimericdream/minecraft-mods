package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillagerEntity.class)
public abstract class VTZombieVillagerEntityMixin extends Entity {
    private Gson gson = new Gson();

    @Shadow
    private int conversionTimer;

    @Shadow
    public abstract boolean isConverting();

    public VTZombieVillagerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private Text prevName = null;

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
    private void vt$writePreviousNameData(WriteView view, CallbackInfo ci) {

        if (this.prevName != null) {
            String json = this.gson.toJson(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, this.prevName).getOrThrow());
            view.putString("VTPrevName", json);
            view.putBoolean("VTWasPrevNameVisible", this.wasPrevNameVisible);
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void vt$readPreviousNameData(ReadView view, CallbackInfo ci) {
        if (view.contains("VTPrevName")) {
            String jsonString = view.getString("VTPrevName", "Uh oh!");

            this.prevName = TextCodecs.CODEC
                .decode(JsonOps.INSTANCE, this.gson.fromJson(jsonString, JsonElement.class))
                .getOrThrow()
                .getFirst();

            this.wasPrevNameVisible = view.getBoolean("VTWasPrevNameVisible", false);
            this.isTimerShowing = true;
        }
    }

    @Unique
    private Text vt$getFormattedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds - (minutes * 60);

        return Text.of(minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
    }
}
