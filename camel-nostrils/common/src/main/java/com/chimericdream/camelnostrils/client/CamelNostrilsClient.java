package com.chimericdream.camelnostrils.client;

import com.chimericdream.camelnostrils.entity.ModEntities;
import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

public class CamelNostrilsClient {
    // NeoForge must call this during mod construction (see CamelNostrilsNeoForge's constructor) - any
    // lifecycle event is too late, since architectury's own RegisterRenderers listener fires on its bus
    // before ours.
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(
            ModEntities.FALLING_UPWARD_BLOCK_ENTITY,
            FallingUpwardBlockEntity.Renderer::new
        );
    }
}
