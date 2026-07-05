package com.chimericdream.minekea.neoforge.client.render.block;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;

public class NeoForgeGlassJarBlockEntityRenderer extends GlassJarBlockEntityRenderer {
    public NeoForgeGlassJarBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    protected int getFluidColor(Fluid fluid) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        return renderProperties.getTintColor(new FluidStack(fluid, 1000));
    }

    protected TextureAtlasSprite getFluidTexture(Fluid fluid) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation sprite = renderProperties.getStillTexture();
        return FluidSpriteCache.getSprite(sprite);
    }
}
