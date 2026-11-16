package com.chimericdream.nextupdatenow.client;

import com.chimericdream.nextupdatenow.entity.ModEntities;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class NextUpdateNowClient {
    // NeoForge must call this while the mod is constructed. Any later lifecycle event is too late.
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(ModEntities.ICE_BALL, ThrownItemRenderer::new);
    }
}
