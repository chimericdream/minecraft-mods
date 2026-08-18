package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.chimericdream.villagertweaks.entity.VT$VillagerAccessor;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractVillager.class)
public abstract class VTAbstractVillagerMixin implements VT$VillagerAccessor {
    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    private void vt$allowNitwitLeashing(CallbackInfoReturnable<Boolean> cir) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        VillagerData data = this.getVillagerData();

        if (config.enableNitwitLeashing && data != null && data.profession().is(VillagerProfession.NITWIT)) {
            cir.setReturnValue(true);
        }
    }
}
