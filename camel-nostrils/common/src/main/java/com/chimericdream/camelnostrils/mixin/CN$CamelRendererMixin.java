package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.client.render.entity.state.CN$CamelRenderStateAccessor;
import com.chimericdream.camelnostrils.entity.CN$CamelAccessor;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CamelRenderer.class)
public abstract class CN$CamelRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/camel/Camel;Lnet/minecraft/client/renderer/entity/state/CamelRenderState;F)V", at = @At("TAIL"))
    private void cn$extractHasSnout(final Camel entity, final CamelRenderState state, final float partialTicks, final CallbackInfo ci) {
        ((CN$CamelRenderStateAccessor) state).cn$setHasSnout(((CN$CamelAccessor) entity).cn$hasSnout());
    }
}
