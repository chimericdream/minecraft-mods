package com.chimericdream.camelnostrils.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntityRenderer.class)
public interface CN$LivingEntityRendererAccessor {
    @Accessor("model")
    void cn$setModel(EntityModel<?> model);
}
