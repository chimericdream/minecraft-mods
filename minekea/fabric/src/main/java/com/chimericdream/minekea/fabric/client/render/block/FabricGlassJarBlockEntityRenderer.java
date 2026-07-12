package com.chimericdream.minekea.fabric.client.render.block;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import com.chimericdream.minekea.fluid.HoneyFluid;
import com.chimericdream.minekea.fluid.MilkFluid;
import com.chimericdream.minekea.fluid.ModFluids;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

/*
 * As of 26.1, Fabric API's fluid rendering (net.fabricmc.fabric.api.client.render.fluid.v1) no longer
 * exposes a way to fetch a fluid's still sprite/tint color directly - FluidRenderHandler was reworked
 * into a render-callback interface with no sprite/color accessors, and the Transfer API's
 * FluidVariantRendering only exposes color, not sprites. Since this mod's own fluids (honey, milk) are
 * registered through Architectury's cross-platform ArchitecturyFluidAttributes (which already carries a
 * source texture + tint color), we read directly from there instead of going through Fabric's fluid
 * rendering registry, mirroring what NeoForgeGlassJarBlockEntityRenderer does with
 * IClientFluidTypeExtensions.
 */
@Environment(EnvType.CLIENT)
public class FabricGlassJarBlockEntityRenderer extends GlassJarBlockEntityRenderer {
    public FabricGlassJarBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
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
