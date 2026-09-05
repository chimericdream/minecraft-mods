package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.BeaconSectionAccessor;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BeaconBeamOwner.Section.class)
public class BCTweaksBeaconSectionMixin implements BeaconSectionAccessor {
    @Shadow
    private int height;

    @Unique
    private boolean bct$hidden = false;

    @Override
    public boolean bct$isHidden() {
        return bct$hidden;
    }

    @Override
    public void bct$setHidden(boolean hidden) {
        bct$hidden = hidden;
    }

    @Override
    public void bct$resetHeight() {
        this.height = 0;
    }
}
