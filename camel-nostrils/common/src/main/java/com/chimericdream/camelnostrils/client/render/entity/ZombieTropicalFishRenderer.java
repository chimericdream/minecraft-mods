package com.chimericdream.camelnostrils.client.render.entity;

import com.chimericdream.camelnostrils.ModInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.resources.Identifier;

/**
 * A {@link TropicalFishRenderer} that keeps the vanilla tropical fish models/animation (both the small
 * and large body shapes) but swaps in the zombie tropical fish's own texture. Since that texture is a
 * single, already-colored image rather than vanilla's tintable pattern system, the vanilla pattern
 * overlay layer and its color tint are both dropped so they don't draw on top of it.
 */
public class ZombieTropicalFishRenderer extends TropicalFishRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/entity/fish/zombie_tropical_fish.png");

    public ZombieTropicalFishRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.layers.removeIf(layer -> layer instanceof TropicalFishPatternLayer);
    }

    @Override
    public Identifier getTextureLocation(TropicalFishRenderState state) {
        return TEXTURE;
    }

    @Override
    protected int getModelTint(TropicalFishRenderState state) {
        return -1;
    }
}
