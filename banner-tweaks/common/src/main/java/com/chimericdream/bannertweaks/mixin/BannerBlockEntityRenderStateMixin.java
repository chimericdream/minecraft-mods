package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.client.render.block.entity.state.BannerBlockEntityRenderStateAccessor;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BannerRenderState.class)
public class BannerBlockEntityRenderStateMixin implements BannerBlockEntityRenderStateAccessor {
    @Unique
    public Component bt$customName;


    @Override
    public Component bt$getCustomName() {
        return bt$customName;
    }

    @Override
    public void bt$setCustomName(Component customName) {
        this.bt$customName = customName;
    }
}
