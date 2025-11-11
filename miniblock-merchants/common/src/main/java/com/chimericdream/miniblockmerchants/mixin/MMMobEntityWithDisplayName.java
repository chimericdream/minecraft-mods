package com.chimericdream.miniblockmerchants.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
abstract public class MMMobEntityWithDisplayName {
    @Shadow
    abstract protected Text getDefaultName();
}
