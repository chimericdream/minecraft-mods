package com.chimericdream.camelnostrils.client;

import com.chimericdream.camelnostrils.entity.ModEntities;
import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

public class CamelNostrilsClient {
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(
            ModEntities.FALLING_UPWARD_BLOCK_ENTITY,
            FallingUpwardBlockEntity.Renderer::new
        );
    }
}
