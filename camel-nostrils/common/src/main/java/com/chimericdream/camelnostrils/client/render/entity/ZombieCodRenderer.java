package com.chimericdream.camelnostrils.client.render.entity;

import com.chimericdream.camelnostrils.ModInfo;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * A {@link CodRenderer} that keeps the vanilla cod model/animation but swaps in the zombie cod's own
 * texture.
 */
public class ZombieCodRenderer extends CodRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/entity/fish/zombie_cod.png");

    public ZombieCodRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
