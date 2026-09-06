package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.block.UpsideDownBedRenderStateDuck;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BedRenderState.class)
public class CN$BedRenderStateMixin implements UpsideDownBedRenderStateDuck {
    @Unique
    private boolean camelnostrils$upsideDown = false;

    @Override
    public void camelnostrils$setUpsideDown(boolean upsideDown) {
        this.camelnostrils$upsideDown = upsideDown;
    }

    @Override
    public boolean camelnostrils$isUpsideDown() {
        return this.camelnostrils$upsideDown;
    }
}
