package com.chimericdream.camelnostrils.client.render.entity;

import com.chimericdream.camelnostrils.ModInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.client.renderer.entity.state.SalmonRenderState;
import net.minecraft.resources.Identifier;

/**
 * A {@link SalmonRenderer} that keeps the vanilla salmon model/animation but swaps in the zombie
 * salmon's own texture.
 */
public class ZombieSalmonRenderer extends SalmonRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/entity/fish/zombie_salmon.png");

    public ZombieSalmonRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(SalmonRenderState state) {
        return TEXTURE;
    }
}
