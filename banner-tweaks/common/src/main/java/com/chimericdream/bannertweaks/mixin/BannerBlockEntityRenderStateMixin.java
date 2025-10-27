package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.client.render.block.entity.state.BannerBlockEntityRenderStateAccessor;
import net.minecraft.client.render.block.entity.state.BannerBlockEntityRenderState;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BannerBlockEntityRenderState.class)
public class BannerBlockEntityRenderStateMixin implements BannerBlockEntityRenderStateAccessor {
    @Unique
    public Text bt$customName;


    @Override
    public Text bt$getCustomName() {
        return bt$customName;
    }

    @Override
    public void bt$setCustomName(Text customName) {
        this.bt$customName = customName;
    }
}
