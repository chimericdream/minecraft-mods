package com.chimericdream.minekea.fabric.client.render.block;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;

@Environment(EnvType.CLIENT)
public class FabricGlassJarBlockEntityRenderer extends GlassJarBlockEntityRenderer {
    public FabricGlassJarBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    protected int getFluidColor(Fluid fluid) {
        FluidRenderHandler renderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);

        return renderHandler.getFluidColor(null, null, fluid.defaultFluidState());
    }

    protected TextureAtlasSprite getFluidTexture(Fluid fluid) {
        FluidRenderHandler renderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        TextureAtlasSprite[] sprites = renderHandler.getFluidSprites(null, null, fluid.defaultFluidState());

        return sprites[0];
    }
}
