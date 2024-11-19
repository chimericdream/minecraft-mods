package com.chimericdream.shulkerstuff.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Particle.class)
public class ShulkerStuff$ParticleAccessor {
    @Shadow
    protected float red;
    @Shadow
    protected float green;
    @Shadow
    protected float blue;
}
