package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.client.render.entity.state.CN$CamelRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CamelRenderState.class)
public class CN$CamelRenderStateMixin implements CN$CamelRenderStateAccessor {
    @Unique
    private boolean cn$hasSnout = true;

    @Override
    public boolean cn$hasSnout() {
        return this.cn$hasSnout;
    }

    @Override
    public void cn$setHasSnout(final boolean hasSnout) {
        this.cn$hasSnout = hasSnout;
    }
}
