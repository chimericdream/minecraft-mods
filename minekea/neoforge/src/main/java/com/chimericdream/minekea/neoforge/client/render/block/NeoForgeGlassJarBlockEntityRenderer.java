package com.chimericdream.minekea.neoforge.client.render.block;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import com.chimericdream.minekea.fluid.HoneyFluid;
import com.chimericdream.minekea.fluid.MilkFluid;
import com.chimericdream.minekea.fluid.ModFluids;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

/*
 * As of 26.1, NeoForge's IClientFluidTypeExtensions no longer exposes getTintColor()/getStillTexture()
 * (it was trimmed down to just fog/overlay rendering hooks as part of the same rendering overhaul that
 * removed the equivalent accessors from Fabric's fluid rendering API - see
 * FabricGlassJarBlockEntityRenderer). Since this mod's own fluids (honey, milk) are registered through
 * Architectury's cross-platform ArchitecturyFluidAttributes (which already carries a source texture +
 * tint color), we read directly from there instead, matching the Fabric implementation.
 */
public class NeoForgeGlassJarBlockEntityRenderer extends GlassJarBlockEntityRenderer {
    public NeoForgeGlassJarBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    protected int getFluidColor(Fluid fluid) {
        return getAttributes(fluid).getColor();
    }

    protected TextureAtlasSprite getFluidTexture(Fluid fluid) {
        Identifier sprite = getAttributes(fluid).getSourceTexture();

        return Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, sprite));
    }

    private static ArchitecturyFluidAttributes getAttributes(Fluid fluid) {
        if (fluid == ModFluids.HONEY_FLUID.get() || fluid == ModFluids.FLOWING_HONEY.get()) {
            return HoneyFluid.ATTRIBUTES;
        }

        if (fluid == ModFluids.MILK_FLUID.get() || fluid == ModFluids.FLOWING_MILK.get()) {
            return MilkFluid.ATTRIBUTES;
        }

        throw new IllegalArgumentException("Unknown fluid in glass jar: " + fluid);
    }
}
