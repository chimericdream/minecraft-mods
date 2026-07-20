package com.chimericdream.minekea.fabric.client.render.block;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import com.chimericdream.minekea.fluid.HoneyFluid;
import com.chimericdream.minekea.fluid.MilkFluid;
import com.chimericdream.minekea.fluid.ModFluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

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

    // Explicitly-supported fluids: water, lava, milk, honey. Any other fluid renders as
    // water. These methods run in the render path, so they must never throw - an unhandled
    // fluid here crashes the client (a lava or water jar used to hard-crash on render).
    private static final Identifier WATER_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "block/water_still");
    private static final Identifier LAVA_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "block/lava_still");
    private static final int WATER_COLOR = 0x3F76E4; // default water tint (RGB; alpha is ignored)
    private static final int LAVA_COLOR = 0xFFFFFF;  // no tint - lava_still is already coloured

    protected int getFluidColor(Fluid fluid) {
        if (fluid == ModFluids.HONEY_FLUID.get() || fluid == ModFluids.FLOWING_HONEY.get()) {
            return HoneyFluid.ATTRIBUTES.getColor();
        }
        if (fluid == ModFluids.MILK_FLUID.get() || fluid == ModFluids.FLOWING_MILK.get()) {
            return MilkFluid.ATTRIBUTES.getColor();
        }
        if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
            return LAVA_COLOR;
        }
        return WATER_COLOR; // water + anything else
    }

    protected TextureAtlasSprite getFluidTexture(Fluid fluid) {
        return Minecraft.getInstance().getAtlasManager()
            .get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, getSourceTexture(fluid)));
    }

    private static Identifier getSourceTexture(Fluid fluid) {
        if (fluid == ModFluids.HONEY_FLUID.get() || fluid == ModFluids.FLOWING_HONEY.get()) {
            return HoneyFluid.ATTRIBUTES.getSourceTexture();
        }
        if (fluid == ModFluids.MILK_FLUID.get() || fluid == ModFluids.FLOWING_MILK.get()) {
            return MilkFluid.ATTRIBUTES.getSourceTexture();
        }
        if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
            return LAVA_TEXTURE;
        }
        return WATER_TEXTURE; // water + anything else
    }
}
