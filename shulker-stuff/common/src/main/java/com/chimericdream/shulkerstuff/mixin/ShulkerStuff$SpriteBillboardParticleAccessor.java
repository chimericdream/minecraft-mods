package com.chimericdream.shulkerstuff.mixin;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpriteBillboardParticle.class)
public abstract class ShulkerStuff$SpriteBillboardParticleAccessor extends ShulkerStuff$ParticleAccessor {
    @Shadow
    protected abstract void setSprite(Sprite sprite);
}
