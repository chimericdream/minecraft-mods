package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.client.render.block.entity.state.ShulkerBoxRenderStateAccessor;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShulkerBoxRenderState.class)
public class ShulkerStuff$ShulkerBoxRenderStateMixin implements ShulkerBoxRenderStateAccessor {
    @Unique
    private boolean ss$useVanillaColor = true;

    @Unique
    private int ss$lidColor = -1;

    @Unique
    private int ss$baseColor = -1;

    @Override
    public boolean ss$useVanillaColor() {
        return ss$useVanillaColor;
    }

    @Override
    public void ss$setUseVanillaColor(boolean useVanillaColor) {
        this.ss$useVanillaColor = useVanillaColor;
    }

    @Override
    public int ss$getLidColor() {
        return ss$lidColor;
    }

    @Override
    public void ss$setLidColor(int lidColor) {
        this.ss$lidColor = lidColor;
    }

    @Override
    public int ss$getBaseColor() {
        return ss$baseColor;
    }

    @Override
    public void ss$setBaseColor(int baseColor) {
        this.ss$baseColor = baseColor;
    }
}
